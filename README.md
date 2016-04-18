# mesos-framework
Create a mesos framework with just an application.properties file!

# Introduction
Writing Frameworks for Apache Mesos is hard. We created the [Mesos Starter](www.github.com/containersolutions/mesos-starter) project to reduce the amount of boilerplate.

MesosFramework is a pre-built version of Mesos-Starter, that allows users to run a binary or Docker container as a Framework via a single configuration file.

# Features
(Most features come from the [Mesos-Starter](https://github.com/ContainerSolutions/mesos-starter) project)

- [x] State stored in ZooKeeper
- [x] Mesos Authorisation
- [ ] ZooKeeper Authorisation (requires testing)
- [x] Jar mode (no docker)
- [x] Resource specification (including port)
- [x] Import Kibana.yml settings file
- [x] "Spread" orchestration strategy (Spreads instances across distinct hosts)
- [x] Decoupled from Kibana. Use any version.
- [x] Decoupled from Mesos. Use any version 0.25+
- [x] Horizontal scaling via JMX

Added by MesosFramework:
- [x] Live horizontal scaling via a REST endpoint
- [x] Single endpoint to check health of all instances
- [x] Customizable mesos healthcheck command

# Quick start
Once you have decided upon a binary or Docker container to run, you simply need to call:

`docker run -d containersol/mesosframework:latest --spring.config.location=your.application.properties`

or if you don't want to use Docker:

`java -jar mesosframework-0.0.1.jar --spring.config.location=your.application.properties`

# Application properties
All options can be specified as either:
- A cli parameter: `--mesos.command=pwd`
- A properties file: `mesos.command=pwd`
- Java options: `-Dmesos.command=pwd`
- Environmental variables: `MESOS_COMMAND=pwd`
In that order of preference.

To pass a configuration file, the following property must be set:
- `--spring.config.location=my.properties` (Or the env var `SPRING_CONFIG_LOCATION`, etc.)

Valid configration options are specified by the [Mesos Starter](www.github.com/containersolutions/mesos-starter) project.

Please see the [examples](./docs/examples) folder for examples.

You can also provide new options with `${my.option}` inside the properties file. Now you can provide that option with `--my.option=Hi!`

# Extensions to the Mesos-starter project
This project adds to the Mesos-starter project on the following points:

## Horizontal scaling
It adds an endpoint at the following location to control the number of instances in the cluster. The endpoint matches the properties file definition of the same name:

`GET /mesos/resources/count` Returns the current number of requested instances. For example to get the current number of instances:

```
$ curl -s http://localhost:8080/mesos/resources/count
3
```

`POST /mesos/resources/count` with a body of type `Integer` will set the number of requested instances. For example, to set the number of instances to 1:

```
$ curl -XPOST -H 'Content-Type: text/plain' http://localhost:8080/mesos/resources/count -d 1
```

## Healthcheck
An extra healthchecks to monitor the state of the running tasks is exposed.
 
`GET /health` returns the health of the running tasks. If `count == running instances` the health will be `UP`. For example:
 
```
$ curl http://localhost:8080/health
{"status":"UP","task":{"status":"UP","mesos.resources.count":1,"instances":1,"TASK_STAGING":0,"TASK_STARTING":0,"TASK_RUNNING":3,"TASK_KILLING":0,"TASK_FINISHED":0,"TASK_FAILED":0,"TASK_KILLED":0,"TASK_LOST":0,"TASK_ERROR":0},"diskSpace":{"status":"UP","total":9896046592,"free":2796249088,"threshold":10485760}}%  
```

# Sponsors
This project is sponsored by Cisco Cloud Services.

# License
Apache License 2.0