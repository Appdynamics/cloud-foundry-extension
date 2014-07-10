package com.appdynamics.extensions.cloudfoundry.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appdynamics.extensions.PathResolver;
import com.google.common.base.Strings;

/**
 * @author amehta
 *
 */
public class CfUtility {
	
	private static Logger logger = LoggerFactory.getLogger(CfUtility.class);

	public static boolean isNumeric(Object obj)	{  
		if(obj == null) return false;
		String str = obj.toString();
		try {  
			Double.parseDouble(str);  
		} catch(NumberFormatException nfe) {  
			logger.debug("Object {} is not a number", obj);
			return false;  
		}  
		return true;  
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

}
