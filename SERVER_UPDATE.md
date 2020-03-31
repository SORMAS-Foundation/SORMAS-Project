# Updating a SORMAS Server
SORMAS releases starting from 1.21.0 contain a script that automatically updates and deploys the server. If you are using an older version and therefore need to do a manual server update, please download the 1.21.0 release files and use the commands specified in the server-update.sh script.

## Preparations
Note: You can skip this step if you've just set up your SORMAS server and have already downloaded the latest release.

* Get the latest release files (deploy.zip) from https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest
* Unzip the archive and copy/upload its contents to **/root/deploy/sormas/$(date +%F)**

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
These are the default users for demo systems. Make sure to deactivate them or change the passwords on productive systems:

### Admin
name: admin
pw: sadmin

### Surveillance Supervisor (web UI)
name: SunkSesa
pw: Sunkanmi

### Surveillance Officer (mobile app)
name: SanaObas
pw: Sanaa
