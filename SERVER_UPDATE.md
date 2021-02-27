# Updating a SORMAS Server

SORMAS releases starting from 1.21.0 contain a script that automatically updates and deploys the server. If you are using an older version and therefore need to do a manual server update, please download the 1.21.0 release files and use the commands specified in the server-update.sh script.

## Preparations

Note: You can skip this step if you've just set up your SORMAS server and have already downloaded the latest release.

* Get the latest release files (deploy.zip) from https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest
* Unzip the archive and copy/upload its contents to **/root/deploy/sormas/$(date +%F)**

        cd /root/deploy/sormas
        SORMAS_VERSION=1.y.z
        wget https://github.com/hzi-braunschweig/SORMAS-Project/releases/download/v${SORMAS_VERSION}/sormas_${SORMAS_VERSION}.zip
        unzip sormas_${SORMAS_VERSION}.zip
        mv deploy/ $(date +%F)
        rm sormas_${SORMAS_VERSION}.zip
## Automatic Server Update
* Navigate to the  folder containing the unzipped deploy files:
``cd /root/deploy/sormas/$(date +%F)``
* Make the update script executable:
``chmod +x server-update.sh``
* Optional: Open server-update.sh in a text editor to customize the values for e.g. the domain path or the database name. You only need to do this if you used custom values while setting up the server.
* Execute the update script and follow the instructions:
``./server-update.sh``
* If anything goes wrong, open the latest update log file (by default located in the "update-logs" folder in the domain directory) and check it for errors.

## Restoring the Database
If anything goes wrong during the automatic database update process when deploying the server, you can use the following command to restore the data:

``pg_restore --clean -U postgres -Fc -d sormas_db sormas_db_....dump``

## Default Logins
These are the default users for most user roles, intended to be used on development or demo systems. In all cases except the admin user, the username and password are identical. Make sure to deactivate them or change the passwords on productive systems.

### Admin
**Username:** admin
**Password:** sadmin

### Web users
**Surveillance Supervisor:** SurvSup
**Case Supervisor:** CaseSup
**Contact Supervisor:** ContSup
**Point of Entry Supervisor:** PoeSup
**Laboratory Officer:** LabOff
**Event Officer:** EveOff
**National User:** NatUser
**National Clinician:** NatClin

### Mobile app users
**Surveillance Officer:** SurvOff
**Hospital Informant:** HospInf
**Point of Entry Informant:** PoeInf

# Updating Keycloak

## Standalone installation

Upgrading from Keycloak 11 to 12 following the steps from here https://www.keycloak.org/docs/latest/upgrading/#_upgrading

1. Stop the old server and make sure to remove any open connections to the DB
2. Backup the DB *(once the upgrade is done the old version cannot be used with the new DB version)*
3. Backup the old installation
4. Remove `${OLD_KEYCLOAK_HOME}/standalone/data/tx-object-store/`
5. Download the new Keycloak installation from https://www.keycloak.org/downloads
6. Copy the `${NEW_KEYCLOAK_HOME}/standalone/` directory from the previous installation over the directory in the new installation
7. Copy the postgres module from `${OLD_KEYCLOAK_HOME}/modules/system/layers/keycloak/org/` over to the new installation directory
8. Copy the SORMAS themes from `{OLD_KEYCLOAK_HOME}/themes/` over to the new installation directory
9. While the new installation is stopped, run `${NEW_KEYCLOAK_HOME}/bin/jboss-cli.sh ----file=${NEW_KEYCLOAK_HOME}/bin/migrate-standalone.cli` *(`.bat` for Windows)*
10. Start the new Keycloak installation from `${NEW_KEYCLOAK_HOME}/bin/standalone.sh` *(`.bat` for Windows)*

## Docker installation

The docker installation is automatically upgraded to the latest version specified in the Dockerfile.

**Prerequisites:** Make sure the DB is backed up, because once the upgrade is done the new DB won't be usable with the old version of Keycloak.

For more info see the [Keycloak Docker Documentation](https://github.com/hzi-braunschweig/SORMAS-Docker/blob/development/keycloak/README.md).
