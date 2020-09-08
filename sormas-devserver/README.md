## SORMAS development server setup using maven-cargo

This module installs a local Payara server, deploys the previously built SORMAS artifacts and starts the server.

The server installation is located in the project build directory (`target/cargo`), as well as the SORMAS server
directories (`target/sormasfolders`).

## Prerequisites
This setup requires a working Java, maven, and docker(-compose) environment.

## Configuration
The port used by the sormas-postgres docker container and other ports used by the cargo server can be configured
in file `.env`.

To run the cargo server against an existing database, configure `SORMAS_POSTGRES_PORT` in `.env` and
skip the `docker-compose` step in the server setup.

After adjusting the configuration, (re)run `mvn install` and (re)start the server.

## Build the project

```
cd sormas-base && mvn install
```

### Start SORMAS-PostgreSQL docker container

```
cd sormas-devserver && docker-compose up -d
```

## Start local SORMAS server

```
cd sormas-devserver && mvn cargo:run
```

## Visit

Once the deployment is completed, you can navigate to `http://localhost:6080/sormas-ui` and login as `admin` with 
password `sadmin`.

## Stop local SORMAS server

```
cd sormas-devserver && mvn cargo:stop
```

## Stop DB SORMAS-PostgreSQL docker container

```
cd sormas-devserver && docker-compose down
```
