#!/bin/bash
here=$(dirname $0)

pushd $here/../sormas-ui/
mvn clean install -DskipTests
popd

sormasUi=`sh $here/target/cargo/installs/payara-5.194/payara5/glassfish/bin/asadmin list-applications --user admin --port 6048 --passwordfile $here/password.txt | grep sormas-ui | sed "s/^\([^ ]*\) .*$/\1/"`
echo "--- redeploy $sormasUi ---"
sh $here/target/cargo/installs/payara-5.194/payara5/glassfish/bin/asadmin --user admin --port 6048 --passwordfile $here/password.txt undeploy $sormasUi
sh $here/target/cargo/installs/payara-5.194/payara5/glassfish/bin/asadmin --user admin --port 6048 --passwordfile $here/password.txt deploy ../sormas-ui/target/sormas-ui.war
