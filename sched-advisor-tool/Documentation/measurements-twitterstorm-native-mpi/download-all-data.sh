#!/bin/sh

exec wget -r -np -nH \
	--cut-dirs=5 --reject 'index.html*' --accept '*.merged.gz' \
	'http://www.fit.vutbr.cz/homes/rychly/juniper-sa/measurement/twitterstorm/'
