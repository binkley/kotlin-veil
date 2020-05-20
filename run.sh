#!/bin/sh

jar=target/kotlin-veil-1.0.1-SNAPSHOT-jar-with-dependencies.jar

test -r $jar || ./mvnw -C package

exec java -jar $jar
