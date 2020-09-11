
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

echo "# SORMAS TO SORMAS NEW CERTIFICATE GENERATION"
echo "# This script generates a new self signed certificate, to be used for SORMAS2SORMAS and SurvNet communication"
echo "# If anything goes wrong, please consult the certificate creation guide or get in touch with the developers."

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
    echo "sormas2sormas directory not found: ${SORMAS2SORMAS_DIR}"
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
    echo "sormas properties file not found: ${SORMAS_PROPERTIES}"
    exit 1
  fi
fi

while [[ -z "${SORMAS_S2S_CERT_PASS}" ]] || [[ ${#SORMAS_S2S_CERT_PASS} -lt 6 ]]; do
  read -sp "Please provide a password for the certificate (at least 6 characters): " SORMAS_S2S_CERT_PASS
  echo
done

while [[ -z "${SORMAS_S2S_CERT_CN}" ]]; do
  read -p "Please provide a Common Name (CN) for the certificate: " SORMAS_S2S_CERT_CN
done

while [[ -z "${SORMAS_S2S_CERT_ORG}" ]]; do
  read -p "Please provide an Organization (O) for the certificate: " SORMAS_S2S_CERT_ORG
done

if [[ ${LINUX} = true ]]; then
  CERT_SUBJ="/CN=${SORMAS_S2S_CERT_CN}/OU=SORMAS/O=${SORMAS_S2S_CERT_ORG}"
else
  CERT_SUBJ="//CN=${SORMAS_S2S_CERT_CN}\OU=SORMAS\O=${SORMAS_S2S_CERT_ORG}"
fi
echo "The certificate will be generated with the following subject:"
echo "CN=${SORMAS_S2S_CERT_CN},OU=SORMAS,O=${SORMAS_S2S_CERT_ORG}"
read -p "Press [Enter] to continue or [Ctrl+C] to cancel."

PEM_FILE=${SORMAS2SORMAS_DIR}/sormas2sormas.privkey.pem
P12_FILE=${SORMAS2SORMAS_DIR}/sormas2sormas.keystore.p12
CRT_FILE=${SORMAS2SORMAS_DIR}/sormas2sormas.cert.crt
CSV_FILE=${SORMAS2SORMAS_DIR}/server-access-data.csv

# generate private key and self signed certificate
openssl req -sha256 -newkey rsa:4096 -passout pass:"${SORMAS_S2S_CERT_PASS}" -keyout "${PEM_FILE}" -x509 -passin pass:"${SORMAS_S2S_CERT_PASS}" -days 1095 -subj "${CERT_SUBJ}" -out "${CRT_FILE}"

# add to encrypted keystore
openssl pkcs12 -export -inkey "${PEM_FILE}" -out "${P12_FILE}" -passin pass:"${SORMAS_S2S_CERT_PASS}" -password pass:"${SORMAS_S2S_CERT_PASS}" -name "${SORMAS_S2S_CERT_CN}" -in "${CRT_FILE}"

rm "${PEM_FILE}"

echo "Generating server access data CSV"
echo -e "\"${SORMAS_S2S_CERT_CN}\",\"${SORMAS_S2S_CERT_ORG}\",\n" > "${CSV_FILE}"

# remove existing properties and empty spaces at end of file
sed -i "/^# Key data for the generated SORMAS to SORMAS certificate/d" "${SORMAS_PROPERTIES}"
sed -i "/^sormas2sormas\.keyAlias/d" "${SORMAS_PROPERTIES}"
sed -i "/^sormas2sormas\.keystoreName/d" "${SORMAS_PROPERTIES}"
sed -i "/^sormas2sormas\.keystorePass/d" "${SORMAS_PROPERTIES}"
sed -i -e :a -e '/^\n*$/{$d;N;};/\n$/ba' "${SORMAS_PROPERTIES}"
# add new properties
{
  echo;
  echo "# Key data for the generated SORMAS to SORMAS certificate";
  echo "sormas2sormas.keyAlias=${SORMAS_S2S_CERT_CN}"
  echo "sormas2sormas.keystoreName=${P12_FILE}"
  echo "sormas2sormas.keystorePass=${SORMAS_S2S_CERT_PASS}"
} >> "${SORMAS_PROPERTIES}"

echo "The script finished executing. Please check for any errors."
