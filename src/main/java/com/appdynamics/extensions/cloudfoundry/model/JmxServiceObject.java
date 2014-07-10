package com.appdynamics.extensions.cloudfoundry.model;

/**
 * @author ashish.mehta
 *
 */
public class JmxServiceObject {
	
	private String jmxServiceUrl;
	private String username;
	private String password;
	private Integer maxParallelConnection;
    private Integer authenticate;
	
	public String getJmxServiceUrl() {
		return jmxServiceUrl;
	}
	public void setJmxServiceUrl(String jmxServiceUrl) {
		this.jmxServiceUrl = jmxServiceUrl;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getMaxParallelConnection() {
		return maxParallelConnection;
	}
	public void setMaxParallelConnection(Integer maxParallelConnection) {
		this.maxParallelConnection = maxParallelConnection;
	}
	public Integer getAuthenticate() {
		return authenticate;
	}
	public void setAuthenticate(Integer authenticate) {
		this.authenticate = authenticate;
	}
	
	@Override
	public String toString(){		
		return "JmxServiceObject{jmxServiceUrl=" + this.jmxServiceUrl +
				",maxParallelConnection=" + this.maxParallelConnection + 
				",authenticate=" + this.authenticate + "}";
		
	}

}
