@echo off
echo Stopping Apache Tomcat 9...
set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
set "CATALINA_HOME=C:\Users\anshi\apache-tomcat-9.0.96"
"%CATALINA_HOME%\bin\catalina.bat" stop
