@echo off
set ORIG_PATH=%cd%
set CATALINA_BASE=${catalina.base}
cd "%CATALINA_HOME%\bin"
call shutdown.bat
cd /d %ORIG_PATH%