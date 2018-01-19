## Project Structure
The project consists of the following modules:

- sormas-api: general business logic and definitions for data exchange between app and server
- sormas-app: the android app
- sormas-backend: server entity services, facades, etc.
- sormas-base: base project that also contains build scripts
- sormas-ear: the ear needed to build the application
- sormas-rest: the rest interface
- sormas-ui: the web application

## Server Updates
For server installation see below.
All commands mentioned are linux commands.
 
### Updating the SORMAS domain
(ignore this when you are setting up a whole development enviroment, as described below)

#### Prepare Deployment
* Get the latest release files (deploy.zip): https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest
* Copy/upload to /root/deploy/sormas/$(date +%F)
* ``cd /root/deploy/sormas/$(date +%F)``

#### Maintainance Mode
* a2dissite your.sormas.server.url.conf
* service apache2 reload

#### Undeployment
.. while server domain is running
* ``rm /opt/domains/sormas/autodeploy/*.war``
* ``rm /opt/domains/sormas/autodeploy/*.ear``

#### Domain Libraries
For information on what libs are used see pom.xml in sormas-base project: https://git.symeda/sormas/sormas/blob/development/sormas-base/pom.xml
* stop server: ``service payara-sormas stop``
* ``rm /opt/domains/sormas/lib/*.jar``
* ``cp ./serverlibs/* /opt/domains/sormas/lib/``

#### OSGi Bundles
* ``rm /opt/domains/sormas/autodeploy/bundles/*.jar``
* ``rm /opt/domains/sormas/autodeploy/.autodeploystatus/*``
* ``rm -r /opt/domains/sormas/osgi-cache/felix``
* ``rm -r /opt/domains/sormas/generated/``
* ``cp ./bundles/* /opt/domains/sormas/autodeploy/bundles/``

#### Database
* Create a database backup directory (if not already done)
    * ``mkdir /root/deploy/sormas/backup``
* Create a backup of the database
    * ``cd /root/deploy/sormas/backup``
    * ``pg_dump -Fc -b -h localhost -U sormas_user sormas_db > "sormas_db_"`date +"%Y-%m-%d_%H-%M-%S"`".dump"``
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
    * Update the Database schema: ``psql -U sormas_user -W sormas_db < sql/sormas_schema.sql``
* If something goes wrong, restorte the database using ``pg_restore -U sormas_user -Fc -d sormas_db < sormas_db_....``

#### Web Applications
* ``service payara-sormas start``
* ``cd /root/deploy/sormas/$(date +%F) (just to make sure you're in the right directory)``
* ``cp apps/*.ear /opt/domains/sormas/autodeploy/``
* ``cp apps/*.war /opt/domains/sormas/autodeploy/``
* ``cp android/debug/*.apk /var/www/sormas/downloads/``

#### Final Steps

* Wait until the deployment is done (see log):  ``tail -f /var/log/glassfish/sormas/server.log``
* a2ensite your.sormas.server.url.conf
* service apache2 reload
* Try to login at https://localhost:6081/sormas-ui (or the webadress of the server). 
  If it doesn't work: restart the server
* Update the mobile app with the new apk file 


## Server Installation
### Postgres Database

* Install PostgreSQL (currently 9.5 or 9.6) on your system
* Install the "temporal tables" addon for Postgres (https://github.com/arkhipov/temporal_tables)
    * Windows: Download latest version for your postgres version: https://github.com/arkhipov/temporal_tables/releases/latest Then you must copy the DLL from the project into the PostgreSQL's lib directory and the .sql and .control files into the directory share\extension.	
    * Linux (see https://github.com/arkhipov/temporal_tables#installation):
        * ``sudo apt-get install libpq-dev``
        * ``sudo apt-get install postgresql-server-dev-all``
        * ``sudo apt install pgxnclient``
        * ``pgxn install temporal_tables``
* Create a PostgreSQL database named "sormas_db" and "sormas_audit_db" with user "sormas_user" (make sure to generate a secure password) as its owner.
    * ``sudo -u postgres psql``
    * ``CREATE USER sormas_user WITH PASSWORD '***' CREATEDB;``
    * ``CREATE DATABASE sormas_db WITH OWNER = sormas_user ENCODING = 'UTF8';``
    * ``CREATE DATABASE sormas_audit_db WITH OWNER = sormas_user ENCODING = 'UTF8';``
    * ``\q``
* Setup the audit log database schema using the sormas_audit_schema.sql from the latest release: ``sudo -u postgres psql sormas_audit_db < sql/sormas_audit_schema.sql``
	
### Payara Application Server
* Download payara 4.1.2.172 (https://www.payara.fish/all_downloads) and extract it to the directory where your servers should be located (e.g. /opt/payara-172)
* Remove the default domains from the server:
    * ``rm -R /opt/payara-172/glassfish/domains/domain1``
    * ``rm -R /opt/payara-172/glassfish/domains/payaradomain``
* Create a directory for your domains. Put it next to the payara server or somewhere else: ``mkdir /opt/domains``

### SORMAS Domain
* Get the latest SORMAS build from github: https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest (deploy.zip). 
* Upload to /root/deploy/sormas/$(date +%F)
* ``cd /root/deploy/sormas/$(date +%F)``
* Open ``glassfish-config.sh`` (or glassfish-config.bat on windows) in a text editor and change GLASSFISH_HOME, DOMAINS_HOME, PORT_BASE, PORT_ADMIN, DB_PW, DB_PW_AUDIT, MAIL_FROM to appropriate values for your server.
* Make the file executable: ``chmod +x glassfish-config.sh``
* Set up a payara domain called "sormas" by executing it: ``./glassfish-config.sh`` Press enter whenever asked for it.
* Adjust the logging configuration in opt/domains/sormas/config/logback.xml based on your needs 
* Make sure the domain folder is owned by the glassfish user: ``chown -R glassfish:glassfish opt/domains/sormas/``
* Copy the startup script to init.d: ``cp payara-sormas /etc/init.d``
* Add the server startup sequence: ``update-rc.d payara-sormas defaults``
* Start the server: ``service payara-sormas start``
* Update the SORMAS domain (see above)

### Apache Web Server
When you are using SORMAS in a production environment you should use a http server like Apache 2 instead of putting the payara server in the first line.
Here are some things that you should do to configure the apache server as proxy:

* Activate all needed modules

		a2enmod ssl
		a2enmod rewrite
		a2enmod proxy
		a2enmod proxy_http
		a2enmod headers
* create a new site /etc/apache2/sites-available/your.sormas.server.url.conf (e.g. sormas.org.conf)
* Force SSL secured connections: redirect from http to https

		<VirtualHost *:80>
			ServerName your.sormas.server.url
			RewriteEngine On
			RewriteCond %{HTTPS} !=on
			RewriteRule ^/(.*) https://your.sormas.server.url/$1 [R,L]
		</VirtualHost>
		<IfModule mod_ssl.c>
		<VirtualHost *:443>
			ServerName your.sormas.server.url
			...
		</VirtualHost>
		</IfModule>
* Configure Logging:

        ErrorLog /var/log/apache2/error.log
        LogLevel warn
        CustomLog /var/log/apache2/access.log combined
* SSL key config

        SSLEngine on
        SSLCertificateFile    /etc/ssl/certs/your.sormas.server.url.crt
        SSLCertificateKeyFile /etc/ssl/private/your.sormas.server.url.key
        SSLCertificateChainFile /etc/ssl/certs/your.sormas.server.url.ca-bundle
* Add a proxy pass to the local port:

		ProxyRequests Off
		ProxyPass /sormas-ui http://localhost:5080/sormas-ui
		ProxyPassReverse /sormas-ui http://localhost:5080/sormas-ui
		ProxyPass /sormas-rest http://localhost:5080/sormas-rest
		ProxyPassReverse /sormas-rest http://localhost:5080/sormas-rest
* Activate output compression (very important!): 

        <IfModule mod_deflate.c>
                AddOutputFilterByType DEFLATE text/plain text/html text/xml
                AddOutputFilterByType DEFLATE text/css text/javascript
                AddOutputFilterByType DEFLATE application/json
                AddOutputFilterByType DEFLATE application/xml application/xhtml+xml
                AddOutputFilterByType DEFLATE application/javascript application/x-javascript
                DeflateCompressionLevel 1
        </IfModule></code>

* Provide the android apk

        Options -Indexes
        Alias "/download/app-debug.apk" "/var/www/sormas/downloads/app-debug.apk"


## Development Environment
- Install the latest Eclipse version, Git for Windows and (optional) a Git client such as TortoiseGit if you don't want to handle version control from the command line/separately for the Eclipse and Android Studio projects
- Open the Git Bash and execute the command <code>git config --global branch.development.rebase true</code> (which ensures that rebase is used when pulling rather than merge)
- Clone the SORMAS-Open repository and import the projects to Eclipse
	- If you're using Eclipse to clone, choose "File -> Import -> Git -> Projects from Git" and continue until you're asked to create a new project from the cloned repository; click cancel instead and use "File -> Import -> Maven -> Existing Maven Projects" to import the separate projects into your workspace
	- If you've cloned the repository from the command line or a Git client, you obviously only need to perform the last step
- Highlight all Eclipse projects and choose "Maven -> Update Project" from the right click menu; perform the update for all projects
- Install Glassfish Tools and (recommended) the Vaadin Plugin for Eclipise (make sure to untick the option to also install the commercial UI designer)
- Set up a Glassfish 4 server in Eclipse and enter the credentials you specified when setting up the server
- Make a copy of "build.properties.example" contained in "sormas-base", rename it to "build.properties" and set "glassfish.domain.root" to the location of the sormas domain located in the "glassfish/domains" folder inside your payara installation
- Install the latest Android Studio version (to avoid any errors, make sure to start the installation with admin rights and choose a path for the Android SDK that contains no whitespaces)
- Open Android Studio and import the "sormas-app" project from Eclipse
- Drag the "build.xml" file contained in "sormas-base" into the Ant view in Eclipse and execute the "install [default]", "deploy-serverlibs" and "deploy-bundles" scripts
- Build the Android Studio project by executing the gradle build (this may be done automatically)
- Start the Glassfish server and deploy "sormas-ear", "sormas-rest" and "sormas-ui" by dragging the respective projects onto it
- Open your browser and type in "https://localhost:6081/sormas-ui" to test whether everything has been set up correctly (and to use the application)


## Release workflow

As release workflow we use the Gitflow Workflow (see https://www.atlassian.com/git/tutorials/comparing-workflows#gitflow-workflow).

For releasing a new version of this project, we integrated the <code>jgitflow-maven-plugin</code> (see https://bitbucket.org/atlassian/jgit-flow/wiki/Home).

Steps to build a new version:

1. Checkout branch <code>development</code>.
2. Run <code>mvn install -Pwith-app</code>.
3. If the build was successful, run <code>mvn jgitflow:release-start jgitflow:release-finish -Pwith-app,with-dep-poms</code>.
	- You will be asked for the release version. Leave this empty to keep the current snapshot version as release version number (<code>-SNAPSHOT</code> will be cut of by jgitflow-maven-plugin).
	- You will be asked for the next development version. Leave this empty and the plugin will increment the micro release number (<code>1.0.1-SNAPSHOT</code> becomes <code>1.0.2-SNAPSHOT</code>). If you want to alter the version just type e.g. <code>1.1.0-SNAPSHOT</code>.

4. The result is that the current state of branch <code>development</code> gets merged to branch <code>master</code> (without -SNAPSHOT), tagged as <code>releases/version-1.0.1</code> and the development version is automatically increased.

### Version numbers

Version Numbers = major.minor.micro

For correct generation of android version codes the releases have to be at least minor releases. Micro releases are reserved for hotfixes of a published release.
- Finish of a release: Increase major or minor number
- Finish of a hotfix (merged directly back to branch <code>master</code>): Increase micro number


### Android Version Code

The <code>versionCode</code> for the Android app is autogenerated by the projects version.
The convention for the versionCode <code>aaabbbccd</code> (generated of version <code>aaa.bbb.cc</code>) is:
  - d: one digit for SNAPSHOT (0), Release Candidates (RC1 to RC8 = 1..8) or Final Release (9)
  - cc: two digits for micro releases (with leading zeros)
  - bbb: three digits for minor releases (with leading zeros)
  - aaa: major releases (if a > 0)


### Local configuration for jgitflow-maven-plugin

The <code>jgitflow-maven-plugin</code> needs credentials for git, which are configurated as variables in <code>sormas-base/pom.xml</code>. 
To use it you need to configure this in your .m2/settings.xml (or pass it as arguments when executing the plugin).

        <profiles>
                <profile>
                        <id>github-config</id>
                        <!-- For jgitflow-maven-plugin against github.com -->
                        <properties>
                                <github.sormas.user>myUserName</github.sormas.user>
                                <github.sormas.password>myPassword</github.sormas.password>
                        </properties>
                </profile>
        </profiles>

        <activeProfiles>
                <activeProfile>github-config</activeProfile>
        </activeProfiles>
