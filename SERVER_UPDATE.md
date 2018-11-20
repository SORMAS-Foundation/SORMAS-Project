# Updating a SORMAS Server
All commands mentioned are linux commands.

## Prepare Deployment
* Get the latest release files (deploy.zip): https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest
* Copy/upload to /root/deploy/sormas/$(date +%F)
* ``cd /root/deploy/sormas/$(date +%F)``
* Show log to track the deploy process: ``tail -f /var/log/glassfish/sormas/server.log``

## Version specifics
* 0.15.0: update the glassfish config with the new apk filename: sormas-release.apk
* 0.16.0: update the glassfish config with the export path, SMS sender name, auth key and secret
* 0.19.0: update the glassfish config with the generated files path
* 1.4.0: update the glassfish config with the automatic classification feature

## Maintainance Mode
* a2dissite your.sormas.server.url.conf
* service apache2 reload

## Undeployment
.. while server domain is running
* ``rm /opt/domains/sormas/autodeploy/*.war``
* ``rm /opt/domains/sormas/autodeploy/*.ear``

## Domain Libraries
For information on what libs are used see pom.xml in sormas-base project: https://git.symeda/sormas/sormas/blob/development/sormas-base/pom.xml
* stop server: ``service payara-sormas stop``
* ``rm /opt/domains/sormas/lib/*.jar``
* ``cp ./serverlibs/* /opt/domains/sormas/lib/``

## OSGi Bundles
* ``rm /opt/domains/sormas/autodeploy/bundles/*.jar``
* ``rm /opt/domains/sormas/autodeploy/.autodeploystatus/*``
* ``rm -r /opt/domains/sormas/osgi-cache/felix``
* ``rm -r /opt/domains/sormas/generated/``
* ``cp ./bundles/* /opt/domains/sormas/autodeploy/bundles/``

## Database
* Create a database backup directory (if not already done)
    * ``mkdir /root/deploy/sormas/backup``
* Create a backup of the database
    * ``cd /root/deploy/sormas/backup``
    * ``sudo -u postgres pg_dump -Fc -b sormas_db > "sormas_db_"`date +"%Y-%m-%d_%H-%M-%S"`".dump"`` \
     (to restore the data you can use: sudo -u postgres pg_restore -Fc -d sormas_db sormas_db_....dump)
    * ``sudo -u postgres pg_dump -Fc -b sormas_audit_db > "sormas_audit_db_"`date +"%Y-%m-%d_%H-%M-%S"`".dump"``
    * ``cd /root/deploy/sormas/$(date +%F)``	
* Update the database schema
    * ``cd /root/deploy/sormas/$(date +%F)/sql``
    * make the schema update script executable: ``chmod +x ./database-update.sh``
    * execute the update script: ``./database-update.sh``
    * confirm schema update by pressing enter when asked
* Alternative: Manual database update
    * Find out current schema version of database:
        * ``psql -U sormas_user -h localhost -W sormas_db``
        * ``SELECT * from schema_version ORDER BY version_number DESC LIMIT 1;``
        * ``\q``
    * Edit sql/sormas_schema.sql
        * ``Remove everything until after the INSERT with the read schema version``
        * ``Surround the remaining with BEGIN; and COMMIT;``
    * Update the Database schema: ``sudo -u postgres psql sormas_db < sql/sormas_schema.sql``
* If something goes wrong, restore the database using ``pg_restore -U sormas_user -Fc -d sormas_db < sormas_db_....``

## Web Applications
* ``service payara-sormas start``
* ``cd /root/deploy/sormas/$(date +%F) (just to make sure you're in the right directory)``
* ``cp apps/*.ear /opt/domains/sormas/autodeploy/``
* ``cp apps/*.war /opt/domains/sormas/autodeploy/``
* ``cp android/release/*.apk /var/www/sormas/downloads/``

## Final Steps

* Wait until the deployment is done (see log)
* a2ensite your.sormas.server.url.conf
* service apache2 reload
* Try to login at https://localhost:6081/sormas-ui (or the webadress of the server). 
  If it doesn't work: restart the server
* Update the mobile app with the new apk file 

## Login

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