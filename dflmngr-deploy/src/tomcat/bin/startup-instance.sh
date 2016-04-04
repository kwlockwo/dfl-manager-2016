#!/bin/bash

ORIG_PATH=`pwd`
export export DISPLAY=:1.5
export CATALINA_BASE=${catalina.base}
export CATALINA_PID=$CATALINA_BASE/bin/catalina.pid
cd $CATALINA_HOME/bin
startup.sh
cd $ORIG_PATH