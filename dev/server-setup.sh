#!/bin/bash
#*******************************************************************************
# SORMAS® - Surveillance Outbreak Response Management & Analysis System
# Copyright © 2016-2020 Helmholtz-Zentrum f�r Infektionsforschung GmbH (HZI)
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

highlight() {
  echo -e '\e[1m'"$*"'\e[22m'
}
success() {
  echo -e '\e[32m'"$(highlight "$*")"'\e[39m'
}
warn() {
  echo -e '\e[33m'"$(highlight "$*")"'\e[39m'
}
fail () {
  echo -e '\e[31m'"$(highlight "$*")"'\e[39m'
}

variable_cheatsheet_section () {
  if [[ $(expr $# % 2) -ne 0 ]]; then
    # With section header as first argument
    SECTION_NAME="$1"
    VAR_ENTRIES=("${@:2}")

    echo "${SECTION_NAME}"
  else
    # Without section header
    VAR_ENTRIES=("${@:1}")
  fi

  i=0
  while [[ $i -lt ${#VAR_ENTRIES[@]} ]]; do
    LABEL="${VAR_ENTRIES[$i]}"; i=$(expr $i + 1)
    VALUE="${VAR_ENTRIES[$i]}"; i=$(expr $i + 1)

    echo " - **${LABEL}**: ${VALUE}"
  done
  echo
}

# Make sure we know the SORMAS project's root directory
if [[ -z "${SORMAS_PROJECT_DIR}" ]]; then
  SORMAS_PROJECT_DIR="$(dirname "$0")"
fi

while [[ "$SORMAS_PROJECT_DIR" =~ /? && ! -d "$SORMAS_PROJECT_DIR/sormas-base" ]]; do
  SORMAS_PROJECT_DIR="$(realpath "$SORMAS_PROJECT_DIR/..")"
done

if [[ ! -d "$SORMAS_PROJECT_DIR/sormas-base" ]]; then
  error "Could not find root directory of SORMAS project!"
  exit 1
fi


# Make sure the user has all required applications installed; if such are
# missing, notify the user and let him or her decide whether to continue
highlight "Checking for application dependencies..."
APP_DEPENDENCIES=(\
  "Docker (with docker-compose):docker-compose" \
  "Ant:ant" \
  "Maven:mvn" \
)

DEPS_MISSING=()
for i in "${!APP_DEPENDENCIES[@]}"; do
  # Separate by colon
  OIFS="$IFS"
  IFS=':'
  PARTS=(${APP_DEPENDENCIES[$i]})
  IFS="$OIFS"

  DEP_NAME="${PARTS[0]}"
  DEP_EXECUTABLE="${PARTS[1]}"

  if [[ -n "$(which "$DEP_EXECUTABLE")" ]]; then
    EXE_STATUS="$(success ✓)"
  else
    EXE_STATUS="$(fail X)"
    DEPS_MISSING=("${DEPS_MISSING[@]}" "${DEP_NAME} (${DEP_EXECUTABLE} command)")
  fi

  if [[ $i -lt $(expr ${#APP_DEPENDENCIES[@]} - 1) ]]; then
    ENTRY_PREFIX=" ├"
  else
    ENTRY_PREFIX=" └"
  fi

  echo "${ENTRY_PREFIX} ${DEP_NAME}: ${EXE_STATUS}"

done

if [[ ${#DEPS_MISSING[@]} -gt 0 ]]; then
  echo
  fail "Some required applications have not been found on your path:"
  for DEP in "${DEPS_MISSING[@]}"; do
    echo "  - ${DEP}"
  done
  echo

  exit 1
#  highlight "Do you want to continue anyway? (May result in errors and incomplete install)"
#  select CHOICE in "No" "Yes"; do
#    case "$CHOICE" in
#      Yes ) break;;
#      No ) exit 0;;
#    esac
#  done
fi

echo

# Create the setup environment
SETUP_DIR="$(mktemp --directory)"
cp -r "$SORMAS_PROJECT_DIR"/sormas-base/setup/* "$SETUP_DIR"
chmod +x "$SETUP_DIR/server-setup.sh"

pushd "$SETUP_DIR" > /dev/null


INSTALL_DIR_DEFAULT="$(realpath "$SORMAS_PROJECT_DIR"/dev/server)"
read -rp "$(highlight "Where do you want your development installation to live? [$INSTALL_DIR_DEFAULT] ")" INSTALL_DIR
if [[ -z "$INSTALL_DIR" ]]; then
  INSTALL_DIR="$INSTALL_DIR_DEFAULT"
fi

SETUP_PARAMS=( \
  DEV_SYSTEM=true \
  INSTALL_DIR="$INSTALL_DIR" \
)

# Prepare PostgreSQL docker container if to be used in dev setup
highlight "Do you want your development server to use the provided PostgreSQL docker container?"
select CHOICE in "Yes" "No"; do
  case "$CHOICE" in
    Yes ) USE_POSTGRESQL_DOCKER=true; break;;
    No ) USE_POSTGRESQL_DOCKER=false; break;;
  esac
done

if [[ "$USE_POSTGRESQL_DOCKER" = true ]]; then
  if [[ -n "$(which sudo)" ]]; then
    highlight "What do you want to use for privileged operations?"
    select CHOICE in "Use sudo" "Use su"; do
      case "$CHOICE" in
        "Use sudo" ) USE_SUDO=true; break;;
        "Use su" ) USE_SUDO=false; break;;
      esac
    done
  else
    USE_SUDO=false
  fi
  SETUP_PARAMS=("${SETUP_PARAMS[@]}" USE_SUDO=true)

  if [[ ${USE_SUDO} = true ]]; then
    ELEVATED=(sudo bash -c)
  else
    ELEVATED=(su -c)
  fi

  pushd "$SORMAS_PROJECT_DIR/dev/postgres-docker" > /dev/null

  echo "Starting PostgreSQL docker container"
  "${ELEVATED[@]}" 'docker-compose up -d --force-recreate'
  DOCKER_RESULT=$?

  popd > /dev/null
  if [[ $DOCKER_RESULT -ne 0 ]]; then
    warn "Could not create and start docker container!"
    read -p "Press [Enter] to continue or [Ctrl+C] to abort setup."
  else
    # The following variable values have to match the setup in
    # postgres-docker/docker-compose.yml file
    SETUP_PARAMS=( \
      "${SETUP_PARAMS[@]}" \
      INIT_DB=true \
      DB_HOST=localhost \
      DB_PORT=5432 \
      DB_PG_PW=sormas \
      DB_PASSWORD=sormas \
      DB_TCP_CONNECT=true \
    )
  fi
fi

while :; do
  echo
  env "${SETUP_PARAMS[@]}" "$SETUP_DIR/server-setup.sh"
  SETUP_RESULT_CODE=$?
  echo "================================================"
  echo

  if [[ $SETUP_RESULT_CODE -eq 0 ]]; then
    success "Server Setup successful!"
    break
  else
    warn "Setup failed."
    highlight "How do you wish to proceed?"
    select CHOICE in "Run setup again" "Abort"; do
      case "$CHOICE" in
        "Run setup again" ) ;;
        "Abort" ) break 2 ;;
      esac
    done
  fi
done

# Retrieve the choices the user made
source "$SETUP_DIR/server-setup.conf"

popd > /dev/null
rm -r "$SETUP_DIR"

highlight "Setting up environment variables..."

# M2_HOME
if [[ -n "$M2_HOME" ]]; then
  MAVEN_HOME="$M2_HOME"
else
  MVN_EXE_PATH="$(realpath -P "$(which mvn)")"
  MAVEN_HOME="$(realpath "$(dirname "$MVN_EXE_PATH")/..")"
fi
while :; do
  if [[ -z "$MAVEN_HOME" ]]; then
    read -rp "$(highlight "Specify the path to your Maven installation (you have it installed, don't you?): ")" MAVEN_HOME
  fi

  if [[ -n "$MAVEN_HOME" ]]; then
    if [[ -x "$MAVEN_HOME/bin/mvn" ]]; then
      MAVEN_HOME="$(realpath "$MAVEN_HOME")"
      break
    else
      warn "The path specified for Maven does not point to a valid Maven installation!"
      unset MAVEN_HOME
    fi
  fi
done

# ANDROID_HOME
while [[ -z "$ANDROID_HOME_DEF" ]]; do
  if [[ -z "$ANDROID_HOME" ]]; then
    read -rp "$(highlight "Specify the path to your Android SDK (you may leave blank if you do not have one installed): ")" ANDROID_HOME
  fi
  if [[ -n "$ANDROID_HOME" ]]; then
    if [[ -x "$ANDROID_HOME/tools/android" ]]; then
      ANDROID_HOME_DEF="export ANDROID_HOME=\"$(realpath "$ANDROID_HOME")\""
    else
      warn "The path specified for the Android SDK does not point to a valid Android SDK installation!"
      unset ANDROID_HOME
    fi
  else
    ANDROID_HOME_DEF="#export ANDROID_HOME="
  fi
done

echo "Writing environment variables to ${ENVSHELL_FILE}..."
DOMAIN_DIR="$DOMAINS_HOME/$DOMAIN_NAME"
ENVSHELL_FILE="$SORMAS_PROJECT_DIR/dev/env.sh"
cat > "$ENVSHELL_FILE" <<-EOF
export M2_HOME="$MAVEN_HOME"
export JAVA_HOME="$JDK_HOME"
$ANDROID_HOME_DEF

export SORMAS_DOMAIN_DIR="$DOMAIN_DIR"
export SORMAS_PAYARA_HOME="$PAYARA_HOME"
export SORMAS_PAYARA_ADMINPORT="$PORT_ADMIN"
EOF
#chmod +x "$ENVSHELL_FILE"

highlight "Environment variable setup script created!"

source "$ENVSHELL_FILE"

highlight "Setting up Ant deployment tasks..."
cat > "$SORMAS_PROJECT_DIR/sormas-base/build.properties" <<-EOF
### Auto-generated by SORMAS development setup script ###
glassfish.domain.root=$DOMAIN_DIR
payara.home=$PAYARA_HOME
payara.admin.port=$PORT_ADMIN
EOF

pushd "$SORMAS_PROJECT_DIR/sormas-base" > /dev/null
# Restarting domain to make sure libraries get loaded
"$DOMAIN_DIR/stop-payara-sormas.sh"

echo "Initial deployment of server libraries..."
ant deploy-serverlibs

"$DOMAIN_DIR/start-payara-sormas.sh"
popd > /dev/null

echo
highlight "Generating cheatsheet for your system configuration..."
SYSSPECS_FILE="$SORMAS_PROJECT_DIR/dev/SERVER_SPECS.md"
cat > "$SYSSPECS_FILE" <<-EOF
# Development Server Reference
This file contains the all the relevant need-to-know configuration values
for your development system.

EOF

variable_cheatsheet_section "## Important locations and links" \
  "Location of Payara start/stop scripts" "$DOMAIN_DIR/{start,stop}-payara-sormas.sh" \
  "Payara Admin Interface" "<http://localhost:${PORT_ADMIN}>" \
  "SORMAS Web-App" "<http://localhost:$(expr ${PAYARA_PORT_BASE} + 80)/sormas-ui>" \
  "SORMAS REST Root" "http://localhost:$(expr ${PAYARA_PORT_BASE} + 80)/sormas-rest" \
  >> "$SYSSPECS_FILE"

printf '\n## Configuration Reference\n' >> "$SYSSPECS_FILE"

variable_cheatsheet_section "### General config" \
  "Development System?" "$DEV_SYSTEM" \
  "Demo System?" "$DEMO_SYSTEM" \
  "init.d service installed?" "$INSTALL_SERVICE" \
  "Java SDK" "$JDK_HOME" \
  "Maven" "$M2_HOME" \
  "Android SDK" "$ANDROID_HOME" \
  >> "$SYSSPECS_FILE"

variable_cheatsheet_section "### Installation Paths" \
  "Payara" "$PAYARA_HOME" \
  "Domains" "$DOMAINS_HOME" \
  "SORMAS temporary files" "$TEMP_DIR" \
  "SORMAS generated files" "$GENERATED_DIR" \
  "SORMAS custom files" "$CUSTOM_DIR" \
  >> "$SYSSPECS_FILE"

variable_cheatsheet_section "### Payara config" \
  "Server version" "$PAYARA_VERSION" \
  "Installation directory" "$PAYARA_HOME" \
  "\`asadmin\` executable" "$PAYARA_HOME/glassfish/bin/asadmin" \
  "User for init.d" "$PAYARA_USER" \
  "Portbase" "$PAYARA_PORT_BASE" \
  "Admin Port" "$(expr $PAYARA_PORT_BASE + 48)" \
  "HTTP Port" "$(expr $PAYARA_PORT_BASE + 80)" \
  >> "$SYSSPECS_FILE"

variable_cheatsheet_section "### Database / PostgreSQL" \
  "Database Host" "$DB_HOST" \
  "Port" "$DB_PORT" \
  "Name of SORMAS database" "$DB_NAME" \
  "Name of SORMAS audit database" "$DB_NAME_AUDIT" \
  "SORMAS database user" "$DB_USER" \
  "SORMAS user password" "$DB_PASSWORD" \
  >> "$SYSSPECS_FILE"

variable_cheatsheet_section "### SORMAS config" \
  "Domain name" "$DOMAIN_NAME" \
  "Domain directory" "$DOMAIN_DIR" \
  "Temporary files directory" "$TEMP_DIR" \
  "Generated files directory" "$GENERATED_DIR" \
  "Custom files directory" "$CUSTOM_DIR" \
  >> "$SYSSPECS_FILE"

echo "You can find a reference with important information about your development system configuration at:"
echo "  $SYSSPECS_FILE"
echo
success "You are good to go. Happy coding! :)"
echo
echo "Tips:"
echo "Source the '$(basename "$ENVSHELL_FILE")' script created in dev directory of" \
  "SORMAS project root before you start your development session to load your" \
  "required environment variables."
echo
echo "Use these Ant-Tasks in 'sormas-base' for development:"
echo "  - $(highlight clean):             Remove leftovers from previous builds; use it after" \
     "pulling changes from the Github-Repo"
echo "  - $(highlight install):           Build all SORMAS components except for the Android App"
echo "  - $(highlight install-with-app):  Build all SORMAS components including the App"
echo "  - $(highlight deploy-serverlibs): Deploy all general server dependencies to your" \
     "payara installation"
echo "  - $(highlight deploy-artifacts):  Deploy SORMAS components to your payara installation"
echo
echo "You can find documentation about the SORMAS development server setup in $SORMAS_PROJECT_DIR/SERVER_DEV_SETUP.md"
