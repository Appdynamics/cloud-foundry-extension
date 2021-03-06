/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appdynamics.extensions.cloudfoundry.conf.Configuration;
import com.appdynamics.extensions.cloudfoundry.conf.MatchPatternConfig;
import com.appdynamics.extensions.cloudfoundry.model.JmxConnectorObject;
import com.appdynamics.extensions.cloudfoundry.utils.CfConstants;
import com.appdynamics.extensions.cloudfoundry.utils.CfUtility;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;

/**
 * @author ashish mehta
 *
 * This extension pulls Cloud Foundary Pivotal metrics via JMS service call 
 * to post them to AppD controller via machine agent
 * 
 */
public class CloudFoundryExtension extends AManagedMonitor{

	private static Logger logger = LoggerFactory.getLogger(CloudFoundryExtension.class);

	private Configuration config;
	private JMXServiceURL jmxServiceUrl;
	private ExecutorService executorService;
	
	private Integer currentThreadId = 1;
	private Integer maxThreads = 0;
	private boolean startThreads = true;
	private volatile boolean initialized = false;
	private boolean refreshThreadStarted = false;

	private Map<Integer, List<ObjectName>> mbeanDomains;
	private static Map<String, Integer> substituteVsNameCountMap = new ConcurrentHashMap<String, Integer>();
	private static Map<String, String> regexMatchedNameVsSubstituteNameMap = new ConcurrentHashMap<String, String>();

	public CloudFoundryExtension (){
		String msg = "Using Monitor Version [" + getImplementationVersion() + "]";
		logger.info(msg);
		System.out.println(msg);
	}

	/**
	 * This method is called by machine agent periodically (every minute)
	 * - Populates the mbeans into map only once during initialization
	 * - Creates and starts a thread pool to fetch attributes-values for mbeans in map.
	 * 
	 */
	public TaskOutput execute(Map<String, String> argsMap, TaskExecutionContext executionContext) throws TaskExecutionException {

		if(argsMap == null || argsMap.isEmpty()){
			logger.error("Task Arguments Passed is null, exiting program");
			return new TaskOutput("Task Arguments Passed is null, exiting program.");
		}

		logger.info("Task Arguments Passed {}", argsMap);

		TaskOutput out = null;

		if(!this.initialized){
			logger.info("CloudFoundary JMX initializing....");
			try{
				File confFile = CfUtility.getConfigFile(argsMap.get(CfConstants.CONFIG_FILE));
				logger.info("Conf File location = {}", confFile);

				if(confFile != null){
					Configuration config = Configuration.read(confFile);
					if (config != null) {
						this.config = config;
						this.maxThreads = config.getJmxService().getMaxParallelConnection();
						try{
							this.jmxServiceUrl =  new JMXServiceURL(config.getJmxService().getJmxServiceUrl());
						}catch(Exception exp){
							logger.error("Error occured while connecting to JMXServiceUrl", exp); 		
							out = new TaskOutput("Error occured while connecting to JMXServiceUrl.");
							return out;
						}
						if(this.populateMbeanDomains()){
							this.initialized = true;
							out = new TaskOutput("CloudFoundary JMX Initilization successful.");
							logger.info("CloudFoundary JMX Initilization successful....");
						}else{
							out = new TaskOutput("CloudFoundary JMX Initilization failed.");
							logger.info("CloudFoundary JMX Initilization failed....");
						}
					} else {
						logger.error("Config could not be loaded, exiting program");         	
					}
				}
			}catch(Exception exp){
				logger.error("Error occurred", exp);
				out = new TaskOutput("Error occurred while executing task");
				return out;
			}
			
		}else{
			logger.info("CloudFoundary JMX already initialized....");
			out = new TaskOutput("CloudFoundary JMX already initialized.");
		}

		if(this.startThreads){
			this.startJMXThreads();
		}else{
			logger.debug("Not running any threads as startThreads = {}", this.startThreads);
		}

		this.startDomainRefreshingThread(this);

		return out;
	}

