
#*******************************************************************************
# SORMAS® - Surveillance Outbreak Response Management & Analysis System
# Copyright © 2016-2018 Helmholtz-Zentrum f�r Infektionsforschung GmbH (HZI)
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

rm setup.log
exec > >(tee -ia setup.log)
exec 2> >(tee -ia setup.log)

echo "# SORMAS SERVER SETUP"
echo "# Welcome to the SORMAS server setup routine. This script will guide you through the setup of your server."
echo "# If anything goes wrong, please consult the server setup guide or get in touch with the developers."

# The Java JDK for the payara server (note that spaces in the path are not supported by payara at the moment)
#AS_JAVA_NATIVE='C:\zulu-11'
#AS_JAVA_NATIVE='/opt/zulu-11'
#AS_JAVA_NATIVE=/usr/lib/jvm/zulu11/

PAYARA_VERSION=5.192

if [[ $(expr substr "$(uname -a)" 1 5) = "Linux" ]]; then
	LINUX=true
else
	LINUX=false
fi

# DIRECTORIES
if [[ ${LINUX} = true ]]; then
	ROOT_PREFIX=${HOME}
	# make sure to update payara-sormas script when changing the user name
	USER_NAME=$(whoami)
	DOWNLOAD_DIR=${ROOT_PREFIX}/opt/sormas/downloads
else
	ROOT_PREFIX=/c
fi

TEMP_DIR=${ROOT_PREFIX}/opt/sormas/temp
DOCUMENTS_DIR=${ROOT_PREFIX}/opt/sormas/documents
GENERATED_DIR=${ROOT_PREFIX}/opt/sormas/generated
CUSTOM_DIR=${ROOT_PREFIX}/opt/sormas/custom
PAYARA_HOME=${ROOT_PREFIX}/opt/payara5
DOMAINS_HOME=${ROOT_PREFIX}/opt/domains

DOMAIN_NAME=sormas
PORT_BASE=6000
PORT_ADMIN=6048
DOMAIN_DIR=${DOMAINS_HOME}/${DOMAIN_NAME}

# DB
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sormas_db
DB_NAME_AUDIT=sormas_audit_db
# Name of the database user; DO NOT CHANGE THIS!
DB_USER=sormas_user
DB_PW=sormas

# ------ Config END ------

echo "--- Please confirm that all values are set properly:"
echo "System type: Local"
if [[ ${LINUX} = true ]]; then
	echo "OS: Linux"
else
	echo "OS: Windows"
fi
echo "Custom Java JDK: ${AS_JAVA_NATIVE}"
echo "Payara: ${PAYARA_VERSION}"
echo "Temp directory: ${TEMP_DIR}"
echo "Directory for documents: ${DOCUMENTS_DIR}"
echo "Directory for generated files: ${GENERATED_DIR}"
echo "Directory for custom files: ${CUSTOM_DIR}"
echo "Payara home: ${PAYARA_HOME}"
echo "Domain directory: ${DOMAIN_DIR}"
echo "Base port: ${PORT_BASE}"
echo "Admin port: ${PORT_ADMIN}"
echo "---"
read -p "Press [Enter] to continue or [Ctrl+C] to cancel and adjust the values..."

if [[ -d "${DOMAIN_DIR}" ]]; then
	echo "The directory/domain $DOMAIN_DIR already exists. Please remove it and restart the script."
	exit 1
fi

echo "Starting server setup..."

# Create needed directories and set user rights
mkdir -p "${PAYARA_HOME}"
mkdir -p "${DOMAINS_HOME}"
mkdir -p "${TEMP_DIR}"
mkdir -p "${DOCUMENTS_DIR}"
mkdir -p "${GENERATED_DIR}"
mkdir -p "${CUSTOM_DIR}"

if [[ ${LINUX} = true ]]; then
	mkdir -p "${DOWNLOAD_DIR}"

	setfacl -m u:${USER_NAME}:rwx "${DOMAINS_HOME}"
	setfacl -m u:${USER_NAME}:rwx "${TEMP_DIR}"
	setfacl -m u:${USER_NAME}:rwx "${DOCUMENTS_DIR}"
	setfacl -m u:${USER_NAME}:rwx "${GENERATED_DIR}"
	setfacl -m u:${USER_NAME}:rwx "${CUSTOM_DIR}"

	setfacl -m u:postgres:rwx "${TEMP_DIR}"
	setfacl -m u:postgres:rwx "${DOCUMENTS_DIR}"
	setfacl -m u:postgres:rwx "${GENERATED_DIR}"
	setfacl -m u:postgres:rwx "${CUSTOM_DIR}"
fi

# Download and unzip payara
if [[ -d "${PAYARA_HOME}/glassfish" ]]; then
	echo "Found Payara (${PAYARA_HOME})"
