package com.appdynamics.extensions.cloudfoundry.utils;

public interface CfConstants {

	public static final String CONFIG_FILE = "config-file";
	
	public static final Integer REQUIRED = 1;
	public static final Integer TO_BE_IGNORED = 0;
	
	public static final String JOB = "job";
	public static final String INDEX = "index";
	public static final String DEPLOYMENT = "deployment";
	public static final String IP = "ip";
	public static final String IP_UNDEFINED = "undefined";

	public static final String PROP_JMX_SERVICE_URL = "extension.pcf.jmx.serviceURL";
	public static final String PROP_JMX_USERNAME = "extension.pcf.jmx.username";
	public static final String PROP_JMX_PASSWORD = "extension.pcf.jmx.password";
	public static final String PROP_JMX_AUTHENTICATE = "extension.pcf.jmx.authenticate";
	public static final String PROP_JMX_CONNECTIONS = "extension.pcf.jmx.maxParallelConnection";


}
