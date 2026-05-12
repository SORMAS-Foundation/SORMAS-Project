#!/bin/bash
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

# Update these variables to match your payara installation
# If the script is ran from the master server-setup.sh script, the variables will be filled automatically
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
if [[ -z "$KEYCLOAK_PORT" ]]; then
  export $(grep KEYCLOAK_PORT= < .env)
fi
echo "KEYCLOAK_PORT: ${KEYCLOAK_PORT}"
if [[ -z "$KEYCLOAK_SORMAS_UI_SECRET" ]]; then
  export $(grep KEYCLOAK_SORMAS_UI_SECRET= < .env)
fi
if [[ -z "$KEYCLOAK_SORMAS_REST_SECRET" ]]; then
  export $(grep KEYCLOAK_SORMAS_REST_SECRET= < .env)
fi
if [[ -z "$KEYCLOAK_SORMAS_BACKEND_SECRET" ]]; then
  export $(grep KEYCLOAK_SORMAS_BACKEND_SECRET= < .env)
fi

if [[ -z "$KEYCLOAK_HOST" ]]; then
  export KEYCLOAK_HOST="localhost:${KEYCLOAK_PORT}"
fi
echo "KEYCLOAK_HOST: ${KEYCLOAK_HOST}"

if [[ -z "$DEV_SYSTEM" ]]; then
  DEV_SYSTEM=true
fi
if [[ ${DEV_SYSTEM} = true ]]; then
  KEYCLOAK_URL="http://${KEYCLOAK_HOST}"
else
  KEYCLOAK_URL="https://${KEYCLOAK_HOST}"
fi

read -p "--- Press [Enter] to continue..."
echo "Running Keycloak and keycloak database docker containers"

docker compose up --detach


echo "Updating Payara with Keycloak configurations"

${ASADMIN} set-config-property --propertyName=payara.security.openid.clientId --propertyValue=sormas-ui --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.clientSecret --propertyValue=${KEYCLOAK_SORMAS_UI_SECRET} --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.scope --propertyValue=openid --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.providerURI --propertyValue=${KEYCLOAK_URL}/keycloak/realms/SORMAS --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.provider.notify.logout --propertyValue=true --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.logout.redirectURI --propertyValue=http://${SORMAS_SERVER_URL}/sormas-ui
${ASADMIN} set-config-property --propertyName=sormas.rest.security.oidc.json --propertyValue="{\"realm\":\"SORMAS\",\"auth-server-url\":\"${KEYCLOAK_URL}/keycloak\",\"ssl-required\":\"external\",\"resource\":\"sormas-rest\",\"credentials\":{\"secret\":\"${KEYCLOAK_SORMAS_REST_SECRET}\"},\"confidential-port\":0,\"principal-attribute\":\"preferred_username\",\"enable-basic-auth\":true}" --source=domain
${ASADMIN} set-config-property --propertyName=sormas.backend.security.oidc.json --propertyValue="{\"realm\":\"SORMAS\",\"auth-server-url\":\"${KEYCLOAK_URL}/keycloak\",\"ssl-required\":\"external\",\"resource\":\"sormas-backend\",\"credentials\":{\"secret\":\"${KEYCLOAK_SORMAS_BACKEND_SECRET}\"},\"confidential-port\":0}" --source=domain

echo "Setup is done and Keycloak is starting up (in case of any error you can go again trough the keycloak_setup.sh script)"
echo "You can start the Keycloak container pair by using the following command:"
echo "  docker start keycloak-keycloak-postgres-1 keycloak-keycloak-1"
echo "Or from within the current directory:"
echo "  docker compose up"
echo "Please make sure to perform the following steps:"
echo "  - Update email settings in Keycloak Admin ${KEYCLOAK_URL}/keycloak"
echo "  - Make sure the clients rootUrl are setup correctly - on dev systems https will have to be replaced by http"
echo "  - In order to user Keycloak with Payara set 'authentication.provider=KEYCLOAK' property in sormas.properties"
read -p "--- Press [Enter] to continue..."
