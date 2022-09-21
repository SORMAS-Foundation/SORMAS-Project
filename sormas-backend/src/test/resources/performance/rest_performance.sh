#!/usr/bin/bash

# This script automates calls to the REST API for performance analysis.
# It is meant to be called in its original location and using the
# cargo server in the same project (file wise) by default.
#
# Endpoints are called as configured in rest_interfaces.txt. E.g., the line
#    cases:1000
# defines a call to /sormas-rest/cases/all/ with batch size 1000
#
# For each call, the relevant log snippet is read to a separate file, which
# then can be processed using the PerformanceLogAnalysisGenerator. Additionally,
# an overall log with times for each REST call is written.
#
# The script accepts an argument to suffix all written files. SO
#    rest_performance.sh <suffix>
# will create a log file rest_<suffix>.log for its execution log along
# with a directory rest_<suffix> containing all log snippets from the execution

SCRIPTDIR=$(dirname -- $0)

# Endpoints to be called and batch sizes are defined in file $ENDPOINTS
ENDPOINTS=$SCRIPTDIR/rest_interfaces.txt

# The SORMAS server's application.debug file is assumed to be in $LOGDIR
LOGDIR=$SCRIPTDIR/../../../../../sormas-cargoserver/target/cargo/configurations/payara/sormas/logs

# Log file snippets for calls to individual REST endpoints are written to $TARGETDIR
TARGETDIR=$SCRIPTDIR/../../../../target/rest_$1
mkdir -p $TARGETDIR

# Execution log of this script is written to file $LOG
LOG=$TARGETDIR/../rest_$1.log
echo > $LOG

while IFS=: read -r interface size
do
	SECONDS=0
	tail -f -n0 $LOGDIR/application.debug > $TARGETDIR/$interface.debug &
	LOGTAIL_PID=$!
	echo "REST $interface ($size) ======================================================================" | head -c70
	echo
	echo "$interface ($size) ........................................................" | head -c30 >> $LOG
	time curl -H 'Authorization: Basic U3Vydk9mZjpTdXJ2T2Zm' http://localhost:6080/sormas-rest/$interface/all/0/$size/NO_LAST_SYNCED_UUID > /dev/null 
	kill -15 $LOGTAIL_PID
	TZ=UTC0 printf '%(%H:%M:%S)T\n' $SECONDS >> $LOG
done < $ENDPOINTS
