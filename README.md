Travis test.

# mesos-framework
Create a mesos framework with just an application.properties file!

## Pre-release
This is a pre-release version. There are no official binaries yet. This is a placeholder and shows intended functionality.

# Introduction
Writing Frameworks for Apache Mesos is hard. We created the [Mesos Starter](www.github.com/containersolutions/mesos-starter) project to reduce the amount of boilerplate.

Mesos-Framework is a pre-built version of Mesos-Starter, that allows users to run a binary or Docker container as a Framework via a single configuration file.

# Quick start
Once you have decided upon a binary or Docker container to run, you simply need to call:

`docker run -d containersol/mesos-framework:latest --spring.config.location=your.application.properties`

or if you don't want to use Docker:

`java -Djava.library.path=/usr/local/lib -jar mesos-framework-0.0.1.jar --spring.config.location=your.application.properties`

* Note, this files have not been released yet. These commands won't work.

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
