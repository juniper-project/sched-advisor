#!/bin/sh

PACKAGE=eu.juniper.sa.monitoring.resources

[[ -n "${REINSTALL}" ]] \
&& mvn -q --file $(dirname "${0}")/../pom.xml clean install

TMPDIR=/tmp/$(basename "${0}").tmp$$
PKGDIR="${PACKAGE//.//}"

mkdir -pv "${TMPDIR}/${PKGDIR}" >&2
cp -v $(dirname "${0}")/target/classes/${PKGDIR}/*.class "${TMPDIR}/${PKGDIR}" >&2

JAVA=${1:-java}
shift

echo
${JAVA} -cp "${TMPDIR}" "${PACKAGE}.$(basename ${0} .sh)" $*
echo

rm -vrf "${TMPDIR}" >&2
