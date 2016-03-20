#!/bin/sh

ORIG_PATH=`pwd`
export CATALINA_BASE=${catalina.base}
cd $CATALINA_BASE/bin
shutdown.sh
cd $ORIG_PATH