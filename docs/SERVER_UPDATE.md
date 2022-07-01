# Updating a SORMAS Server

SORMAS releases starting from 1.21.0 contain a script that automatically updates and deploys the server. If you are using an older version and therefore need to do a manual server update, please download the 1.21.0 release files and use the commands specified in the server-update.sh script.

## Preparations
Note: At some versions it is mandatory to switch to a new Payara Server. If your version bump does apply to the listing below, please proceed with [Payara migration](SERVER_UPDATE.md#how-to-migrate-to-new-payara-server).
* Switching from <=v1.66.4 to v1.67.0 or newer

Note: You can skip this step if you've just set up your SORMAS server and have already downloaded the latest release.

* Get the latest release files (deploy.zip) from <https://github.com/hzi-braunschweig/SORMAS-Project/releases/latest>
* Unzip the archive and copy/upload its contents to **/root/deploy/sormas/$(date +%F)**
    ```bash
    cd /root/deploy/sormas
    SORMAS_VERSION=1.y.z
    wget https://github.com/hzi-braunschweig/SORMAS-Project/releases/download/v${SORMAS_VERSION}/sormas_${SORMAS_VERSION}.zip
    unzip sormas_${SORMAS_VERSION}.zip
    mv deploy/ $(date +%F)
    rm sormas_${SORMAS_VERSION}.zip
    ```
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

## Updating Keycloak

### Standalone installation

Follow the official Keycloak upgrade [guide](https://www.keycloak.org/docs/latest/upgrading/).

To update follow this steps:

1. Prerequisites
* Backup the DB
* Backup the current Keycloak configuration
* Download the 18.0.1 zip from <https://www.keycloak.org/downloads>
* Extract everything from the archive somewhere on your disk (will call this `KEYCLOAK_HOME_NEW`)

2. From you current installation (will call this `KEYCLOAK_HOME_OLD`) directory copy the following into the new installation
* Copy directory `KEYCLOAK_HOME_OLD/themes/sormas` over to `KEYCLOAK_HOME_NEW/themes`
* Copy `KEYCLOAK_HOME_OLD/providers/sormas-keycloak-service-provider-*.jar` over to `KEYCLOAK_HOME_16/providers`

3. Setup Keycloak to use the [Database](https://www.keycloak.org/server/db)
4. Start Keycloak
* Database will be migrated automatically


### Docker installation

The docker installation is automatically upgraded to the latest version specified in the Dockerfile.

**Prerequisites:** Make sure the DB is backed up, because once the upgrade is done the new DB won't be usable with the old version of Keycloak.

For more info see the [Keycloak Docker Documentation](https://github.com/hzi-braunschweig/SORMAS-Docker/blob/development/keycloak/README.md).

## How to migrate to new Payara Server

### Step 1: Shutdown existing domain
```bash
# Stop domain
service payara-sormas stop

# Move existing domain
DOMAIN_PATH=/opt/domains
DOMAIN_NAME="sormas"
DOMAIN_BACKUP_NAME="sormas_backup"
mv $DOMAIN_PATH/$DOMAIN_NAME $DOMAIN_PATH/$DOMAIN_BACKUP_NAME
```

### Step 2: Setup Payara domain
Please follow the [server setup](SERVER_SETUP.md#sormas-server): Create the payara domain under the same path as before, use the same directory paths and the same database settings.

### Step 3: Apply your config file changes
Transfer your settings from `sormas.properties`, `logback.xml` or changes in the domain setup. Use the new provided files and copy your changes in, don't reuse old files!

### Step 4: Install new SORMAS version
To install the new SORMAS version in the Payara domain, proceed with the [automatic update](SERVER_UPDATE.md#automatic-server-update) or for developers: Deploy SORMAS via the IDE as usual.
