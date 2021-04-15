# SORMAS development server setup using maven-cargo

This module installs a local Payara server, deploys the previously built SORMAS artifacts and starts the server.

The server installation is located in the project build directory (`target/cargo`), as well as the SORMAS server
directories (`target/sormasfolders`).

## Prerequisites
This setup requires a working Java, maven, and docker(-compose) environment.

## Configuration
The configuration of the docker setup and the payara domain setup are defined by the generated file `.env`. It
configures port and server of the sormas-postgres docker container or another postgres database, as well as other
ports defined in the `domain.xml`.

Properties used in the payara deployment are configured in the generated file `target/sormas.properties`.

Both `.env` and `sormas.properties` can be customized by adding a file `custom.env` respectively `custom.properties`
in the project base directory, where additional properties for the respective purpose are defined. Already defined
properties are overwritten. Both `custom.env` and `custom.properties` are excluded in `.gitignore`, so local
customizations are protected from accidental commits.

To run the cargo server against an existing database, configure

```env
SORMAS_POSTGRES_SERVER=<database-server>
SORMAS_POSTGRES_PORT=<database-port>
```

in file `custom.env` and skip the `docker-compose` step in the server setup (see file `custom.env.example`).

To add properties to the generated `sormas.properties`, configure e.g.

```.properties
custombranding=true
custombranding.name=<name>
custombranding.logo.path=<logopath>
```
in file `custom.properties` (see file `custom.properties.example`).

After adjusting the configurations, (re)run `mvn install` and (re)start the server.

## Build the project (simple)
The most convenient way to build and deploy the SORMAS artifacts to cargo is to use `build_deploy.sh`.


## Build the project (details)
Build all SORMAS artifacts:

```bash
cd sormas-base && mvn install
```

### Start SORMAS-PostgreSQL docker container

```bash
cd sormas-cargoserver && docker-compose up -d
```

### Start local SORMAS server

```bash
cd sormas-cargoserver && mvn cargo:run
```

### Visit

Once the deployment is completed, you can navigate to `http://localhost:6080/sormas-ui` and login as `admin` with
password `sadmin`.

### Stop local SORMAS server

```bash
cd sormas-cargoserver && mvn cargo:stop
```

### Stop DB SORMAS-PostgreSQL docker container

```bash
cd sormas-cargoserver && docker-compose down
```

### Remove docker volume (if intended)

The SORMAS-PostgreSQL docker container uses a named docker volume:

```bash
$ docker volume ls
DRIVER              VOLUME NAME
local               sormas-cargoserver_psqldata_cargoserver
```

To remove this docker volume:

```bash
sudo docker volume rm sormas-cargoserver_psqldata_cargoserver
```
