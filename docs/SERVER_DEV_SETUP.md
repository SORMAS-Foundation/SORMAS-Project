# Installing a SORMAS Server for development

**Note: This guide explains how to configure a SORMAS server on Linux and Windows systems for development. Please note that there is no database setup because the script supposes the use of the Docker Postgresql image (see [SORMAS-Docker](https://github.com/hzi-braunschweig/SORMAS-Docker)).**

## Content
* [Prerequisites](#prerequisites)
  * [Java 11](#java-11)
  * [ant](#ant)
  * [Postgres Database](#postgres-database)
* [SORMAS Server](#sormas-server)

## Related
* [Installing a SORMAS Server](SERVER_SETUP.md)

## Prerequisites

### Java 11
See [Installing Java](SERVER_SETUP.md#java-11)

SORMAS just recently moved to Java 11. We still need to support Java 8 for a transition period. Therefore, please just
use Java 8 language features for now.

### Ant

Download and install Ant, it can be done from [Ant site](https://ant.apache.org/bindownload.cgi) or with packages from your Linux distribution.

### Postgres Database

See [Installing Postgresql](SERVER_SETUP.md#postgres-database)

Alternatively you can use the Docker image available in [SORMAS-Docker](https://github.com/hzi-braunschweig/SORMAS-Docker) repository.

## SORMAS Server

Install you own Payara server (see [installing SORMAS Server](SERVER_SETUP.md#sormas-server)) or run ``bash ./server-setup-dev-docker.sh``

This script will download Payara (if needed) and install SORMAS in the Payara server.

You can edit this script to change paths and ports.

Other steps :
* **IMPORTANT**: Adjust the SORMAS configuration for your country in /opt/domains/sormas/sormas.properties
* Adjust the logging configuration in ``${HOME}/opt/domains/sormas/config/logback.xml`` based on your needs (e.g. configure and activate email appender)
* Build and deploy applications (ear and war) with you IDE.

## Keycloak

See [Keycloak](SERVER_SETUP.md#keycloak-server) for how to install Docker locally.

If you are doing active development on Keycloak (themes, authentication mechanisms, translations, etc.) it's recommended to install the standalone variant.

## VAADIN Debug Mode

To enable [VAADIN Debug Mode](https://vaadin.com/docs/v8/framework/advanced/advanced-debug.html), go to ``sormas-ui/src/main/webapp/WEB-INF/web.xml`` and set ``productionMode`` to ``false``.
Make sure not to commit your changes to these files, for example by using .gitignore. To access the debug Window, got to <url>/sormas-ui/?debug. You may need to log in as admin once first.

## Other components

See [Installing a SORMAS Server](SERVER_SETUP.md)
