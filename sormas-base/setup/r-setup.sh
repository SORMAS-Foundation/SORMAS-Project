
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
exec > >(tee -ia r-setup.log)
exec 2> >(tee -ia r-setup.log)

echo "# SORMAS R SETUP"
echo "# Welcome to the SORMAS R setup and update routine. This script will guide you through the setup of R."

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

TEMP_DIR=${ROOT_PREFIX}/opt/sormas/temp
DOMAINS_HOME=${ROOT_PREFIX}/opt/domains

DOMAIN_NAME=sormas
DOMAIN_DIR=${DOMAINS_HOME}/${DOMAIN_NAME}

echo "--- Please confirm that all values are set properly:"
if [[ ${LINUX} = true ]]; then
	echo "OS: Linux"
else
	echo "OS: Windows"
fi
echo "Temp directory: ${TEMP_DIR}"
echo "Domain directory: ${DOMAIN_DIR}"
echo "---"
read -p "Press [Enter] to continue or [Ctrl+C] to cancel and adjust the values..."

# ------ Config END ------

# install R  
if [[ ${LINUX} = true ]]; then
	sudo apt install r-base
	
else
	R_WINDOWS_URL=https://cran.r-project.org/bin/windows/base/
	R_INSTALLER=$(curl -s $R_WINDOWS_URL | grep '<a href="R-' | sed -n 's/.\+href="\(.\+\)".\+$/\1/p')
	R_VERSION=$(echo $R_INSTALLER | sed -n s/[^0-9]*$//p)
	
	echo "Do you wish to install the current R for Windows (${R_VERSION})?"
	select yn in "Yes" "No"
	do
	case $yn in
		Yes)
			echo "Downloading ${R_WINDOWS_URL}${R_INSTALLER}"
			curl ${R_WINDOWS_URL}${R_INSTALLER} > ${R_INSTALLER}
			echo "Starting installation of R..."
			./${R_INSTALLER}
		break
		;;
		*)
		echo "Skipping download"
		break
		;;
	esac
	done
fi

# install R packages 
if [[ ${LINUX} = true ]]; then
	read -p "Required R packages will be downloaded, compiled and installed. Press [Enter] to continue or [Ctrl+C] to cancel."
	sudo apt install libpq-dev 
	sudo R -f install_packages.r
	#sudo apt remove libpq-dev
	
else
	read -p "Required R packages will be downloaded and installed. Admin rights will be requested. Press [Enter] to continue or [Ctrl+C] to cancel."
	
	R_DIR_DEFAULT="${PROGRAMFILES}\\R\\${R_VERSION}"
	echo "--- Enter the install path of R on your system (default: \"${R_DIR_DEFAULT}\":"
	read -r R_DIR
	if [[ -z "${R_DIR}" ]]; then
		R_DIR="${R_DIR_DEFAULT}"
	fi
	while [[ ! -d "${R_DIR}" ]]; do
		read -p "Please specify a valid directory" R_DIR
	done
	echo "Starting package installation..."
	powershell -Command "Start-Process -Verb \"RunAs\" -Wait -FilePath \"${R_DIR}\\bin\R.exe\" \"--no-save -f install_packages.r\""
fi

#modify sormas.properties, if needed
SORMAS_PROPERTIES="${DOMAIN_DIR}/sormas.properties"
read -p "If needed, rscript.executable in '$SORMAS_PROPERTIES' will be modified. Press [Enter] to continue or [Ctrl+C] to cancel."

RSCRIPT_EXEC_CURRENT=$(sed -n 's/^\s*rscript.executable=//p' "${SORMAS_PROPERTIES}")
RSCRIPT_EXEC_LINE=$(grep -n '^[:whitespace:#]*rscript.executable=' "${SORMAS_PROPERTIES}" | sed 's/:.*//g' | tail -n 1)

if [[ -z "${RSCRIPT_EXEC_CURRENT}" ]]; then
	
	if [[ ${LINUX} = true ]]; then
		RSCRIPT_EXEC="Rscript"
	else
		RSCRIPT_EXEC="${R_DIR}\\bin\\Rscript.exe"
	fi
	
	RSCRIPT_EXEC_4PROPS="${RSCRIPT_EXEC//\\/\\\\}"

	if [[ -z "${RSCRIPT_EXEC_LINE}" ]]; then
		echo "rscript.executable=${RSCRIPT_EXEC_4PROPS}" >> "${SORMAS_PROPERTIES}"
	else
		RSCRIPT_EXEC_LINE=$(( ${RSCRIPT_EXEC_LINE} + 1 ))
		sed -i.bak "${RSCRIPT_EXEC_LINE}irscript.executable=${RSCRIPT_EXEC_4PROPS//\\/\\\\}" "${SORMAS_PROPERTIES}"
	fi
	echo "Set property rscript.executable in sormas.properties to: ${RSCRIPT_EXEC}"
else
	echo "Property rscript.executable in sormas.properties remains unchanged: ${RSCRIPT_EXEC_CURRENT}"
fi

echo "Setup of R completed."
