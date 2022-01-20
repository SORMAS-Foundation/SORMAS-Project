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

Upgrading from Keycloak 12 to 16 following the steps from here <https://www.keycloak.org/docs/16.1/upgrading/#_upgrading>

*16.1.0 doesn't provide a way to upgrade host based installations as there were a lot of changes due to the Wildfly update <https://www.keycloak.org/docs/16.1/upgrading/#migrating-to-16-0-0>*

To update follow this steps:

1. Prerequisites
* Backup the DB
* Backup the current Keycloak configuration
* Download 16.1.0 zip from <https://www.keycloak.org/downloads>
* Extract everything from the archive somewhere on your disk (will call this `KEYCLOAK_HOME_16`)

2. From you current installation (will call this `KEYCLOAK_HOME_12`) directory copy the following into the new installation
* Copy directory `KEYCLOAK_HOME_12/themes/sormas` over to `KEYCLOAK_HOME_16/themes`
* Copy directory `KEYCLOAK_HOME_12/modules/system/layers/keycloak/org/postgresql` over into `KEYCLOAK_HOME_16/modules/system/layers/keycloak/org`
* Copy `KEYCLOAK_HOME_12/standalone/deployments/sormas-keycloak-service-provider-*.jar` over to `KEYCLOAK_HOME_16/standalone/deployments`

3. Edit the `KEYCLOAK_HOME_16/standalone/configuration/standalone.xml`
* Search for `java:jboss/datasources/KeycloakDS` and you should find something like this
```xml
<datasource jndi-name="java:jboss/datasources/KeycloakDS" pool-name="KeycloakDS" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
```
* Replace it's content with the content from `KEYCLOAK_HOME_12/standalone/configuration/standalone.xml` and you should end up with something like this
```xml
<datasource jndi-name="java:jboss/datasources/KeycloakDS" pool-name="KeycloakDS" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
    <connection-url>jdbc:postgresql://host:5432/keycloak-db-name</connection-url>
    <driver>postgresql</driver>
     <pool>
         <max-pool-size>20</max-pool-size>
     </pool>
     <security>
          <user-name>keycloak-db-username</user-name>
          <password>keycloak-db-password</password>
      </security>
 </datasource>
```
* Make sure that you replace `keycloak-db-name`, `keycloak-db-username` and `keycloak-db-password` with your actual values
* In the section `<drivers>` bellow, add also this option
```xml
<driver name="postgresql" module="org.postgresql">
    <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
</driver>
```
* You should end up with something like this

```xml
<drivers>
    <driver name="postgresql" module="org.postgresql">
        <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
    </driver>
    <driver name="h2" module="com.h2database.h2">
        <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
    </driver>
</drivers>
```
4. Start Keycloak
* Database will be migrated automatically


Upgrading from Keycloak 11 to 12 following the steps from here <https://www.keycloak.org/docs/12.0/upgrading/#_upgrading>

1. Stop the old server and make sure to remove any open connections to the DB
2. Backup the DB *(once the upgrade is done the old version cannot be used with the new DB version)*
3. Backup the old installation
4. Remove `${OLD_KEYCLOAK_HOME}/standalone/data/tx-object-store/`
5. Download the new Keycloak installation from <https://www.keycloak.org/downloads>
6. Copy the `${NEW_KEYCLOAK_HOME}/standalone/` directory from the previous installation over the directory in the new installation
7. Copy the postgres module from `${OLD_KEYCLOAK_HOME}/modules/system/layers/keycloak/org/` over to the new installation directory
8. Copy the SORMAS themes from `{OLD_KEYCLOAK_HOME}/themes/` over to the new installation directory
9. While the new installation is stopped, run `${NEW_KEYCLOAK_HOME}/bin/jboss-cli.sh ----file=${NEW_KEYCLOAK_HOME}/bin/migrate-standalone.cli` *(`.bat` for Windows)*
10. Start the new Keycloak installation from `${NEW_KEYCLOAK_HOME}/bin/standalone.sh` *(`.bat` for Windows)*

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
