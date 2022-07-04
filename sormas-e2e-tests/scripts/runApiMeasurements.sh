#!/bin/sh
#
# SORMAS® - Surveillance Outbreak Response Management & Analysis System
# Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <https://www.gnu.org/licenses/>.
#

#This shell script is the execution entry point for the jenkins job used to meassure the loading time for all main pages

echo "Script started at:"
date +"%T"

echo "Deleting allure report folder..."
rm -rf ./allureReports
echo "Deleting custom report"
rm -rf ./customReports/apiMeasurements/customReport.html
echo "Cleaning old results from results.txt file"
cat /dev/null > ./customReports/apiMeasurements/data/results.txt
echo "Executing gradle clean..."
./gradlew clean goJF
echo "Starting all BDD tests under @ApiMeasurements tag..."
./gradlew startTests -Dcucumber.tags="@ApiMeasurements" -Dheadless=true -Dcourgette.threads=1 -DenvConfig=/srv/dockerdata/jenkins_new/sormas-files/envData_test.json --stacktrace --debug --scan