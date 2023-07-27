#!/bin/bash

# Replace these values with your custom JAR information
ZKFinger_JAR="libraries/ZKFingerReader.jar" #path/to/your/custom.jar
GROUP_ID="com.circuit"
ARTIFACT_ID="zkfinger"
VERSION="1.0"

# Step 1: Install the custom JAR into the local Maven repository
mvn install:install-file -Dfile="$ZKFinger_JAR" -DgroupId="$GROUP_ID" -DartifactId="$ARTIFACT_ID" -Dversion="$VERSION" -Dpackaging=jar

# Step 2: Add the custom JAR as a dependency in the pom.xml
# This is already in the pom file

#cat << EOF >> pom.xml
#<dependency>
#    <groupId>$GROUP_ID</groupId>
#    <artifactId>$ARTIFACT_ID</artifactId>
#    <version>$VERSION</version>
#</dependency>
#EOF
