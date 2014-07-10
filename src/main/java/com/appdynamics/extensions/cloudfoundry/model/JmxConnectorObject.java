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
