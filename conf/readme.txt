1) What this program does?

- logs the attributes and values found in domain = org.cloudfoundary
- based on the configuration in config.yaml, it will either fetch only required mbeans/attributes or ignore specified mbeans/attributes

2) How to run this program?

- run below command (user should be able to create the files in the directory)
java -Dlog4j.configuration=file:log4j.xml -jar cloud-foundary-extension.jar config.yaml


3) Log file = jmx-metrics.log created in the directory has the attributes and values (in form of custom metrics log statements)