	/**
	 * Populates mbeans map for JMX domains provided in config file
	 * Adds required (or other than to be ignored) mbeans into map.
	 * Map has keys as integer and values as List of mbeans
	 * Each list is traversed by a thread to fetch attributes and values for the mbeans
	 * 
	 */
	private boolean populateMbeanDomains(){	
		Integer maxConnections = this.config.getJmxService().getMaxParallelConnection();
		JmxConnectorObject jmxConn = null;
		try{			
			if (this != null){
				jmxConn = this.jmxOpenConnection();

				MBeanServerConnection mbsc = jmxConn.getMbsc();
				String[] names = mbsc.getDomains();

				logger.debug("Domain names size {}", names.length);

				if (mbeanDomains == null)
					mbeanDomains = new HashMap<Integer, List<ObjectName>>();
				else
					mbeanDomains.clear();
				
				Integer conNum = 1;

				for (String domain : names) {
					if(config.getDomains().contains(domain)){
						logger.debug("Contains domain {}", domain);
						Set<?> objectNames = mbsc.queryNames(new ObjectName(domain + ":*"), null);
						for (Object mxbeanName : objectNames) {
							if(mxbeanName instanceof ObjectName){
								ObjectName mbeanObject = (ObjectName)mxbeanName;

								String deploymentValue = mbeanObject.getKeyProperty(CfConstants.DEPLOYMENT);
								
								logger.debug("deploymentValue: {}", deploymentValue);
								
								if(config.getDeploymentsConfig().getRequiredOrIgnoredDeployments() == CfConstants.REQUIRED){
									if(!config.getDeploymentsConfig().getDeploymentNames().contains(deploymentValue) && !isRegexPatternMatch(config.getDeploymentsConfig().getDeploymentsMatchPatterns(), deploymentValue)){
										logger.debug("Not required mbean = {} with deployment = {}", mbeanObject.toString(), deploymentValue);
										continue;
									}
								}else{
									if(config.getDeploymentsConfig().getDeploymentNames().contains(deploymentValue)){
										logger.debug("Ignoring mbean object = {} with deployment = {}", mbeanObject.toString(), deploymentValue);
										continue;
									}
								}

								
								String jobValue = mbeanObject.getKeyProperty(CfConstants.JOB);
								
								if(config.getJobsConfig().getRequiredOrIgnoredJobs() == CfConstants.REQUIRED){
									if(!config.getJobsConfig().getJobNames().contains(jobValue) && !isRegexPatternMatch(config.getJobsConfig().getJobMatchPatterns(), jobValue)){
										logger.debug("Not required mbean = {} with job = {}", mbeanObject.toString(), jobValue);
										continue;
									}
								}else{
									if(config.getJobsConfig().getJobNames().contains(jobValue)){
										logger.debug("Ignoring mbean object = {} with job = {}", mbeanObject.toString(), jobValue);
										continue;
									}
								}								

								List<ObjectName> domainsList = mbeanDomains.get(conNum);
								if(domainsList == null){
									domainsList = new ArrayList<ObjectName>();
									mbeanDomains.put(conNum, domainsList);
								}

								logger.debug("Adding mbeanObject [{}] to Map with key = {}", mbeanObject, conNum);

								domainsList.add(mbeanObject);
								conNum++;
								if(conNum > maxConnections)
									conNum = 1;
							}
						}
					}
				}	

				if(mbeanDomains.isEmpty()){
					this.startThreads = false;
					logger.debug("No CloudFoundry Deployments/Jobs found to be monitored. Please check required/ignored deployments/jobs section in config.yaml");
				}
			}
			return true;
		}catch(Exception ex){
			logger.error("Exception Occurred", ex);	
			return false;
		}finally{
			this.jmxConnectionClose(jmxConn);
		}
	}
	
	/**
	 * Matches the input text with the given match patterns
	 * 
	 * @param matchPatterns
	 * @param text
	 * @return boolean
	 */
	private static boolean isRegexPatternMatch(List<MatchPatternConfig> matchPatterns, String text){
		if(matchPatterns == null || matchPatterns.isEmpty())
			return false;
         
		boolean matched = false;
		for(MatchPatternConfig matchPatternConfig : matchPatterns){
			if (CfUtility.isRegexMatched(matchPatternConfig.getPattern(), text)){
				matched = true;
				break;
			}
		}
		return matched;
	}
	
