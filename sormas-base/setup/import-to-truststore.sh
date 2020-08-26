
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

TRUSTSTORE_FILE_NAME=sormas2sormas.truststore.p12

TRUSTSTORE_FILE=${SORMAS2SORMAS_DIR}/${TRUSTSTORE_FILE_NAME}
NEW_TRUSTSTORE=false
if [ ! -f "${TRUSTSTORE_FILE}" ]; then
  NEW_TRUSTSTORE=true
  echo "${TRUSTSTORE_FILE_NAME} not found. A new truststore file will be created."
fi

while [[ -z "${SORMAS_S2S_TRUSTSTORE_PASS}" ]] || [[ ${#SORMAS_S2S_TRUSTSTORE_PASS} -lt 4 ]]; do
  read -sp "Please provide the password for the truststore (at least 4 characters): " SORMAS_S2S_TRUSTSTORE_PASS
  echo
done

read -p "Please provide the file name of the certificate to import. It should be located inside the sormas2sormas folder: " CRT_FILE_NAME
CRT_FILE=${SORMAS2SORMAS_DIR}/${CRT_FILE_NAME}
while [[ -z "${CRT_FILE_NAME}" ]] || [ ! -f "${CRT_FILE}" ]; do
  echo "File not found in ${SORMAS2SORMAS_DIR} folder."
  read -p "Please provide the file name of the certificate to import. It should be located inside the sormas2sormas folder: " CRT_FILE_NAME
done

# import crt
echo "Importing certificate into truststore..."
if [[ ${NEW_TRUSTSTORE} = true ]]; then
  openssl pkcs12 -export -nokeys -out "${TRUSTSTORE_FILE}" -password pass:"${SORMAS_S2S_TRUSTSTORE_PASS}" -in "${CRT_FILE}"
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
else
  # export existing certificates to temporary file
  TEMP_FILE=${SORMAS2SORMAS_DIR}/tempcert.pem
  openssl pkcs12 -in "${TRUSTSTORE_FILE}" -password pass:"${SORMAS_S2S_TRUSTSTORE_PASS}" -out ${TEMP_FILE}

  # create new truststore with the new certificate and the certificates from the temporary file
  openssl pkcs12 -export -nokeys -out "${TRUSTSTORE_FILE}" -password pass:"${SORMAS_S2S_TRUSTSTORE_PASS}" -in "${CRT_FILE}" -certfile ${TEMP_FILE}
  rm ${TEMP_FILE}
fi

echo "The script finished executing. Please check for any errors."
