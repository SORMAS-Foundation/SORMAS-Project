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

#This shell script is the execution entry point for the jenkins job used to populate the system with Persons and Immunization


echo "I will run $1 times the tests"
echo "Script started at:"
date +"%T"

rm -rf ./allureReports
./gradlew clean goJF
for ((i = 1; i <= $1; ++i)); do
  rm -rf ./allure-results
  ./gradlew clean
  echo "Run: $i "
  echo "Started at:"
  date +"%T"
  ./gradlew startTests -Dcucumber.tags="@PersonsAndImmunizations" -Dheadless=true -Dcourgette.threads=9 -DenvConfig=........./data.json
  echo "Finished at:"
  date +"%T"
done
echo "Script finished at:"
date +"%T"