else
	PAYARA_ZIP_FILE_NAME="payara-${PAYARA_VERSION}.zip"
	PAYARA_ZIP_FILE="${PAYARA_HOME}/${PAYARA_ZIP_FILE_NAME}"
	if [[ -f "${PAYARA_ZIP_FILE}" ]]; then
		echo "Payara already downloaded: ${PAYARA_ZIP_FILE}"
	else
		echo "Downloading Payara 5..."
		PAYARA_DOWNLOAD_URL="https://search.maven.org/remotecontent?filepath=fish/payara/distributions/payara/${PAYARA_VERSION}/${PAYARA_ZIP_FILE_NAME}"
		if [[ ${LINUX} = true ]]; then
			wget -O "${PAYARA_ZIP_FILE}" "${PAYARA_DOWNLOAD_URL}"
		else
			curl -L -o "${PAYARA_ZIP_FILE}" "${PAYARA_DOWNLOAD_URL}"
		fi
	fi

	echo "Unzipping Payara..."
	unzip -q -o "${PAYARA_ZIP_FILE}" -d "${PAYARA_HOME}"
	mv "${PAYARA_HOME}/payara5"/* "${PAYARA_HOME}"
	rm -R "${PAYARA_HOME}/payara5"
	rm -R "${PAYARA_HOME}/glassfish/domains"
fi

ASENV_PATH_LINUX="${PAYARA_HOME}/glassfish/config/asenv.conf"
ASENV_PATH_WINDOWS="${PAYARA_HOME}/glassfish/config/asenv.bat"

if [[ ${LINUX} = true ]]; then
	ASENV_PATH="${ASENV_PATH_LINUX}"
else
	ASENV_PATH="${ASENV_PATH_WINDOWS}"
fi

# Identify JDK
if [[ -z "${PAYARA_ZIP_FILE}" ]]; then
	#payara already installed
	while read line; do
		if [[ "$line" =~ ^AS_JAVA.* ]]; then
			AS_JAVA_NATIVE=$(echo "${line#*=}"| tr -d '"')
		fi
	done < "${ASENV_PATH}"
fi

if [[ -n "${AS_JAVA_NATIVE}" ]]; then
	if [[ ${LINUX} = true ]]; then
		AS_JAVA="$AS_JAVA_NATIVE"
	else
		AS_JAVA=$(printf "/$AS_JAVA_NATIVE" | sed 's/:\?\\/\//g')
	fi
fi

if [[ -z "${AS_JAVA}" ]]; then
	JAVAC="javac"
else
	JAVAC="${AS_JAVA}/bin/javac"
fi

# Check Java JDK
JAVA_JDK_VERSION=11
JAVA_VERSION=$("${JAVAC}" -version 2>&1 | sed 's/^.\+ //;s/^1\.//;s/[^0-9].*//')
if [[ ! "${JAVA_VERSION}" =~ ^[0-9]+$ ]]; then
	if [[ -z "${PAYARA_ZIP_FILE}" ]]; then
		if [[ -z "${AS_JAVA}" ]]; then
			echo "ERROR: No Java JDK found. Please install a Java ${JAVA_JDK_VERSION} JDK or specify the JDK you want to use by adding AS_JAVA={PATH_TO_YOUR_JAVA_DIRECTORY} to ${ASENV_PATH}."
		else
			echo "ERROR: No Java JDK found in the path specified in ${ASENV_PATH}. Please adjust the value of the AS_JAVA entry."
		fi
	else
		if [[ -z "${AS_JAVA}" ]]; then
			echo "ERROR: No Java JDK found. Please install a Java ${JAVA_JDK_VERSION} JDK or specify the JDK you want to use by specifying AS_JAVA_NATIVE variable in this script."
		else
			echo "ERROR: No Java JDK found in the path specified in this script. Please adjust the value of the AS_JAVA_NATIVE variable."
		fi
	fi
	exit 1
elif [[ "${JAVA_VERSION}" -eq "${JAVA_JDK_VERSION}" ]]; then
	echo "Found Java ${JAVA_VERSION} JDK."
elif [[ "${JAVA_VERSION}" -gt "${JAVA_JDK_VERSION}" ]]; then
	read -p "Found Java ${JAVA_VERSION} JDK - This version may be too new, SORMAS functionality cannot be guaranteed. Consider downgrading to Java ${JAVA_JDK_VERSION} JDK and restarting the script. Press [Enter] to continue or [Ctrl+C] to cancel."
else
	echo "ERROR: Found Java ${JAVA_VERSION} JDK - This version is too old."
	exit 1
fi

if [[ -n "${PAYARA_ZIP_FILE}" ]] && [[ -n "${AS_JAVA}" ]]; then

	#set Java JDK for payara
	printf "AS_JAVA=\"${AS_JAVA}\"" >> ${ASENV_PATH_LINUX}
	if [[ ${LINUX} != true ]]; then
		printf "set AS_JAVA=${AS_JAVA_NATIVE}" >> ${ASENV_PATH_WINDOWS}
	fi
