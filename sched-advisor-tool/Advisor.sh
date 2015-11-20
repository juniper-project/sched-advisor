#!/bin/bash

PACKAGE=eu.juniper.sa.tool

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

for I in $*; do
	if [[ "${I:0:2}" == "-D" ]]; then
		JAVA_ARGS+="${I} "
	else
		CLASS_ARGS+="${I} "
	fi
done

exec mvn -q --file $(dirname "${0}")/pom.xml \
	exec:exec -e \
	-Dexec.executable="java" \
	-Dexec.args="-classpath %classpath ${JAVA_ARGS} ${PACKAGE}.$(basename ${0} .sh) ${CLASS_ARGS}"

# Example:
# $ ./Advisor.sh ../juniper-sample-mapreduce/application_model_2maps.xml /tmp/monitor.sql /tmp/advice.xml
# $ ./Advisor.sh ../juniper-sample-mapreduce/application_model_2maps.xml "http://localhost:3000/executions/" /tmp/advice.xml
# $ ./Advisor.sh ../juniper-sample-butpf/Documentation/measurements-native-mpi/deployment_plan_localhost.xml ../juniper-sample-butpf/Documentation/measurements-native-mpi/data.lo/petafuel.sql.statements.1000.gz /tmp/advice.xml
# $ ./Advisor.sh ../juniper-sample-butpf/Documentation/measurements-native-mpi/deployment_plan_localhost.xml ../juniper-sample-butpf/Documentation/measurements-native-mpi/data.lo.ramdisk/petafuel.1000.ram-mon-data.gz /tmp/advice.xml
# $ ./Advisor.sh ../juniper-sample-butpf/Documentation/measurements-native-mpi/deployment_plan_localhost.xml ../juniper-sample-butpf/Documentation/measurements-native-mpi/data.lo.ramdisk/petafuel.1000.ram-src-mon-data.gz /tmp/advice.xml
# $ ./Advisor.sh ../juniper-sample-butpf/Documentation/measurements-native-mpi/deployment_plan_network.xml ../juniper-sample-butpf/Documentation/measurements-native-mpi/data.net/petafuel.1000.merged.gz /tmp/advice.xml
