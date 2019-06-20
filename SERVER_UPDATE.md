# Updating a SORMAS Server
SORMAS releases starting from 1.21.0 contain a script that automatically updates and deploys the server. If you use 1.21.0 or a later version, it's recommended to use this script instead of doing a manual update.

## Content
* [Automatic Server Update](#automatic-server-update)
  * [Preparations](#preparations)
  * [Update Script](#update-script)
* [Manual Server Update](#manual-server-update)
  * [Deployment Preparations](#deployment-preparations)
  * [Version Specifics](#version-specifics)
  * [Maintenance Mode](#maintenance-mode)
  * [Undeployment](#undeployment)
  * [Domain Libraries](#domain-libraries)
  * [OSGi Bundles](#osgi-bundles)
  * [Database Update](#database-update)
  * [Web Applications](#web-applications)
  * [Final Steps](#final-steps)
* [Default Logins](#default-logins)

## Automatic Server Update

### Preparations
* Get the latest release files (deploy.zip) from https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest
* Unzip the archive and copy/upload its contents to **/root/deploy/sormas/$(date +%F)**

### Update Script
* Navigate to the "update" folder in the folder containing the unzipped deploy files:
``cd /root/deploy/sormas/$(date +%F)/update``
* Make the update script executable:
``chmod +x server-update.sh``
* Optional: Open server-update.sh in a text editor to customize the values for e.g. the domain path or the database name. You only need to do this if you used custom values while setting up the server.
* Execute the update script and follow the instructions:
``./server-update.sh``
* If anything goes wrong, open the latest update log file (by default located in the "update-logs" folder in the domain directory) and check it for errors.

## Manual Server Update

### Deployment Preparations
* Get the latest release files (deploy.zip) from https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest
* Unzip the archive and copy/upload its contents to **/root/deploy/sormas/$(date +%F)**
* Navigate to the folder containing the deploy files:  
``cd /root/deploy/sormas/$(date +%F)``
* Show log to track the deploy process (in a second console window):  
``tail -f /var/log/glassfish/sormas/server.log``

### Version Specifics
Please make sure to take these actions when you update the server to the respective version. This is not required when the SORMAS server was set up with a newer version already.

* 0.15.0: Update the glassfish config with the new apk filename: sormas-release.apk
* 0.16.0: Update the glassfish config with the export path, SMS sender name, auth key and secret
* 0.19.0: Update the glassfish config with the generated files path
* 1.4.0: Update the glassfish config with the automatic classification feature
* ~~1.10.0: Update the auth realm query properties:~~
* 1.19.0: Update the auth realm query properties:

Linux:
```
/opt/payara-172/glassfish/bin/asadmin --port 6048 delete-auth-realm sormas-realm
/opt/payara-172/glassfish/bin/asadmin --port 6048 create-auth-realm --classname org.wamblee.glassfish.auth.FlexibleJdbcRealm --property "jaas.context=sormasRealm:sql.password=SELECT password FROM users WHERE username\=? AND active\=true:sql.groups=SELECT userrole FROM users_userroles INNER JOIN users ON users_userroles.user_id\=users.id WHERE users.username\=?:sql.seed=SELECT seed FROM users WHERE username\=?:datasource.jndi=jdbc/sormasUsersDataPool:assign-groups=AUTHED_USER:password.digest=SHA-256:charset=UTF-8" sormas-realm
/opt/payara-172/glassfish/bin/asadmin --port 6048 set server-config.security-service.default-realm=sormas-realm
```
Windows:
```
C:/srv/payara-172/glassfish/bin/asadmin --port 6048 delete-auth-realm sormas-realm
C:/srv/payara-172/glassfish/bin/asadmin --port 6048 create-auth-realm --classname org.wamblee.glassfish.auth.FlexibleJdbcRealm --property "jaas.context=sormasRealm:sql.password=SELECT password FROM users WHERE username\=? AND active\=true:sql.groups=SELECT userrole FROM users_userroles INNER JOIN users ON users_userroles.user_id\=users.id WHERE users.username\=?:sql.seed=SELECT seed FROM users WHERE username\=?:datasource.jndi=jdbc/sormasUsersDataPool:assign-groups=AUTHED_USER:password.digest=SHA-256:charset=UTF-8" sormas-realm
C:/srv/payara-172/glassfish/bin/asadmin --port 6048 set server-config.security-service.default-realm=sormas-realm
```

### Maintenance Mode
* ``a2dissite your.sormas.server.url.conf``
* ``apache2ctl graceful``

### Undeployment
While server domain is running:
* ``rm /opt/domains/sormas/autodeploy/*.war``
* ``rm /opt/domains/sormas/autodeploy/*.ear``

### Domain Libraries
For information on what libs are used see pom.xml in sormas-base project: https://git.symeda/sormas/sormas/blob/development/sormas-base/pom.xml
* Stop server:  
``service payara-sormas stop``
* ``rm /opt/domains/sormas/lib/*.jar``
* Make sure you're in the right directory:  
``cd /root/deploy/sormas/$(date +%F)``
* ``cp ./serverlibs/* /opt/domains/sormas/lib/``

### OSGi Bundles 
You only need to execute these commands when the SORMAS version installed on your server is older than 1.10.
* ``rm /opt/domains/sormas/autodeploy/bundles/*.jar``
* ``rm /opt/domains/sormas/autodeploy/.autodeploystatus/*``
* ``rm -r /opt/domains/sormas/osgi-cache/felix``
* ``rm -r /opt/domains/sormas/generated/``

### Database Update
**IMPORTANT: You should always do a backup when manually updating a SORMAS server, but you DON'T need to do a database update when you use version 1.21.0 or later as these are automatically performing the updates during the deployment process.**

#### Backup
You can ignore the following warning/error:
> could not change directory to "/root/deploy/sormas/backup": Permission denied

Create a database backup directory (if not already done):
* ``mkdir /root/deploy/sormas/backup``

Create a backup of the database:
* ``cd /root/deploy/sormas/backup``
* ``sudo -u postgres pg_dump -Fc -b sormas_db > "sormas_db_"`date +"%Y-%m-%d_%H-%M-%S"`".dump"``
* ``sudo -u postgres pg_dump -Fc -b sormas_audit_db > "sormas_audit_db_"`date +"%Y-%m-%d_%H-%M-%S"`".dump"``
* ``cd /root/deploy/sormas/$(date +%F)``	

If the update procedure fails, you can use the following command to restore the data:

``pg_restore --clean -U postgres -Fc -d sormas_db sormas_db_....dump``

#### Automatic Update
* ``cd /root/deploy/sormas/$(date +%F)/sql``
* Make the schema update script executable:  
``chmod +x ./database-update.sh``
* Execute the update script:  
``./database-update.sh``
* Confirm schema update by pressing enter when asked

#### Manual Update
You can update the database schema manually if the automatic update fails for some reason.
* Find out current schema version of database:  
``psql -U sormas_user -h localhost -W sormas_db``  
``SELECT * from schema_version ORDER BY version_number DESC LIMIT 1;``  
``\q``
* Edit sql/sormas_schema.sql:  
``Remove everything until after the INSERT with the read schema version``  
``Surround the remaining with BEGIN; and COMMIT;``
* Update the Database schema:  
``sudo -u postgres psql sormas_db < sql/sormas_schema.sql``

### Web Applications
* ``service payara-sormas start``
* Make sure you're in the right directory:  
``cd /root/deploy/sormas/$(date +%F)``
* ``cp apps/*.ear /opt/domains/sormas/autodeploy/``
* ``cp apps/*.war /opt/domains/sormas/autodeploy/``
* ``cp android/release/*.apk /var/www/sormas/downloads/``

### Final Steps
* Wait until the deployment is done (see log)
* ``a2ensite your.sormas.server.url.conf``
* ``apache2ctl graceful``
* Try to login at https://localhost:6081/sormas-ui (or the webadress of the server); if it doesn't work, restart the server
* Update the mobile app with the new apk file 

## Default Logins
These are the default users for demo systems. Make sure to deactivate them or change the password on productive systems:

### Admin
name: admin
pw: sadmin

### Surveillance Supervisor (web UI)
name: SunkSesa
pw: Sunkanmi

### Surveillance Officer (mobile app)
name: SanaObas
pw: Sanaa
