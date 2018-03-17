/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.cloudfoundry.model;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

public class JmxConnectorObject {
	
	private JMXConnector jmxConnector;
	private MBeanServerConnection mbsc;
	
	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}
	public void setJmxConnector(JMXConnector jmxConnector) {
		this.jmxConnector = jmxConnector;
	}
	public MBeanServerConnection getMbsc() {
		return mbsc;
	}
	public void setMbsc(MBeanServerConnection mbsc) {
		this.mbsc = mbsc;
	}

}
