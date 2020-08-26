
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

echo "# SORMAS TO SORMAS CERTIFICATE IMPORT"
echo "# This script imports a certificate into the local truststore, to be used for SORMAS2SORMAS communication"
echo "# If anything goes wrong, please consult the sormas to sormas import guide or get in touch with the developers."

if [[ $(expr substr "$(uname -a)" 1 5) = "Linux" ]]; then
	LINUX=true
else
	LINUX=false
fi

# DIRECTORIES
if [[ ${LINUX} = true ]]; then
	ROOT_PREFIX=
else
	ROOT_PREFIX=/c
fi
if [[ -z "${SORMAS2SORMAS_DIR}" ]]; then
  DEFAULT_SORMAS2SORMAS_DIR="${ROOT_PREFIX}/opt/sormas2sormas"
  if [[ -d "${DEFAULT_SORMAS2SORMAS_DIR}" ]]; then
    SORMAS2SORMAS_DIR="${DEFAULT_SORMAS2SORMAS_DIR}"
  else
    while [[ ! -d "${SORMAS2SORMAS_DIR}" ]]; do
		  read -r -p "Please specify a valid sormas2sormas directory: " SORMAS2SORMAS_DIR
	  done
	  export SORMAS2SORMAS_DIR
  fi
else
  if [[ ! -d "${SORMAS2SORMAS_DIR}" ]]; then
    echo "sormas2sormas directory is invalid: ${SORMAS2SORMAS_DIR}"
    exit 1
  fi
fi

if [[ -z "${SORMAS_PROPERTIES}" ]]; then
  DEFAULT_SORMAS_PROPERTIES_PATH="${ROOT_PREFIX}/opt/domains/sormas/sormas.properties"
  if [[ -f "${DEFAULT_SORMAS_PROPERTIES_PATH}" ]]; then
    SORMAS_PROPERTIES="${DEFAULT_SORMAS_PROPERTIES_PATH}"
  else
    while [[ ! -f "${SORMAS_PROPERTIES}" ]]; do
		  read -r -p "Please specify a valid sormas properties path: " SORMAS_PROPERTIES
	  done
	  export SORMAS_PROPERTIES
  fi
else
  if [[ ! -f "${SORMAS_PROPERTIES}" ]]; then
    echo "sormas properties file is invalid: ${SORMAS_PROPERTIES}"
    exit 1
  fi
fi

TRUSTSTORE_FILE_NAME=sormas2sormas.truststore.p12
TRUSTSTORE_FILE=${SORMAS2SORMAS_DIR}/${TRUSTSTORE_FILE_NAME}
NEW_TRUSTSTORE=false
if [ ! -f "${TRUSTSTORE_FILE}" ]; then
  NEW_TRUSTSTORE=true
  echo "${TRUSTSTORE_FILE_NAME} not found. A new truststore file will be created."
fi

while [[ -z "${SORMAS_S2S_TRUSTSTORE_PASS}" ]] || [[ ${#SORMAS_S2S_TRUSTSTORE_PASS} -lt 6 ]]; do
  if [[ ${NEW_TRUSTSTORE} = true ]]; then
    read -sp "Please provide the password for the new truststore (at least 6 characters): " SORMAS_S2S_TRUSTSTORE_PASS
  else
    read -sp "Please provide the password for the truststore: " SORMAS_S2S_TRUSTSTORE_PASS
  fi
  echo
done

read -p "Please provide the file name of the certificate to import. It should be located inside the sormas2sormas folder: " CRT_FILE_NAME
CRT_FILE=${SORMAS2SORMAS_DIR}/${CRT_FILE_NAME}
while [[ -z "${CRT_FILE_NAME}" ]] || [ ! -f "${CRT_FILE}" ]; do
  echo "File not found in ${SORMAS2SORMAS_DIR} folder."
  read -p "Please provide the file name of the certificate to import. It should be located inside the sormas2sormas folder: " CRT_FILE_NAME
  CRT_FILE=${SORMAS2SORMAS_DIR}/${CRT_FILE_NAME}
done

# get new certificate alias, which is the same as the Common Name (CN)
ALIAS=$(openssl x509 -noout -subject -nameopt multiline -in "${CRT_FILE}" | sed -n 's/ *commonName *= //p')

# import crt
echo "Importing certificate into truststore..."
keytool -importcert -trustcacerts -noprompt -keystore "${TRUSTSTORE_FILE}" -storetype pkcs12 -alias ${ALIAS} -storepass "${SORMAS_S2S_TRUSTSTORE_PASS}" -file "${CRT_FILE}"

if [[ ${NEW_TRUSTSTORE} = true ]]; then
  #update properties
  if [[ -z ${SORMAS_PROPERTIES} ]]; then
	  echo "sormas.properties file was not found."
    echo "Please add the following properties to the sormas.properties file:"
    echo "sormas2sormas.truststoreName=${TRUSTSTORE_FILE_NAME}"
    echo "sormas2sormas.truststorePass=${SORMAS_S2S_TRUSTSTORE_PASS}"
  else
    # remove existing properties and empty spaces at end of file
    sed -i "/^# SORMAS to SORMAS truststore data/d" "${SORMAS_PROPERTIES}"
    sed -i "/^sormas2sormas\.truststoreName/d" "${SORMAS_PROPERTIES}"
    sed -i "/^sormas2sormas\.truststorePass/d" "${SORMAS_PROPERTIES}"
    sed -i -e :a -e '/^\n*$/{$d;N;};/\n$/ba' "${SORMAS_PROPERTIES}"
    # add new properties
    {
      echo;
      echo "# SORMAS to SORMAS truststore data";
      echo "sormas2sormas.truststoreName=${TRUSTSTORE_FILE_NAME}";
      echo "sormas2sormas.truststorePass=${SORMAS_S2S_TRUSTSTORE_PASS}";
    } >> "${SORMAS_PROPERTIES}"
  fi
fi

echo "The script finished executing. Please check for any errors."
