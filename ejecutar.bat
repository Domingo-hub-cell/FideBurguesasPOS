@echo off
mvn clean package
java -jar target\fideburguesas-pos-1.0.0.jar
pause
