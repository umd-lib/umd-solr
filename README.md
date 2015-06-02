Updating the UMD custom Solr Classes
---------------------------------------------------

1. Build iand deploy the umd solr project  
	* Build the assembly a deploy to umd nexus `mvn clean deploy`
2. Fetch the `umd-solr-<VERSION>.jar` from nexus and replace the previous version jar in `/apps/solr/lib` directory on the solr server.
	* wget "https://maven.lib.umd.edu/nexus/service/local/artifact/maven/content?r=releases&g=edu.umd.lib&a=umd-solr&v=LATEST" --content-disposition
	* mv /apps/solr/lib/umd-solr-2.2.2-2.jar /apps/solr/lib/umd-solr-2.2.2-2.jar.bak (assuming 2.2.2-2 is the previous version)
	* mv umd-solr-*.jar /apps/solr/lib/
3. You would have to restart the solr server for the changes to take effect.