	/**
	 * Returns the name for the given text based on the match patterns
	 * If given text doesn't match return the text itself
	 * If given text matches:
	 *    - fetch the name from regexMatchedNameVsSubstitutedNameMap if available
	 *    - if not available then name would be substituteName (or substituteName-count)
	 *    - count as per substituteVsNameCountMap
	 * 
	 * @param matchPatterns
	 * @param text
	 * @return RegexMatchedName
	 */
	private static String regexPatternMatchedName(List<MatchPatternConfig> matchPatterns, String text){
		if(matchPatterns == null || matchPatterns.isEmpty())
			return text;
		
		String returnName = text;
		for(MatchPatternConfig matchPatternConfig : matchPatterns){
			if (CfUtility.isRegexMatched(matchPatternConfig.getPattern(), text)){
				if(matchPatternConfig.getSubstituteName() != null){
					String savedName = regexMatchedNameVsSubstituteNameMap.get(text);
					if(savedName == null) {
						Integer currentCount = substituteVsNameCountMap.get(matchPatternConfig.getSubstituteName());
						if (currentCount != null){
							savedName = matchPatternConfig.getSubstituteName() + "-" + currentCount;
							substituteVsNameCountMap.put(matchPatternConfig.getSubstituteName(), ++currentCount);
						}else{
							substituteVsNameCountMap.put(matchPatternConfig.getSubstituteName(), 1);
							savedName = matchPatternConfig.getSubstituteName();
						}
						returnName = savedName;
						regexMatchedNameVsSubstituteNameMap.put(text, returnName);
					}else{
						returnName = savedName;
					}
				}
			}
		}

		return returnName;
		
	}

	/**
	 * Starts a thread pool with set (or calculated) number of threads to
	 * fetch the mBean data concurrently.
	 * Shutdown the thread pool once all threads tasks are done.
	 * 
	 */
	private void startJMXThreads(){
		
		Integer maxThreads = this.config.getJmxService().getMaxParallelConnection();

		this.maxThreads = (mbeanDomains.size() > maxThreads? maxThreads : mbeanDomains.size());
		executorService = Executors.newFixedThreadPool(maxThreads);

		logger.info("Concurrent threads running for JMX calls = {}", this.maxThreads);

		for(int i= 0; i< this.maxThreads; i++){
			JMXThread jmxTh = this.new JMXThread();
			jmxTh.cfExt = this;
			executorService.execute(jmxTh);
		}

		if(executorService != null){
			executorService.shutdown();
			try{
				if(!executorService.awaitTermination(60, TimeUnit.SECONDS)){
					logger.debug("Tasks did not finish in 60 seconds, shutting down all threads.");
					executorService.shutdownNow();
				}
			}catch(InterruptedException ie){
					executorService.shutdownNow();
				}
		}
			
	}

	/**
	 * Method to open/create connection to JMX service
	 */
	private JmxConnectorObject jmxOpenConnection() throws Exception{
		JMXConnector jmxConnector = null;

		if(this.config.getJmxService().getAuthenticate() == 1){
			Map<String, String[]> env = new HashMap<String, String[]>();		
			String[] credentials = {this.config.getJmxService().getUsername(), this.config.getJmxService().getPassword()};
			env.put(JMXConnector.CREDENTIALS, credentials);

			jmxConnector = JMXConnectorFactory.connect(this.jmxServiceUrl, env);
		}else 
			jmxConnector = JMXConnectorFactory.connect(this.jmxServiceUrl, null);

		MBeanServerConnection mbsc =  jmxConnector.getMBeanServerConnection();

		JmxConnectorObject jmxConn = new JmxConnectorObject();
		jmxConn.setJmxConnector(jmxConnector);
		jmxConn.setMbsc(mbsc);

		return jmxConn;
	}

	/**
	 * Method to close connection to JMX service once thread tasks are done.
	 */
	private void jmxConnectionClose(JmxConnectorObject jmxConn) {
		if(jmxConn != null && jmxConn.getJmxConnector() != null)
			try{
				jmxConn.getJmxConnector().close();
			}catch(Exception exp){
				logger.error("Exception occurred while closing JMX connection",  exp);
			}
	}

