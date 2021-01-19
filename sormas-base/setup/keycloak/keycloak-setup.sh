#*******************************************************************************
# SORMAS® - Surveillance Outbreak Response Management & Analysis System
# Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#*******************************************************************************

#!/bin/bash

rm -f keycloak_setup.log
exec > >(tee -ia keycloak_setup.log)
exec 2> >(tee -ia keycloak_setup.log)

echo "# KEYCLOAK FOR SORMAS SERVER SETUP"
echo "# Welcome to the KEYCLOAK SORMAS server setup routine. This script will guide you through the setup of your server."
echo "# If anything goes wrong, please consult the server setup guide or get in touch with the developers."

echo "# Checking system"

if [[ $(expr substr "$(uname -a)" 1 5) = "Linux" ]]; then
	LINUX=true
else
	LINUX=false
fi

if [[ ${LINUX} = true ]]; then
	ROOT_PREFIX=
else
	ROOT_PREFIX=/c
fi

if [ -x "$(command -v docker)" ]; then
  echo "Found docker"
else
  echo "Docker not installed. Please install before setting up Keycloak"
  exit 2
fi

echo "# Checking if the Payara server is up and running"

# Update this variables to match your payara installation
# If the script is ran from the master server-setup.sh script, the variables will be filled automatically
if [[ -z "$DB_PORT" ]]; then
 DB_PORT=5432
fi
if [[ -z "$PORT_BASE" ]]; then
 PORT_BASE=6080
 echo "Using default Payara PORT_BASE ${PORT_BASE}"
fi
if [[ -z "$PORT_ADMIN" ]]; then
  PORT_ADMIN=6048
  echo "Using default Payara PORT_ADMIN ${PORT_ADMIN}"
fi
if [[ -z "$PAYARA_HOME" ]]; then
  PAYARA_HOME=${ROOT_PREFIX}/opt/payara5
  echo "Using default PAYARA_HOME ${PAYARA_HOME}"
fi
if [[ -z "$DOMAINS_HOME" ]]; then
  DOMAINS_HOME=${ROOT_PREFIX}/opt/domains
  echo "Using default DOMAINS_HOME ${DOMAINS_HOME}"
fi
if [[ -z "$DOMAIN_NAME" ]]; then
  DOMAIN_NAME=sormas
  echo "Using default DOMAIN_NAME ${DOMAIN_NAME}"
fi
if [[ -z "$SORMAS_SERVER_URL" ]]; then
  SORMAS_SERVER_URL="localhost:${PORT_BASE}"
  echo "Using default SORMAS_SERVER_URL ${SORMAS_SERVER_URL}"
fi

read -p "Press [Enter] to continue or [Ctrl+C] to cancel and adjust the values..."

if [[ ! -d ${PAYARA_HOME} ]];then
  echo "Payara not found ${PAYARA_HOME}"
  exit 2
fi

${PAYARA_HOME}/bin/asadmin restart-domain --domaindir ${DOMAINS_HOME} ${DOMAIN_NAME}

PAYARA_STATUS=$?

if [[ 0 != $PAYARA_STATUS ]]; then
	echo "ERROR: Cannot start payara. Status ${PAYARA_STATUS}"
	exit 2
fi

ASADMIN="${PAYARA_HOME}/bin/asadmin --port ${PORT_ADMIN}"

# Keycloak settings
KEYCLOAK_VERSION=11.0.0
KEYCLOAK_PORT=7080

DB_HOST=localhost

KEYCLOAK_DB_HOST=host.docker.internal
KEYCLOAK_DB_PORT=5432
KEYCLOAK_DB_NAME=keycloak
KEYCLOAK_DB_USER=keycloak
KEYCLOAK_DB_VENDOR=postgres
KEYCLOAK_ADMIN_USER=admin
KEYCLOAK_ADMIN_PASSWORD=password

KEYCLOAK_SORMAS_UI_SECRET=changeit
KEYCLOAK_SORMAS_REST_SECRET=changeit
KEYCLOAK_SORMAS_BACKEND_SECRET=changeit

echo "Keycloak port: ${KEYCLOAK_PORT}"

echo "Preparing the local Keycloak DB"

while [[ -z "${KEYCLOAK_DB_PASSWORD}" ]]; do
	read -r -p "--- Enter a password for the new database user '${KEYCLOAK_DB_USER}': " KEYCLOAK_DB_PASSWORD
done

cat > setup.sql <<-EOF
CREATE USER ${KEYCLOAK_DB_USER} WITH PASSWORD '${KEYCLOAK_DB_PASSWORD}' CREATEDB;
CREATE DATABASE ${KEYCLOAK_DB_NAME} WITH OWNER = '${KEYCLOAK_DB_USER}' ENCODING = 'UTF8';
EOF

if [[ ${LINUX} = true ]]; then
	# no host is specified as by default the postgres user has only local access
	su postgres -c "psql -p ${DB_PORT} < setup.sql"
