FROM jeanblanchard/java:8
ADD mesosframework-*.jar /tmp/mesosframework.jar
ENTRYPOINT ["java", "-Djava.library.path=/usr/local/lib", "-jar", "/tmp/mesosframework.jar"]
