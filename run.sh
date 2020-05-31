#!/bin/sh

jar=target/kotlin-veil-0-SNAPSHOT-jar-with-dependencies.jar

test -r $jar || ./mvnw -C package

exec java -jar $jar
