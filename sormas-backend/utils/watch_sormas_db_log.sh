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
#

#!/usr/bin/bash

# This scripts prints out queries from the SORMAS postgres container's sql log.
# Before, log_min_duration_statement is set to 0, so all queries are logged.
# Adapt the value of log_min_duration_statement to your needs.
# Parameters - if present - are replaced in the query, which is also prefixed
# to use EXPLAIN, so the script's output can be used to analyze long running queries.
# To find relevant queries, use 'grep "-- Duration"' on the script's output.
#
# *** This script is experimental ***

PGPASSWORD=sormas psql --host 127.0.0.1 --port 5434 -U postgres sormas_db << EOF
alter system set log_min_duration_statement = 0;
SELECT pg_reload_conf();
EOF

docker logs --follow --tail 0 sormas-cargoserver_postgres_1 2>&1 | sed '/LOG: *duration: *[0-9.]* ms *execute [^:]*:/!d;N;s/^.*duration: *\([0-9.]* ms\) *execute [^:]*: \(.*\)\n.*DETAIL:  parameters: \(.*\)$/\2 ---- \3 ---- \1/' | while read logline
do
  [[ $logline =~ ^(.*)\ +----\ +(.*)\ +----\ +(.*)$ ]] && query="EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) ${BASH_REMATCH[1]} " && parameters=${BASH_REMATCH[2]} && duration=${BASH_REMATCH[3]}
  if [[ -z "${parameters// }" ]]
  then
    echo "-- Log: $logline"
  else
    replaceParams=`sed "s/\(\\\$[0-9]*\) = \('[^']*'\)\(, \)*/s\/\1\\\\\([^0-9]\\\\\)\/\2\\\\\1\/g;/g;" <<<$parameters`
    replaceParams=`sed 's#s/#s/\\\#g' <<<$replaceParams`
    echo "-- Query: $query"
    echo "-- Parameters: $parameters"
    echo "-- Duration: $duration"
    sed -e $replaceParams <<<$query
  fi
done
