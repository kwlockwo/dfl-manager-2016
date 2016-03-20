#!/bin/sh

ORIG_PATH=`pwd`
export CATALINA_BASE=${catalina.base}
cd $CATALINA_BASE/bin
TITLE=Dflmngr Tomcat
startup.bat $TITLE
cd $ORIG_PATH