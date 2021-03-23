#!/bin/bash
# Execute with ./build_deploy.sh <arg> , e.g. ./build_deploy.sh fast

mvn cargo:stop

# recompile
cd ../sormas-base || exit

if [ "$1" = "wipe" ]; then
  cd ../sormas-cargoserver || exit
  docker-compose down
  docker volume rm sormas-cargoserver_psqldata_cargoserver
  docker-compose up -d
  cd ../sormas-base || exit
  mvn clean install -DskipTests

elif [ "$1" = "test" ]; then
  mvn clean install

elif [ "$1" = "fast" ]; then
  mvn -T 1C install -DskipTests

elif [ "$1" = "verify" ]; then
  mvn verify -DskipTests

else
  mvn install -DskipTests
fi

# restart server
cd ../sormas-cargoserver || exit
docker-compose up -d
mvn cargo:run  -Dcargo.glassfish.domain.debug=true