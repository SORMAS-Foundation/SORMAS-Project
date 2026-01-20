#!/bin/bash

echo "Build started at: $(date '+%H:%M:%S.%3N')"

cd ./sormas-base

echo "Executed from path: $(pwd)"

set MAVEN_OPTS=-Xmx4g -Xms2g -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:+UseParallelGC
mvnd.cmd package -o -nsu -pl ../sormas-ear  -Dmaven.test.skip \
-Dmaven.javadoc.skip=true \
-Dmaven.source.skip=true \
-Dmaven.compile.fork=true \
-Dlicense.skip=true

echo "un/deploy sormas-ear"
# asadmin.bat --port 6048 undeploy sormas-ear 2>/dev/null || true
asadmin.bat --port 6048 deploy --force ../sormas-ear/target/sormas-ear.war

END_TIME=$(date +%s)
echo "Build finished: $(date -d @$END_TIME '+%H:%M:%S.%3N')"

