/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry.conf;

import java.util.List;

public class AttributesConfig {

	private Integer requiredOrIgnoredAttributes;
	private List<String> attributeNames;
	private List<MatchPatternConfig> attributesMatchPatterns;

	public Integer getRequiredOrIgnoredAttributes() {
		return requiredOrIgnoredAttributes;
	}
	public void setRequiredOrIgnoredAttributes(Integer requiredOrIgnoredAttributes) {
		this.requiredOrIgnoredAttributes = requiredOrIgnoredAttributes;
	}
	public List<String> getAttributeNames() {
		return attributeNames;
	}
	public void setAttributeNames(List<String> attributeNames) {
		this.attributeNames = attributeNames;
	}
	public List<MatchPatternConfig> getAttributesMatchPatterns() {
		return attributesMatchPatterns;
	}
	public void setAttributesMatchPatterns(
			List<MatchPatternConfig> attributesMatchPatterns) {
		this.attributesMatchPatterns = attributesMatchPatterns;
	}

	@Override
	public String toString(){
		return  "AttributesConfig{requiredOrIgnoredAttributes=" + this.requiredOrIgnoredAttributes + 
				", attributeNames=" + this.attributeNames + 
				", attributesMatchPatterns=" + this.attributesMatchPatterns + "}";
	}

}
