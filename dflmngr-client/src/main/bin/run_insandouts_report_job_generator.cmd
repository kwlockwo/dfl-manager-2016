@echo off 

set BASE_DIR=${app.home}
set JAVA_HOME=${java.dir}
set MODLUES_DIR=%BASE_DIR%\modules
set TOMCAT_LIB=%CATALINA_HOME%\lib
set TOMCAT_BIN=%BASE_DIR%\tomcat\bin
set LIB_DIR=%BASE_DIR%\lib
set APP_VERSION=${project.version}
set APP_MODULES=%MODLUES_DIR%\dflmngr-base-%APP_VERSION%.jar;%MODLUES_DIR%\dflmngr-model-%APP_VERSION%.jar;%MODLUES_DIR%\dflmngr-core-%APP_VERSION%.jar;%MODLUES_DIR%\dflmngr-webservices-%APP_VERSION%.jar;%MODLUES_DIR%\dflmngr-client-%APP_VERSION%.jar

set CLASSPATH=%APP_MODULES%;%LIB_DIR%\*;%TOMCAT_LIB%\catalina.jar;%TOMCAT_BIN%\tomcat-juli.jar

%JAVA_HOME%\bin\java -classpath %CLASSPATH% net.dflmngr.scheduler.generators.InsAndOutsReportJobGenerator