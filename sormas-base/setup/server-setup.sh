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

#!/bin/sh

# DEVELOPMENT ENVIRONMENT OR PRODUCTION/TEST SERVER?
echo "Are you setting up a local system or a server?"
select LS in "Local" "Server"; do
    case $LS in
        Local ) DEV_SYSTEM=true; break;;
        Server ) DEV_SYSTEM=false; break;;
    esac
done

if [ -d "/c/Windows" ]; then
	WINDOWS=true
else
	WINDOWS=false
fi

# DIRECTORIES
if [ ${WINDOWS} = true ]; then
	ROOT_PREFIX=/c
	TEMP_DIR=${ROOT_PREFIX}/opt/sormas-temp
	GENERATED_DIR=${ROOT_PREFIX}/opt/sormas-generated
else 
	ROOT_PREFIX=
	USER_NAME=payara
	TEMP_DIR=${ROOT_PREFIX}/home/${USER_NAME}/sormas-temp
	GENERATED_DIR=${ROOT_PREFIX}/home/${USER_NAME}/sormas-generated
	DOWNLOAD_DIR=${ROOT_PREFIX}/var/www/sormas/downloads
fi

PAYARA_HOME=${ROOT_PREFIX}/opt/payara5
DOMAINS_HOME=${ROOT_PREFIX}/opt/domains

DOMAIN_NAME=sormas
PORT_BASE=6000
PORT_ADMIN=6048
DOMAIN_DIR=${DOMAINS_HOME}/${DOMAIN_NAME}

# DB
DB_SERVER=localhost
DB_PORT=5432
DB_NAME=sormas_db
DB_NAME_AUDIT=sormas_audit_db
DB_USER=sormas_user

# MAIL
MAIL_FROM=dummy@sormas.org

# ------ Config END ------

echo "--- all values set properly?"
if [ ${DEV_SYSTEM} = true ]; then
	echo "System type: Local"
else
	echo "System type: Server"
fi
if [ ${WINDOWS} = true ]; then
	echo "OS: Windows"
else
	echo "OS: Linux"
fi
echo "Temp Directory: ${TEMP_DIR}"
echo "Generated Directory: ${GENERATED_DIR}"
echo "Payara Home: ${PAYARA_HOME}"
echo "Domain Directory: ${DOMAIN_DIR}"
echo "Port Base: ${PORT_BASE}"
echo "Admin Port: ${PORT_ADMIN}"

read -p "Press [Enter] to continue..."

if [ -d "$DOMAIN_DIR" ]; then
	echo "The directory/domain $DOMAIN_DIR already exists. Please remove it and restart the script."
	exit 1
fi

# create needed directories and set user rights
mkdir -p ${PAYARA_HOME}
mkdir -p ${DOMAINS_HOME}
mkdir -p ${TEMP_DIR}
mkdir -p ${GENERATED_DIR}

if [ ${WINDOWS} != true ]; then
	mkdir -p ${DOWNLOAD_DIR}

	adduser ${USER_NAME}
	setfacl -m u:${USER_NAME}:rwx ${DOMAINS_HOME}
	setfacl -m u:${USER_NAME}:rwx ${TEMP_DIR}
	setfacl -m u:${USER_NAME}:rwx ${GENERATED_DIR}

	setfacl -m u:postgres:rwx ${TEMP_DIR} 
	setfacl -m u:postgres:rwx ${GENERATED_DIR}
fi

# check Java version
JAVA_VERSION=$(javac -version 2>&1 | sed -n 's/.*\.\(.*\)\..*/\1/p;')
if [ "${JAVA_VERSION}" -eq 8 ]; then
    echo "Found Java ${JAVA_VERSION}."
elif [ "${JAVA_VERSION}" -gt 8 ]; then
    echo "Found Java ${JAVA_VERSION} - this version may be too new - we can't guarantee that everything is working as expected."
elif [ -z "${JAVA_VERSION}" ]; then
	echo "ERROR: No Java Development Kit found on your system."
	exit 1
else
    echo "ERROR: Found Java $VER - this version is too old."
	exit 1
fi 

# download and unzip payara
if [ -d ${PAYARA_HOME}/glassfish ]; then
	echo "Found Payara (${PAYARA_HOME})"
