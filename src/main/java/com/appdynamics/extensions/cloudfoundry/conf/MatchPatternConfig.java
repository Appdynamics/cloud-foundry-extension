/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry.conf;

public class MatchPatternConfig {
	
	private String pattern;
	private String substituteName;
	
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getSubstituteName() {
		return substituteName;
	}
	public void setSubstituteName(String substituteName) {
		this.substituteName = substituteName;
	}
	
	@Override
	public String toString(){
		return  "MatchPatternConfig{pattern=" + this.pattern + 
				", substituteName=" + this.substituteName + "}";
	}


}
