/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.appdynamics.extensions.cloudfoundry.model.JmxServiceObject;

/**
 * @author ashish.mehta
 *
 */
public class Configuration {
	
	public static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	private JmxServiceObject jmxService;
	private List<String> domains;
	private DeploymentsConfig deploymentsConfig;
	private JobsConfig jobsConfig;
	private AttributesConfig attributesConfig;
	private String aggregationType = "AVERAGE";
	private String timeRollupType = "AVERAGE";
	private String clusterRollupType = "INDIVIDUAL";
	private String metricPrefix;
	private Integer domainRefreshTimeInMins;
	
	public JmxServiceObject getJmxService() {
		return jmxService;
	}
	public void setJmxService(JmxServiceObject jmxService) {
		this.jmxService = jmxService;
	}	
	public List<String> getDomains() {
		return domains;
	}
	public void setDomains(List<String> domains) {
		this.domains = domains;
	}
	public DeploymentsConfig getDeploymentsConfig() {
		return deploymentsConfig;
	}
	public void setDeploymentsConfig(DeploymentsConfig deploymentsConfig) {
		this.deploymentsConfig = deploymentsConfig;
	}
	public String getAggregationType() {
		return aggregationType;
	}
	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}
	public String getTimeRollupType() {
		return timeRollupType;
	}
	public void setTimeRollupType(String timeRollupType) {
		this.timeRollupType = timeRollupType;
	}
	public String getClusterRollupType() {
		return clusterRollupType;
	}
	public void setClusterRollupType(String clusterRollupType) {
		this.clusterRollupType = clusterRollupType;
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
	public JobsConfig getJobsConfig() {
		return jobsConfig;
	}
	public void setJobsConfig(JobsConfig jobsConfig) {
		this.jobsConfig = jobsConfig;
	}
	public AttributesConfig getAttributesConfig() {
		return attributesConfig;
	}
	public void setAttributesConfig(AttributesConfig attributesConfig) {
		this.attributesConfig = attributesConfig;
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
	
	@Override
	public String toString(){
		return  "Configuration{jmxService=" + this.jmxService +
				",domains=" + this.domains +
                ",deploymentsConfig=" + this.deploymentsConfig +
				",jobsConfig=" + this.jobsConfig +
				",attributesConfig=" + this.attributesConfig +
				",aggregationType=" + this.aggregationType +
				",timeRollupType=" + this.timeRollupType +
				",clusterRollupType=" + this.clusterRollupType +
				",domainRefreshTimeInMins=" + this.domainRefreshTimeInMins +
				",metricPrefix=" + this.metricPrefix + "}";
				 
	}

}
