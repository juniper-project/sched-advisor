#!/bin/sh

PACKAGE=eu.juniper.sa.tool.utils

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

exec mvn -q --file $(dirname "${0}")/pom.xml \
	exec:java -e -Dexec.mainClass="${PACKAGE}.$(basename ${0} .sh)" -Dexec.args="${1:-eu.juniper.sa.tool.plugins}"

# Example:
# $ REINSTALL=1 ./ClassFinder eu.juniper.sa.tool.plugins