else
	PAYARA_ZIP_FILE="${PAYARA_HOME}/payara-5.192.zip"
	if [ -f ${PAYARA_ZIP_FILE} ]; then
		echo "Payara already downloaded: ${PAYARA_ZIP_FILE}"
	else
		curl -o ${PAYARA_ZIP_FILE} "https://search.maven.org/remotecontent?filepath=fish/payara/distributions/payara/5.192/payara-5.192.zip"
	fi

	unzip -o ${PAYARA_ZIP_FILE} -d ${PAYARA_HOME}
	mv "${PAYARA_HOME}/payara5"/* "${PAYARA_HOME}"
	rm -R "${PAYARA_HOME}/payara5"
	rm -R "${PAYARA_HOME}/glassfish/domains"
fi

# setting ASADMIN_CALL and creating domain
${PAYARA_HOME}/bin/asadmin create-domain --domaindir ${DOMAINS_HOME} --portbase ${PORT_BASE} --nopassword ${DOMAIN_NAME}
ASADMIN="${PAYARA_HOME}/bin/asadmin --port ${PORT_ADMIN}"

if [ ${WINDOWS} != true ]; then
	chown -R ${USER_NAME}:${USER_NAME} ${PAYARA_HOME}
fi
read -p "Press [Enter] to continue..."

${PAYARA_HOME}/bin/asadmin start-domain --domaindir ${DOMAINS_HOME} ${DOMAIN_NAME}
read -p "Press [Enter] to continue..."

if [ -z "${DB_PW}" ]; then
	echo "Enter the password for the database user '${DB_USER}'"
	read DB_PW
fi

# General domain settings
${ASADMIN} delete-jvm-options -Xmx512m
${ASADMIN} create-jvm-options -Xmx2048m

# JDBC pool
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.ConnectionPoolDataSource --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME}:serverName=${DB_SERVER}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}DataPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}DataPool jdbc/${DOMAIN_NAME}DataPool

# Pool for audit log
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.XADataSource --datasourceclassname org.postgresql.xa.PGXADataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME_AUDIT}:serverName=${DB_SERVER}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}AuditlogPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}AuditlogPool jdbc/AuditlogPool

${ASADMIN} create-javamail-resource --mailhost localhost --mailuser user --fromaddress ${MAIL_FROM} mail/MailSession

${ASADMIN} create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property "org.glassfish.resources.custom.factory.PropertiesFactory.fileName=\${com.sun.aas.instanceRoot}/sormas.properties" sormas/Properties

cp sormas.properties ${DOMAIN_DIR}
cp logback.xml ${DOMAIN_DIR}/config/
# fixes outdated certificate
cp cacerts.jks ${DOMAIN_DIR}/config/


if [ ${WINDOWS} != true ]; then
	cp payara-sormas /etc/init.d``
	update-rc.d payara-sormas defaults
	
	chown -R ${USER_NAME}:${USER_NAME} ${DOMAIN_DIR}
fi

read -p "Press [Enter] to continue..."

# Logging
${ASADMIN} create-jvm-options -Dlogback.configurationFile=\${com.sun.aas.instanceRoot}/config/logback.xml
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.maxHistoryFiles=14
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=0
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationOnDateChange=true
#${ASADMIN} set-log-levels org.wamblee.glassfish.auth.HexEncoder=SEVERE
#${ASADMIN} set-log-levels javax.enterprise.system.util=SEVERE

read -p "Press [Enter] to continue..."

if [ ${DEV_SYSTEM} != true ]; then
  #make the payara listen to localhost only
  ${ASADMIN} set configs.config.server-config.http-service.virtual-server.server.network-listeners=http-listener-1
  ${ASADMIN} delete-network-listener --target=server-config http-listener-2
  ${ASADMIN} set configs.config.server-config.network-config.network-listeners.network-listener.admin-listener.address=127.0.0.1
  ${ASADMIN} set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.address=127.0.0.1
  ${ASADMIN} set configs.config.server-config.iiop-service.iiop-listener.orb-listener-1.address=127.0.0.1
  ${ASADMIN} set configs.config.server-config.iiop-service.iiop-listener.SSL.address=127.0.0.1
  ${ASADMIN} set configs.config.server-config.iiop-service.iiop-listener.SSL_MUTUALAUTH.address=127.0.0.1
  ${ASADMIN} set configs.config.server-config.jms-service.jms-host.default_JMS_host.host=127.0.0.1
  ${ASADMIN} set configs.config.server-config.admin-service.jmx-connector.system.address=127.0.0.1

  read -p "Press [Enter] to continue..."
fi


${PAYARA_HOME}/bin/asadmin stop-domain --domaindir ${DOMAINS_HOME} ${DOMAIN_NAME}

echo "setup completed. Please run the server using the init.d script for the proper permissions"

echo "Checklist"
echo "  - sormas.properties adjusted to this system?"
echo "  - Apache properly configured?"
