/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry.utils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.appdynamics.extensions.PathResolver;
import com.google.common.base.Strings;

/**
 * @author amehta
 *
 */
public class CfUtility {

	/**
     * Currently, appD controller only supports Integer values. This function will round all the decimals into integers and convert them into strings.
     * @param attribute
     * @return
     */
	public static String convertMetricValuesToString(Object attribute) {
		if(attribute instanceof Double){
			return String.valueOf(Math.round((Double) attribute));
		}
		else if(attribute instanceof Float){
			return String.valueOf(Math.round((Float) attribute));
		}
		return attribute.toString();
	}
	
	public static File getConfigFile(String filename) {
		if(filename == null){
			return null;
		}
		//for absolute paths
		if(new File(filename).exists()){
			return new File(filename);
		}
		//for relative paths
		File jarPath = PathResolver.resolveDirectory(CfUtility.class);
		String configFileName = "";
		if(!Strings.isNullOrEmpty(filename)){
			configFileName = jarPath + File.separator + filename;
		}
		
		return new File(configFileName);
	}
	
	public static boolean isRegexMatched(String pattern, String text) {

        Pattern regexPattern = Pattern.compile(pattern);
        Matcher regexMatcher = regexPattern.matcher(text);

        while (regexMatcher.matches()) {
            return true;
        }

        return false;
    }

}
