
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
SORMAS2SORMAS_DIR=${ROOT_PREFIX}/opt/sormas2sormas

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
P10_NAME=${SORMAS2SORMAS_DIR}/sormas2sormas.request.p10
P12_NAME=${SORMAS2SORMAS_DIR}/sormas2sormas.keystore.p12
CRT_NAME=${SORMAS2SORMAS_DIR}/sormas2sormas.cert.crt

# generate Private & Public Key & CSR
openssl req -passout pass:"${SORMAS_S2S_CERT_PASS}" -sha256 -newkey rsa:4096 -keyform PEM -new -keyout "${PEM_NAME}" -out "${P10_NAME}" -subj "${CERT_SUBJ}"

# generate self signed certifcate
openssl x509 -passin pass:"${SORMAS_S2S_CERT_PASS}" -req -days 1095 -sha256 -in "${P10_NAME}" -keyform PEM -signkey "${PEM_NAME}" -out "${CRT_NAME}"

# encrypt
openssl pkcs12 -export -inkey "${PEM_NAME}" -out "${P12_NAME}" -passin pass:"${SORMAS_S2S_CERT_PASS}" -password pass:"${SORMAS_S2S_CERT_PASS}" -in "${CRT_NAME}"

#update properties
if [[ -z ${SORMAS_PROPERTIES} ]]; then
	echo "sormas.properties file was not found."
  echo "Please add the following properties to the sormas.properties file:"
  echo "sormas2sormas.keyAlias=${CRT_NAME}"
  echo "sormas2sormas.keyPassword=${SORMAS_S2S_CERT_PASS}"
else
  {
  echo;
  echo "# Key data for the generated SORMAS 2 SORMAS certificates";
  echo "sormas2sormas.keyAlias=${CRT_NAME}";
  echo "sormas2sormas.keyPassword=${SORMAS_S2S_CERT_PASS}";
  } >> "${SORMAS_PROPERTIES}"
fi

echo "The script finished executing. Please check for any errors."
