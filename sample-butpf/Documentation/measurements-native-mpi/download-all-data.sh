#!/bin/sh

exec wget -r -np -nH \
	--cut-dirs=5 --reject 'index.html*' --accept '*.gz' \
	'http://www.fit.vutbr.cz/homes/rychly/juniper-sa/measurement/native-sample-butpf/'
