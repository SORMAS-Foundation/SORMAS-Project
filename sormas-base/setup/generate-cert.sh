
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

if [[ ${LINUX} = true ]]; then
	ROOT_PREFIX=
else
	ROOT_PREFIX=/c
fi

if [[ -z "${SORMAS2SORMAS_DIR}" ]] || [[ ! -d "${SORMAS2SORMAS_DIR}" ]]; then
  DEFAULT_SORMAS2SORMAS_DIR="${ROOT_PREFIX}/opt/sormas2sormas"
  if [[ -d "${DEFAULT_SORMAS2SORMAS_DIR}" ]]; then
    SORMAS2SORMAS_DIR="${DEFAULT_SORMAS2SORMAS_DIR}"
  else
    while [[ ! -d "${SORMAS2SORMAS_DIR}" ]]; do
		  read -r -p "Please specify a valid sormas2sormas directory: " SORMAS2SORMAS_DIR
	  done
	  export SORMAS2SORMAS_DIR
  fi
fi

while [[ -z "${SORMAS_S2S_CERT_PASS}" ]]; do
  read -sp "Please provide a password for the certificate: " SORMAS_S2S_CERT_PASS
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

PEM_NAME=${SORMAS2SORMAS_DIR}/sormas2sormas.privkey.pem
P12_NAME=${SORMAS2SORMAS_DIR}/sormas2sormas.keystore.p12
CRT_NAME=${SORMAS2SORMAS_DIR}/sormas2sormas.cert.crt

# generate private key and self signed certificate
openssl req -sha256 -newkey rsa:4096 -passout pass:"${SORMAS_S2S_CERT_PASS}" -keyout "${PEM_NAME}" -x509 -passin pass:"${SORMAS_S2S_CERT_PASS}" -days 1095 -subj "${CERT_SUBJ}" -out "${CRT_NAME}"

# add to encrypted keystore
openssl pkcs12 -export -inkey "${PEM_NAME}" -out "${P12_NAME}" -passin pass:"${SORMAS_S2S_CERT_PASS}" -password pass:"${SORMAS_S2S_CERT_PASS}" -in "${CRT_NAME}"

rm "${PEM_NAME}"

#update properties
if [[ -z ${SORMAS_PROPERTIES} ]]; then
	echo "sormas.properties file was not found."
  echo "Please add the following properties to the sormas.properties file:"
  echo "sormas2sormas.keyAlias=${CRT_NAME}"
  echo "sormas2sormas.keyPassword=${SORMAS_S2S_CERT_PASS}"
else
  {
  echo;
  echo "# Key data for the generated SORMAS to SORMAS certificate";
  echo "sormas2sormas.keyAlias=${CRT_NAME}";
  echo "sormas2sormas.keyPassword=${SORMAS_S2S_CERT_PASS}";
  } >> "${SORMAS_PROPERTIES}"
fi

echo "The script finished executing. Please check for any errors."
