/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry.conf;

import java.util.List;

public class DeploymentsConfig {
	
	private Integer requiredOrIgnoredDeployments;
	private List<String> deploymentNames;
	private List<MatchPatternConfig> deploymentsMatchPatterns;
	
	public Integer getRequiredOrIgnoredDeployments() {
		return requiredOrIgnoredDeployments;
	}
	public void setRequiredOrIgnoredDeployments(Integer requiredOrIgnoredDeployments) {
		this.requiredOrIgnoredDeployments = requiredOrIgnoredDeployments;
	}
	public List<String> getDeploymentNames() {
		return deploymentNames;
	}
	public void setDeploymentNames(List<String> deploymentNames) {
		this.deploymentNames = deploymentNames;
	}
	public List<MatchPatternConfig> getDeploymentsMatchPatterns() {
		return deploymentsMatchPatterns;
	}
	public void setDeploymentsMatchPatterns(
			List<MatchPatternConfig> deploymentsMatchPatterns) {
		this.deploymentsMatchPatterns = deploymentsMatchPatterns;
	}
	
	@Override
	public String toString(){
		return  "DeploymentsConfig{requiredOrIgnoredDeployments=" + this.requiredOrIgnoredDeployments + 
				", deploymentNames=" + this.deploymentNames + 
				", deploymentsMatchPatterns=" + this.deploymentsMatchPatterns + "}";
	}
	

}
