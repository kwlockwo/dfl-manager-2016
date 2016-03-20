@echo off
set ORIG_PATH=%cd%
set CATALINA_BASE=${catalina.base}
cd "%CATALINA_HOME%\bin"
set TITLE=Dflmngr Tomcat
call startup.bat %TITLE%
cd /d %ORIG_PATH%