fi

# Set up the database
echo "Starting database setup..."

while [[ -z "${DB_PW}" ]]; do
	read -p "--- Enter a password for the new database user '${DB_USER}': " DB_PW
done

echo "skip sql, it's init is done in the docker image"

echo "---"
read -p "Database setup completed. Please check the output for any error. Press [Enter] to continue or [Ctrl+C] to cancel."


# Setting ASADMIN_CALL and creating domain
echo "Creating domain for Payara..."
"${PAYARA_HOME}/bin/asadmin" create-domain --domaindir "${DOMAINS_HOME}" --portbase "${PORT_BASE}" --nopassword "${DOMAIN_NAME}"
ASADMIN="${PAYARA_HOME}/bin/asadmin --port ${PORT_ADMIN}"

if [[ ${LINUX} = true ]]; then
	chown -R "${USER_NAME}:${USER_NAME}" "${PAYARA_HOME}"
fi

echo "Copying servlerlibs to ${DOMAIN_DIR}"
ant -buildfile ../build.xml deploy-serverlibs

${PAYARA_HOME}/bin/asadmin start-domain --domaindir ${DOMAINS_HOME} ${DOMAIN_NAME}

if [[ 0 != $? ]]; then
	echo "ERROR: Payara domain failed to start."
	exit 2
fi

echo "--- Enter the email sender address that is used for all mails generated by the system:"
while [[ -z "${MAIL_FROM}" ]]; do
	read MAIL_FROM
done

echo "Configuring domain..."

# General domain settings
${ASADMIN} delete-jvm-options -Xmx512m
${ASADMIN} create-jvm-options -Xmx4096m

# JDBC pool
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.ConnectionPoolDataSource --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME}:serverName=${DB_HOST}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}DataPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}DataPool jdbc/${DOMAIN_NAME}DataPool

# Pool for audit log
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.XADataSource --datasourceclassname org.postgresql.xa.PGXADataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME_AUDIT}:serverName=${DB_HOST}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}AuditlogPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}AuditlogPool jdbc/AuditlogPool

${ASADMIN} create-javamail-resource --mailhost localhost --mailuser user --fromaddress "${MAIL_FROM}" mail/MailSession

${ASADMIN} create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property "org.glassfish.resources.custom.factory.PropertiesFactory.fileName=\${com.sun.aas.instanceRoot}/sormas.properties" sormas/Properties

cp ../setup/sormas.properties "${DOMAIN_DIR}"
cp ../setup/start-payara-sormas.sh "${DOMAIN_DIR}"
cp ../setup/stop-payara-sormas.sh "${DOMAIN_DIR}"
cp ../setup/logback.xml ${DOMAIN_DIR}/config/
if [[ ${DEV_SYSTEM} = true ]] && [[ ${LINUX} != true ]]; then
	# Fixes outdated certificate - don't do this on linux systems!
	cp ../setup/cacerts.jks.bin "${DOMAIN_DIR}/config/cacerts.jks"
fi
cp ../setup/loginsidebar.html "${CUSTOM_DIR}"
cp ../setup/loginsidebar-header.html "${CUSTOM_DIR}"
cp ../setup/logindetails.html "${CUSTOM_DIR}"
cp ../setup/loginmain.html "${CUSTOM_DIR}"


read -p "--- Press [Enter] to continue..."

# Logging
echo "Configuring logging..."
${ASADMIN} create-jvm-options "-Dlogback.configurationFile=\${com.sun.aas.instanceRoot}/config/logback.xml"
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.maxHistoryFiles=14
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=0
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationOnDateChange=true
#${ASADMIN} set-log-levels org.wamblee.glassfish.auth.HexEncoder=SEVERE
#${ASADMIN} set-log-levels javax.enterprise.system.util=SEVERE

# don't stop the domain, because we need it running for the update script
#read -p "--- Press [Enter] to continue..."
#"${PAYARA_HOME}/bin/asadmin" stop-domain --domaindir "${DOMAINS_HOME}" "${DOMAIN_NAME}"

bash ${DOMAIN_DIR}/stop-payara-sormas.sh

echo "Server setup completed."
echo "Commands to start and stop the domain: "
echo "${DOMAIN_DIR}/start-payara-sormas.sh"
echo "${DOMAIN_DIR}/stop-payara-sormas.sh"

echo "---"
echo "Please make sure to perform the following steps:"
echo "  - Adjust the ${DOMAIN_DIR}/sormas.properties file to your system"
echo "  - Execute the r-setup.sh file to enable disease network diagrams"
echo "  - Build and deploy war and ear"
