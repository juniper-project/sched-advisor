#!/bin/sh

PACKAGE=eu.juniper.sa.deployment.plan

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

exec mvn -q --file $(dirname "${0}")/pom.xml \
	exec:java -e -Dexec.mainClass="${PACKAGE}.$(basename ${0} .sh)" -Dexec.args="$*"
