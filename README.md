localization-properties-consistency-maven-plugin
================================================

This maven plugin search for locale-specific properties file and compare them to indicate if a property is missing for a given locale.

How to start
------------

This is package as a Maven Plugin, to use it you will need to add the following to your project pom:

    <plugin>
    	<groupId>fr.openwide.maven.plugins</groupId>
    	<artifactId>localization-properties-consistency-maven-plugin</artifactId>
    	<version>0.0.1-SNAPSHOT</version>
    	<executions>
    		<execution>
    			<id>scan</id>
    			<goals>
    				<goal>scan</goal>
    			</goals>
    		</execution>
    	</executions>
    </plugin>
  
This is still a `SNAPSHOT` version and it hasn't been deployed outside of Open Wide yet.

About
-----

Sources are licensed under the Apache 2 license.

This plugin is mostly developed by Open Wide (http://www.openwide.fr/). External contributions or ideas are welcome.
