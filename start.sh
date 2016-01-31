#!/bin/bash

export WORKSPACE=workspace

# Prerequisites
[ -d $WORKSPACE/quickstart ] || git clone https://github.com/wildfly/quickstart.git $WORKSPACE/quickstart

# WildFly 9
[ -f wildfly-9.0.2.Final.zip ] || wget http://download.jboss.org/wildfly/9.0.2.Final/wildfly-9.0.2.Final.zip
export WILDFLY_ZIP_URL=file://${PWD}/wildfly-9.0.2.Final.zip
git -C $WORKSPACE/quickstart fetch origin 9.x
git -C $WORKSPACE/quickstart checkout 9.x

# Composition of classes for JenkinsTestExecutor
CLASSES=org.jboss.qa.jcontainer.example.jobs.BaseJob,org.jboss.qa.jcontainer.example.jobs.HelloWorldJmsQuickstartJob

# Run
rm -rf $WORKSPACE/wildfly
mvn clean compile exec:java -Dexec.arguments=$CLASSES
