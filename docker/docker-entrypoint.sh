#!/bin/bash
set -x
exec java $JAVA_OPTS -jar /tmp/mesosframework.jar "$@"