else
  if [[ -z ${PSQL} ]]; then
    PSQL_DEFAULT="${PROGRAMFILES//\\/\/}/PostgreSQL/10/"
    echo "--- Enter the name install path of Postgres on your system (default: \"${PSQL_DEFAULT}\":"
    read -r PSQL_DIR
    if [[ -z "${PSQL_DIR}" ]]; then
      PSQL_DIR="${PSQL_DEFAULT}"
    fi
    PSQL="${PSQL_DIR}/bin/psql.exe"
	fi
	if [[ -z ${DB_PG_PW} ]]; then
	  while [[ -z "${DB_PG_PW}" ]]; do
      read -r -p "--- Enter the password for the 'postgres' user of your database: " DB_PG_PW
    done
  fi
	"${PSQL}" --no-password --file=setup.sql "postgresql://postgres:${DB_PG_PW}@${DB_HOST}:${DB_PORT}/postgres"
fi

echo "Running Keycloak as a docker image"

KEYCLOAK_DOCKER_CMD="-e KEYCLOAK_USER=${KEYCLOAK_ADMIN_USER} "
KEYCLOAK_DOCKER_CMD+="-e KEYCLOAK_PASSWORD=${KEYCLOAK_ADMIN_PASSWORD} "
KEYCLOAK_DOCKER_CMD+="-e DB_VENDOR=${KEYCLOAK_DB_VENDOR} "
KEYCLOAK_DOCKER_CMD+="-e DB_ADDR=${KEYCLOAK_DB_HOST} "
KEYCLOAK_DOCKER_CMD+="-e DB_PORT=${KEYCLOAK_DB_PORT} "
KEYCLOAK_DOCKER_CMD+="-e DB_USER=${KEYCLOAK_DB_USER} "
KEYCLOAK_DOCKER_CMD+="-e DB_PASSWORD=${KEYCLOAK_DB_PASSWORD} "
KEYCLOAK_DOCKER_CMD+="-e PROXY_ADDRESS_FORWARDING=true "
KEYCLOAK_DOCKER_CMD+="-e SORMAS_SERVER_URL=${SORMAS_SERVER_URL} "
KEYCLOAK_DOCKER_CMD+="-e KEYCLOAK_SORMAS_UI_SECRET=${KEYCLOAK_SORMAS_UI_SECRET} "
KEYCLOAK_DOCKER_CMD+="-e KEYCLOAK_SORMAS_REST_SECRET=${KEYCLOAK_SORMAS_REST_SECRET} "
KEYCLOAK_DOCKER_CMD+="-e KEYCLOAK_SORMAS_BACKEND_SECRET=${KEYCLOAK_SORMAS_BACKEND_SECRET} "
KEYCLOAK_DOCKER_CMD+="-p ${KEYCLOAK_PORT}:8080 "

KEYCLOAK_DOCKER_CMD+="hzibraunschweig/sormas-keycloak:latest"

docker run -d --name sormas_keycloak ${KEYCLOAK_DOCKER_CMD}

echo "Updating Payara with Keycloak configurations"

${ASADMIN} set-config-property --propertyName=payara.security.openid.clientSecret --propertyValue=${KEYCLOAK_SORMAS_UI_SECRET} --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.clientId --propertyValue=sormas-ui --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.scope --propertyValue=openid --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.providerURI --propertyValue=http://localhost:${KEYCLOAK_PORT}/keycloak/auth/realms/SORMAS --source=domain
${ASADMIN} set-config-property --propertyName=sormas.rest.security.oidc.json --propertyValue="{\"realm\":\"SORMAS\",\"auth-server-url\":\"http://localhost:${KEYCLOAK_PORT}/keycloak/auth\",\"ssl-required\":\"external\",\"resource\":\"sormas-rest\",\"credentials\":{\"secret\":\"${KEYCLOAK_SORMAS_REST_SECRET}\"},\"confidential-port\":0,\"principal-attribute\":\"preferred_username\",\"enable-basic-auth\":true}" --source=domain
${ASADMIN} set-config-property --propertyName=sormas.backend.security.oidc.json --propertyValue="{\"realm\":\"SORMAS\",\"auth-server-url\":\"http://localhost:${KEYCLOAK_PORT}/keycloak/auth/\",\"ssl-required\":\"external\",\"resource\":\"sormas-backend\",\"credentials\":{\"secret\":\"${KEYCLOAK_SORMAS_BACKEND_SECRET}\"},\"confidential-port\":0}" --source=domain

echo "Setup is done and Keycloak is starting up (in case of any error you can go again trough the keycloak_setup.sh script)"
echo "You can start Keycloak by using the following command"
echo "  docker run ${KEYCLOAK_DOCKER_CMD}"
echo "Please make sure to perform the following steps:"
echo "  - Update email settings in Keycloak Admin http://localhost:${KEYCLOAK_PORT}/keycloak/auth"
echo "  - Make sure the clients rootUrl are setup correctly"
echo "  - Create an admin user in Keycloak which can be used to Sync other users"
echo "  - In order to user Keycloak with Payara set 'authentication.provider=KEYCLOAK' property in sormas.properties"
read -p "--- Press [Enter] to continue..."
