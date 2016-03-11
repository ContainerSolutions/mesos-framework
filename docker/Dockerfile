from jeanblanchard/java:8
ADD mesos-framework-*.jar /tmp/mesos-framework.jar
ENTRYPOINT ["java", "-Djava.library.path=/usr/local/lib", "-jar", "/tmp/mesos-framework.jar"]
