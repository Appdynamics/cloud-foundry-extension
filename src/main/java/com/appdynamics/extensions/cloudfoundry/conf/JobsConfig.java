/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry.conf;

import java.util.List;

public class JobsConfig {
	
	private Integer requiredOrIgnoredJobs;
	private List<String> jobNames;
	private List<MatchPatternConfig> jobMatchPatterns;

	public Integer getRequiredOrIgnoredJobs() {
		return requiredOrIgnoredJobs;
	}
	public void setRequiredOrIgnoredJobs(Integer requiredOrIgnoredJobs) {
		this.requiredOrIgnoredJobs = requiredOrIgnoredJobs;
	}
	public List<String> getJobNames() {
		return jobNames;
	}
	public void setJobNames(List<String> jobNames) {
		this.jobNames = jobNames;
	}
	public List<MatchPatternConfig> getJobMatchPatterns() {
		return jobMatchPatterns;
	}
	public void setJobMatchPatterns(List<MatchPatternConfig> jobMatchPatterns) {
		this.jobMatchPatterns = jobMatchPatterns;
	}
	
	@Override
	public String toString(){
		return  "JobsConfig{requiredOrIgnoredJobs=" + this.requiredOrIgnoredJobs + 
				", jobNames=" + this.jobNames + 
				", jobMatchPatterns=" + this.jobMatchPatterns + "}";
	}

}