	/**
	 * Calls JMX service to retrieve attributes and values for the given Mbean object.
	 * Creates custom metric name-value pair and send it to controller via machine agent.
	 * If the value is NULL or is NOT a number, it will set value as 0.
	 */
	public void getMbeanMetric(JmxConnectorObject jmxConn, ObjectName mbeanObj) throws Exception{

		String deployment = mbeanObj.getKeyProperty(CfConstants.DEPLOYMENT);
		String job = mbeanObj.getKeyProperty(CfConstants.JOB);
		String index = mbeanObj.getKeyProperty(CfConstants.INDEX);
		String ip = mbeanObj.getKeyProperty(CfConstants.IP);
		
		if(ip == null || ip.equals("null"))
			ip = CfConstants.IP_UNDEFINED;

		logger.debug("MbeanObject = {}, deployment = {}, job = {}, index = {} , ip = {}", mbeanObj, deployment, job, index, ip);

		if(deployment == null || job == null || index == null){
			logger.error("Deployment [{}] or Job [{}] or Index [{}] values are null in the mbean {}", deployment, job, index, mbeanObj);
			return;
		}
		
		//Replacing the names as per the config, especially for REGEX mapped names
		deployment = regexPatternMatchedName(config.getDeploymentsConfig().getDeploymentsMatchPatterns(), deployment);
		job = regexPatternMatchedName(config.getJobsConfig().getJobMatchPatterns(), job);

		MBeanInfo mbeanInfo = jmxConn.getMbsc().getMBeanInfo(mbeanObj);
		MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();
		if(mbeanAttributeInfos != null && mbeanAttributeInfos.length > 0){

			List<String> attribsList = new ArrayList<String>();
			for(int i = 0; i< mbeanAttributeInfos.length; i++) {

				String attribName = mbeanAttributeInfos[i].getName();

				logger.debug("mbeanAttributeInfos[{}]  name = {}",i, attribName);

				if(config.getAttributesConfig().getRequiredOrIgnoredAttributes() == CfConstants.REQUIRED){
					if(!config.getAttributesConfig().getAttributeNames().contains(attribName) && !isRegexPatternMatch(config.getAttributesConfig().getAttributesMatchPatterns(), attribName)){
						logger.debug("Not required attribute = {}", attribName);
						continue;
					}
				}else{
					if(config.getAttributesConfig().getAttributeNames().contains(attribName)){
						logger.debug("Ignoring attribute = {}", attribName);
						continue;
					}
				}								
				attribsList.add(attribName);
			}

			logger.debug("mbeanObj = {}, Arraylist = {}", mbeanObj, attribsList);

			if(!attribsList.isEmpty()){
				String [] attributesArray = (String [])attribsList.toArray(new String[attribsList.size()]);
				logger.debug("mbeanObj {} and attributes {}", mbeanObj, attributesArray);
				AttributeList attributesList = jmxConn.getMbsc().getAttributes(mbeanObj, attributesArray);	
				if (attributesList != null && attributesList.size() > 0){
					for(int i = 0; i< attributesList.size(); i++){
						Attribute attr = (Attribute)attributesList.get(i);
						String attrName = attr.getName();
						Object attrValue = attr.getValue();
						
						attrName = attrName.replaceAll(",", ";");
						
						if (attrValue != null && attrValue instanceof Number) {
							logger.debug("JMX Attribute fetched as {} = {}", attrName, attrValue);
							
							String metricPath = this.config.getMetricPrefix() + deployment + "|" + job + "|" + index + "|" + ip + "|" + attrName;
							String metricValue = CfUtility.convertMetricValuesToString(attrValue);

							logger.debug("Metric [{} = {}]", metricPath, metricValue);
							printMetric(metricPath, metricValue, this.config.getAggregationType(), this.config.getTimeRollupType(), this.config.getClusterRollupType());				
						}else
							logger.debug("JMX Attribute[{}={}] fetched having value null or not a number", attrName, attrValue);
					}
				}
			}else{
				logger.debug("No Attributes found to be monitored for mbean Object = {}", mbeanObj);
			}
		}
	}

