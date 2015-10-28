Cloud-Foundry-monitoring-extension
==================================

An AppDynamics extension to be used with a stand alone Java machine agent to provide metrics from Pivotal Cloud Foundry.

### Use Case

Pivotal Cloud Foundry is PaaS infrastructure software. The Cloud Foundry monitoring extension captures metrics exposed by Pivotal CF JMX server and displays them in the AppDynamics Metric Browser.

### Prerequisites

Make sure that the Pivotal CF JMX server has enabled remote access to JMX Mbeans.
The machine where the machine agent runs (with this monitoring extension) has access to the Pivotal CF JMX server.

### Metrics Provided
 
Various CF job related metrics available via JMX mbean.

Note : By default, a Machine agent or a AppServer agent can send a fixed number of metrics to the controller. To change this limit, please follow the instructions mentioned here

http://docs.appdynamics.com/display/PRO14S/Metrics+Limits
 
### Installation
 
1. Run "mvn clean install" and find the CloudFoundryMonitor.zip file in the "target" folder. You can also download the CloudFoundryMonitor.zip from AppDynamics Exchange.

2. Unzip as "CloudFoundryMonitor" and copy the "CloudFoundryMonitor" directory to `<MACHINE_AGENT_HOME>/monitors`
 
### Configuration
 
Note : Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a yaml validator
 
1. Configure the Cloud Foundry JMX server instance by editing the config.yaml file in `<MACHINE_AGENT_HOME>/monitors/CloudFoundryMonitor/`.

2. Configure the MBeans in the config.yaml. By default, "org.cloudfoundry" is all that you may need.

3. Configure the jobs and attributes required (or to be ignored) in config.yaml file.

4. The metric path will be like 'Custom Metrics|CF|$job-name|$index|$attribute-name'

5. Sample config.yaml file is available in <MACHINE_AGENT_HOME>/monitors/CloudFoundryMonitor/

6. Configure the path to the config.yaml file by editing the <task-arguments> in the monitor.xml file in the `<MACHINE_AGENT_HOME>/monitors/CloudFoundryMonitor/` directory.
Below is the sample:
default-value="monitors/CloudFoundryMonitor/config.yaml"
 
### Multiple Pivotal CF JMX instances/end points:
 
1. Make sure you have a separate machine agent installed and it has CloudFoundry Extension running which is pointing to each of the Pivotal CF JMX server instance or endpoint. 
 
2. Make sure that each has separate node names belonging to the same tier has the same <tier-name> in the<MACHINE_AGENT_HOME>/conf/controller-info.xml. 
 
3. Make sure that in every node, the <MACHINE_AGENT_HOME>/monitors/CloudFoundryMonitor/config.yaml should emit the same metric path, so that metrics reported by each of the nodes are aggregated at tier level.
 
### System Properties added for PCF Service Broker

1. extension.pcf.jmx.serviceURL

2. extension.pcf.jmx.username

3. extension.pcf.jmx.password

4. extension.pcf.jmx.authenticate

5. extension.pcf.jmx.maxParallelConnection
 
### Contributing
 
Find out more in the [AppSphere](http://community.appdynamics.com/t5/AppDynamics-eXchange/Cloud-Foundry-Monitoring-Extension/idi-p/9428) community.
 
### Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).




