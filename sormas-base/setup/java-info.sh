
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


# DEVELOPMENT ENVIRONMENT OR PRODUCTION/TEST SERVER?
echo "--- Are you setting up a local system or a server?"
select LS in "Local" "Server"; do
    case $LS in
        Local ) DEV_SYSTEM=true; DEMO_SYSTEM=true; break;;
        Server ) DEV_SYSTEM=false; break;;
    esac
done

if [[ ${DEV_SYSTEM} != true ]]; then
	echo "--- Is the server meant to be a demo/test or a production server?"
	select LS in "Demo/Test" "Production"; do
		case $LS in
			Demo/Test ) DEMO_SYSTEM=true; break;;
			Production ) DEMO_SYSTEM=false; break;;
		esac
	done
else
	DEMO_SYSTEM=false
fi

# The Java JDK for the payara server
#AS_JAVA_NATIVE='C:\\Program Files\\Zulu\\zulu-8'
#AS_JAVA_NATIVE='/opt/zulu-8'


if [[ $(expr substr "$(uname -a)" 1 5) = "Linux" ]]; then
	LINUX=true
else
	LINUX=false
fi

# DIRECTORIES
if [[ ${LINUX} = true ]]; then
	ROOT_PREFIX=
	# make sure to update payara-sormas script when changing the user name
	USER_NAME=payara
	DOWNLOAD_DIR=${ROOT_PREFIX}/var/www/sormas/downloads
else 
	ROOT_PREFIX=/c
fi

TEMP_DIR=${ROOT_PREFIX}/opt/sormas/temp
GENERATED_DIR=${ROOT_PREFIX}/opt/sormas/generated
CUSTOM_DIR=${ROOT_PREFIX}/opt/sormas/custom
PAYARA_HOME=${ROOT_PREFIX}/opt/payara5
DOMAINS_HOME=${ROOT_PREFIX}/opt/domains

DOMAIN_NAME=sormas
DOMAIN_DIR=${DOMAINS_HOME}/${DOMAIN_NAME}


# ------ Config END ------

echo "--- Please confirm that all values are set properly:"
if [[ ${DEV_SYSTEM} = true ]]; then
	echo "System type: Local"
else
	echo "System type: Server"
fi
if [[ ${LINUX} = true ]]; then
	echo "OS: Linux"
else
	echo "OS: Windows"
fi
echo "Java JDK: ${AS_JAVA_NATIVE}"
echo "Payara: ${PAYARA_VERSION}"
echo "Temp directory: ${TEMP_DIR}"
echo "Directory for generated files: ${GENERATED_DIR}"
echo "Directory for custom files: ${CUSTOM_DIR}"
echo "Payara home: ${PAYARA_HOME}"
echo "Domain directory: ${DOMAIN_DIR}"
echo "---"
read -p "Press [Enter] to continue or [Ctrl+C] to cancel and adjust the values..."

if [[ -d "$DOMAIN_DIR" ]]; then
	echo "The directory/domain $DOMAIN_DIR already exists. Please remove it and restart the script."
	exit 1
fi

echo "Starting server setup..."

# Create needed directories and set user rights
mkdir -p ${PAYARA_HOME}
mkdir -p ${DOMAINS_HOME}
mkdir -p ${TEMP_DIR}
mkdir -p ${GENERATED_DIR}
mkdir -p ${CUSTOM_DIR}

if [[ ${LINUX} = true ]]; then
	mkdir -p ${DOWNLOAD_DIR}

	adduser ${USER_NAME}
	setfacl -m u:${USER_NAME}:rwx ${DOMAINS_HOME}
	setfacl -m u:${USER_NAME}:rwx ${TEMP_DIR}
	setfacl -m u:${USER_NAME}:rwx ${GENERATED_DIR}
	setfacl -m u:${USER_NAME}:rwx ${CUSTOM_DIR}

	setfacl -m u:postgres:rwx ${TEMP_DIR} 
	setfacl -m u:postgres:rwx ${GENERATED_DIR}
	setfacl -m u:postgres:rwx ${CUSTOM_DIR}
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
JAVA_VERSION=$("${JAVAC}" -version 2>&1 | sed 's/^.\+ //;s/^1\.//;s/[^0-9].*//')
if [[ ! ${JAVA_VERSION} =~ '^[0-9]+$' ]]; then
	if [[ -z ${PAYARA_ZIP_FILE} ]]; then
		if [[ -z "${AS_JAVA}" ]]; then
			echo "ERROR: No Java JDK found. Please install a Java 8 JDK or specify the JDK you want to use by adding AS_JAVA={PATH_TO_YOUR_JAVA_DIRECTORY} to ${ASENV_PATH}."
		else
			echo "ERROR: No Java JDK found in the path specified in ${ASENV_PATH}. Please adjust the value of the AS_JAVA entry."
		fi
	else
		if [[ -z "${AS_JAVA}" ]]; then
			echo "ERROR: No Java JDK found. Please install a Java 8 JDK or specify the JDK you want to use by specifying AS_JAVA_NATIVE variable in this script."
		else
			echo "ERROR: No Java JDK found in the path specified in this script. Please adjust the value of the AS_JAVA_NATIVE variable."
		fi
	fi
	exit 1
elif [[ ${JAVA_VERSION} -eq 8 ]]; then
	echo "Found Java ${JAVA_VERSION} JDK."
elif [[ ${JAVA_VERSION} -gt 8 ]]; then
	read -p "Found Java ${JAVA_VERSION} JDK - This version may be too new, SORMAS functionality cannot be guaranteed. Consider downgrading to Java 8 SDK and restarting the script. Press [Enter] to continue or [Ctrl+C] to cancel."
else
	echo "ERROR: Found Java ${JAVA_VERSION} JDK - This version is too old."
	exit 1
fi
