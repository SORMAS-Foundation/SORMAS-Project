#!/usr/bin/env bash
BASE_URL=${BASE_URL:-http://localhost:8080}
docker run -e "JAVA_OPTS=-DbaseUrl=${BASE_URL} -DdurationMin=1 -DrequestPerSecond=10" \
  -e SIMULATION_NAME=gatling.test.example.simulation.ExampleSimulation jecklgamis/gatling-java-example:latest