	private void printMetric(String metricPath, String metricValue, String aggregation, String timeRollup, String cluster) {
		MetricWriter metricWriter = this.getMetricWriter(metricPath, aggregation, timeRollup, cluster);
		if (metricValue != null) {
			metricWriter.printMetric(metricValue);
		}
	}

	public static String getImplementationVersion() {
		return CloudFoundryExtension.class.getPackage().getImplementationTitle();
	}

	/**
	 * Synchronized method call to get Id value for threads as 
	 * key to retrieve list of mbeans to traverse.
	 */
	private synchronized Integer getDomainsMapKey(){
		Integer threadNum = this.currentThreadId;

		if(this.currentThreadId == this.maxThreads){
			this.currentThreadId = 1;
		}else 
			this.currentThreadId++;

		return threadNum;
	}

	/**
	 * Starting a standalone thread to refresh the configurations at the set frequency
	 */
	private void startDomainRefreshingThread(final CloudFoundryExtension cfExt){
		if(!cfExt.refreshThreadStarted){
			Thread refreshThread = new Thread(){
				public void run() {
					while(true){
						try{
							Thread.sleep(cfExt.config.getDomainRefreshTimeInMins() * 60 * 1000);
							cfExt.initialized = false;
							logger.info("Cloud Foundry initilization set to {}", cfExt.initialized);
						}catch(InterruptedException exp){
							logger.error("Thread interrupted with exp = {}", exp);
						}
					}

				}

			};

			refreshThread.start();
			cfExt.refreshThreadStarted = true;
		}
	}

	/**
	 * Thread task to traverse the mbeans list and fetch the data by calling JMX service.
	 */
	public class JMXThread implements Runnable {
		CloudFoundryExtension cfExt;
		Integer mapKey = 0;

		public void run() {
			logger.debug("Starting thread = {} at time = {}", Thread.currentThread().getName(), new Date(System.currentTimeMillis()));
			JmxConnectorObject jmxConn = null;

			try {
				jmxConn = cfExt.jmxOpenConnection();
				if(jmxConn != null){
					if (mapKey == 0){
						mapKey = cfExt.getDomainsMapKey();
					}
					List<ObjectName> domainsList = cfExt.mbeanDomains.get(mapKey);
					if(domainsList != null && !domainsList.isEmpty()){
						for (ObjectName domain : domainsList){
							try{
								cfExt.getMbeanMetric(jmxConn, domain);
							}catch(Exception e){
								logger.error("Exception occurred during getMbeanMetric()", e);
								continue;
							}
						}
					}else{
						logger.info("Mbeans list empty for map key {}", mapKey);
					}
				}
			} catch (Exception e) {
				logger.error("Exception occurred", e);
			} finally{
				cfExt.jmxConnectionClose(jmxConn);
			}	
			logger.debug("Finishing thread = {} at time = {}", Thread.currentThread().getName(), new Date(System.currentTimeMillis()));
		}		
	}

	public static void main(String[] args) {	
		CloudFoundryExtension cfExt = new CloudFoundryExtension();
		try{
			File confFile = CfUtility.getConfigFile(args[0]);
			if(confFile != null){
				Configuration config = Configuration.read(confFile);
				if (config != null) {

					cfExt.config = config;
					cfExt.maxThreads = config.getJmxService().getMaxParallelConnection();
					try{
						cfExt.jmxServiceUrl =  new JMXServiceURL(config.getJmxService().getJmxServiceUrl());
					}catch(Exception exp){
						logger.error("Error occured while connecting to JMXServiceUrl", exp); 		
					}
					cfExt.populateMbeanDomains();
				} else {
					logger.error("Config could not be loaded, exiting program");         	
				}
				if(cfExt.startThreads){
					while(true){
						cfExt.startJMXThreads();
						Thread.sleep(60*1000);
					}
				}else{
					logger.debug("Not running any threads as startThreads = {}", cfExt.startThreads);
				}
			}
		}catch(Exception exp){
			logger.error("Error occurred", exp);
		}
	}
}
