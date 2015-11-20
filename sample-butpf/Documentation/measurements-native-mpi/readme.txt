=== Measurements of a native MPI implementation of the Petafuel use case by BUT ===


Configuration files (deployment plan XML files, etc.) are in
https://bitbucket.org/rychly/juniper-sa/raw/master/juniper-sample-butpf/Documentation/measurements-native-mpi/

./deployment_plan_localhost.xml
a deployment plan for the execution on a localhost

./deployment_plan_network.xml
a deployment plan for the execution on a MPI cluster with two nodes


Monitoring data files (SQL dump files compressed by GZip) are in
http://www.fit.vutbr.cz/homes/rychly/juniper-sa/measurement/native-sample-butpf/

./data.lo/
measurements from the execution on a localhost

./data.lo.ramdisk/
measurements from the execution on a localhost with a ram-disk for
* monitoring data in the ram-disk in the case of *.ram-mon-data.gz
* source data and monitoring data in the ram-disk in the case of *.ram-src-mon-data.gz

./data.net/
measurements from the execution on a MPI cluster with two nodes (pcskoda and pcmaliulin) as
* measurements from pcskoda in the case of *.pcskoda.gz
* measurements from pcskoda in the case of *.pcmaliulin.gz
* measurements merged from all the nodes in the case of *.merged.gz

Granularities of the measurements are 10/100/1000 records for one run
in the case of *.10.*, *.100.*, and *.1000.*, respectively
(granularity 10 means many small messages while granularity 1000 means a few big messages).
Note that there is a very high monitoring overhead especially for the smaller granularities.
