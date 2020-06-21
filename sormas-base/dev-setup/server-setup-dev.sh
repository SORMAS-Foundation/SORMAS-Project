#!/bin/bash
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


# Create the setup environment
SETUP_DIR="$(mktemp --directory)"
cp -r "$SORMAS_PROJECT_DIR"/sormas-base/setup/* "$SETUP_DIR"
chmod +x "$SETUP_DIR/server-setup.sh"

pushd "$SETUP_DIR" > /dev/null


INSTALL_DIR_DEFAULT="$(realpath "$SORMAS_PROJECT_DIR"/dev-server)"
read -rp "$(highlight "Where do you want your development installation to live? [$INSTALL_DIR_DEFAULT] ")" INSTALL_DIR
if [[ -z "$INSTALL_DIR" ]]; then
  INSTALL_DIR="$INSTALL_DIR_DEFAULT"
fi

SETUP_PARAMS=( \
  DEV_SYSTEM=true \
  INSTALL_DIR="$INSTALL_DIR" \
)

# Prepare PostgreSQL docker container if to be used in dev setup
highlight "Do you want your development server to use the PostgreSQL docker container?"
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

  pushd "$SORMAS_PROJECT_DIR/postgres-docker" > /dev/null

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
    success "Setup completed successfully!"
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

popd > /dev/null
rm -r "$SETUP_DIR"
