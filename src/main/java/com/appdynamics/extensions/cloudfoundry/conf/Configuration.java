package com.appdynamics.extensions.cloudfoundry.conf;

import com.appdynamics.extensions.cloudfoundry.model.JmxServiceObject;
import com.appdynamics.extensions.cloudfoundry.utils.CfConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author ashish.mehta
 *
 */
public class Configuration {
	
	public static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	private JmxServiceObject jmxService;
	private List<String> attributes;
	private List<String> jobs;
	private List<String> domains;
	private List<String> deployments;
	private Integer requiredOrIgnored;
	private String metricPrefix;
	private Integer domainRefreshTimeInMins;
	
	public JmxServiceObject getJmxService() {
		return jmxService;
	}
	public void setJmxService(JmxServiceObject jmxService) {
		this.jmxService = jmxService;
	}	
	public List<String> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	public List<String> getJobs() {
		return jobs;
	}
	public void setJobs(List<String> jobs) {
		this.jobs = jobs;
	}
	public List<String> getDomains() {
		return domains;
	}
	public void setDomains(List<String> domains) {
		this.domains = domains;
	}
	public List<String> getDeployments() {
		return deployments;
	}
	public void setDeployments(List<String> deployments) {
		this.deployments = deployments;
	}
	public Integer getRequiredOrIgnored() {
		return requiredOrIgnored;
	}
	public void setRequiredOrIgnored(Integer requiredOrIgnored) {
		this.requiredOrIgnored = requiredOrIgnored;
	}
	public String getMetricPrefix() {
		return metricPrefix;
	}
	public void setMetricPrefix(String metricPrefix) {
		this.metricPrefix = metricPrefix;
	}	
	public Integer getDomainRefreshTimeInMins() {
		return domainRefreshTimeInMins;
	}
	public void setDomainRefreshTimeInMins(Integer domainRefreshTimeInMins) {
		this.domainRefreshTimeInMins = domainRefreshTimeInMins;
	}
	
	public static Configuration read() {
        File conf = new File("conf", "config.yaml");
        return read(conf);
    }
	
	public static Configuration read(File conf) {		
        Yaml yaml = new Yaml(new Constructor(Configuration.class));
        try {
        	Configuration config = (Configuration) yaml.load(new FileInputStream(conf));
        	logger.info("Configuration = {}", config);
        	
            return config;
        } catch (FileNotFoundException e) {
            logger.error("The file is not found " + conf.getAbsolutePath(), e);
        }
        return null;
    }

	/**
	 * This method will override the properties which are loaded from the config file with the settings passed to the machine agent during invocation
	 */
	public void overrideWithSystemProperties() {
		if(jmxService != null) {
			jmxService.setJmxServiceUrl(System.getProperty(CfConstants.PROP_JMX_SERVICE_URL, jmxService.getJmxServiceUrl()));
			jmxService.setUsername(System.getProperty(CfConstants.PROP_JMX_USERNAME, jmxService.getUsername()));
			jmxService.setPassword(System.getProperty(CfConstants.PROP_JMX_PASSWORD, jmxService.getPassword()));

			try {
				jmxService.setAuthenticate(Integer.parseInt(System.getProperty(CfConstants.PROP_JMX_AUTHENTICATE, jmxService.getAuthenticate().toString())));
				jmxService.setMaxParallelConnection(Integer.parseInt(System.getProperty(CfConstants.PROP_JMX_CONNECTIONS, jmxService.getMaxParallelConnection().toString())));
			} catch (NumberFormatException exception) {
				logger.info("Error in parsing Authenticate and Max Parallel Connection, using the settings in the config file");
			}
		}
	}

	@Override
	public String toString(){
		return  "Configuration{jmxService=" + this.jmxService +
				",attributes=" + this.attributes +
				",jobs=" + this.jobs +
				",domains=" + this.domains +
				",deployments=" + this.deployments +
				",requiredOrIgnored=" + this.requiredOrIgnored +
				",domainRefreshTimeInMins=" + this.domainRefreshTimeInMins +
				",metricPrefix=" + this.metricPrefix + "}";
				 
	}

}
