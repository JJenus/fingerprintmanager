@echo off

:: Replace these values with your custom JAR information
set ZKFinger_JAR=libraries/ZKFingerReader.jar
set GROUP_ID=your.group.id
set ARTIFACT_ID=your-artifact-id
set VERSION=your-version

:: Step 1: Install the custom JAR into the local Maven repository
mvn install:install-file -Dfile="%ZKFinger_JAR%" -DgroupId="%GROUP_ID%" -DartifactId="%ARTIFACT_ID%" -Dversion="%VERSION%" -Dpackaging=jar

:: Step 2: Add the custom JAR as a dependency in the pom.xml
:: echo ^<dependency^> >> pom.xml
:: echo     ^<groupId^>%GROUP_ID%^</groupId^> >> pom.xml
:: echo     ^<artifactId^>%ARTIFACT_ID%^</artifactId^> >> pom.xml
:: echo     ^<version^>%VERSION%^</version^> >> pom.xml
:: echo ^</dependency^> >> pom.xml
