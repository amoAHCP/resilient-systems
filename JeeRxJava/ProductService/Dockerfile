FROM jboss/wildfly

ADD customization /opt/jboss/wildfly/customization/
# ADD src /opt/jboss/wildfly/build/
# ADD pom.xml /opt/jboss/wildfly/build/pom.xml
ADD target /opt/jboss/wildfly/target/
# USER root
# RUN yum -y install maven
# USER jboss

# RUN cd /opt/jboss/wildfly/build/ && mvn clean package && cp /opt/jboss/wildfly/build/target/WarehouseService.war /opt/jboss/wildfly/target/

CMD ["/opt/jboss/wildfly/customization/execute.sh"]
 

