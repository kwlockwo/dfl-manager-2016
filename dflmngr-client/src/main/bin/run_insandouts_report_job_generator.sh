#!/bin/bash

export BASE_DIR=${app.home}
export JAVA_HOME=${java.dir}
export MODLUES_DIR=$BASE_DIR/modules
export TOMCAT_LIB=$CATALINA_HOME/lib
export TOMCAT_BIN=$BASE_DIR/tomcat/bin
export LIB_DIR=$BASE_DIR/lib
export APP_VERSION=${project.version}
export APP_MODULES=$MODLUES_DIR/dflmngr-base-$APP_VERSION.jar:$MODLUES_DIR/dflmngr-model-$APP_VERSION.jar:$MODLUES_DIR/dflmngr-core-$APP_VERSION.jar:$MODLUES_DIR/dflmngr-webservices-$APP_VERSION.jar:$MODLUES_DIR/dflmngr-client-$APP_VERSION.jar

export CLASSPATH=$APP_MODULES:$LIB_DIR/*:$TOMCAT_LIB/catalina.jar:$TOMCAT_BIN/tomcat-juli.jar

$JAVA_HOME/bin/java -classpath $CLASSPATH net.dflmngr.scheduler.generators.InsAndOutsReportJobGenerator