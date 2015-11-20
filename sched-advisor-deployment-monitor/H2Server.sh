#!/bin/sh

exec mvn -q exec:java -e -Dexec.mainClass="org.h2.tools.Server" -Dexec.args="$*"
