@echo off
echo ========================================================
echo Starting Apache Tomcat 9 for TimeTrack...
echo ========================================================
set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
set "CATALINA_HOME=C:\Users\anshi\apache-tomcat-9.0.96"

echo Deploying latest timetrack.war to Tomcat webapps...
copy /Y "%~dp0target\timetrack.war" "%CATALINA_HOME%\webapps\timetrack.war"

echo Starting Tomcat Server...
echo Once started, access your application at:
echo http://localhost:8080/timetrack/
echo ========================================================
"%CATALINA_HOME%\bin\catalina.bat" run
