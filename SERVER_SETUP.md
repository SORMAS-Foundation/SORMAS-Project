# Installing a SORMAS Server
**Note: All commands below are Linux commands. On windows systems use the corresponding commands or the windows explorer.**

* [Postgres Database](#postgres-database)
* [Payara Application Server](#payara-application-server)
* [SORMAS Domain](#sormas-domain)
* [Apache Web Server](#apache-web-server)

## Postgres Database

* Install PostgreSQL (currently 9.5 or 9.6) on your system
* **set max_prepared_transactions = 64 (at least) in postgresql.conf** (e.g. /etc/postgresql/9.5/main/postgresql.conf)
* Install the "temporal tables" addon for Postgres (https://github.com/arkhipov/temporal_tables)
    * Windows: Download latest version for your postgres version: https://github.com/arkhipov/temporal_tables/releases/latest Then you must copy the DLL from the project into the PostgreSQL's lib directory and the .sql and .control files into the directory share\extension.	
    * Linux (see https://github.com/arkhipov/temporal_tables#installation):
        * ``sudo apt-get install libpq-dev``
        * ``sudo apt-get install postgresql-server-dev-all``
        * ``sudo apt install pgxnclient``
	* Check for GCC: ``gcc --version`` - install if missing
        * ``pgxn install temporal_tables``
* Create a PostgreSQL database named "sormas_db" and "sormas_audit_db" with user "sormas_user" (make sure to generate a secure password) as its owner.
    * ``sudo -u postgres psql``
    * ``CREATE USER sormas_user WITH PASSWORD '***' CREATEDB;``
    * ``CREATE DATABASE sormas_db WITH OWNER = sormas_user ENCODING = 'UTF8';``
    * ``CREATE DATABASE sormas_audit_db WITH OWNER = sormas_user ENCODING = 'UTF8';``
    * ``\q``
* Setup the audit log database schema using the sormas_audit_schema.sql from the latest release: ``sudo -u postgres psql sormas_audit_db < sql/sormas_audit_schema.sql``
* Setup the sormas database schema using the sormas_schema.sql from the latest release: ``sudo -u postgres psql sormas_audit_db < sql/sormas_schema.sql``
	
## Payara Application Server
* Download payara 4.1.2.172 (https://www.payara.fish/all_downloads) and extract it to the directory where your servers should be located (e.g. /opt/payara-172)
* Remove the default domains from the server:
    * ``rm -R /opt/payara-172/glassfish/domains/domain1``
    * ``rm -R /opt/payara-172/glassfish/domains/payaradomain``
* Create a directory for your domains. Put it next to the payara server or somewhere else: ``mkdir /opt/domains``

## SORMAS Domain
* Get the latest SORMAS build from github: https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest (deploy.zip). 
* Upload to /root/deploy/sormas/$(date +%F)
* ``cd /root/deploy/sormas/$(date +%F)``
* Open ``glassfish-config.sh`` (or glassfish-config.bat on windows) in a text editor and change GLASSFISH_HOME, DOMAINS_HOME, PORT_BASE, PORT_ADMIN, DB_PW, DB_PW_AUDIT, MAIL_FROM to appropriate values for your server.
* Make the file executable: ``chmod +x glassfish-config.sh``
* Set up a payara domain called "sormas" by executing it: ``./glassfish-config.sh`` Press enter whenever asked for it.
* Adjust the logging configuration in opt/domains/sormas/config/logback.xml based on your needs (e.g. configure and activate email appender)
* in opt/domains/sormas/config/logging.properties replace ``org.wamblee.glassfish.auth.HexEncoder.level.level=SEVERE`` with ``  org.wamblee.glassfish.auth.HexEncoder.level=SEVERE``
* Make sure the domain folder is owned by the glassfish user: ``chown -R glassfish:glassfish opt/domains/sormas/``
* Create two folders called "/opt/sormas-temp" and "/opt/sormas-generated" (if you choose another path, you have to adjust the sormas.properties file accordingly)
* Set user rights for postgres and glassfish users: ``setfacl -m u:postgres:rwx /opt/sormas-temp
setfacl -m u:glassfish:rwx /opt/sormas-temp
setfacl -m u:postgres:rwx /opt/sormas-generated
setfacl -m u:glassfish:rwx /opt/sormas-generated``
* Copy the startup script to init.d: ``cp payara-sormas /etc/init.d``
* Add the server startup sequence: ``update-rc.d payara-sormas defaults``
* Start the server: ``service payara-sormas start``
* [Update the SORMAS domain](SERVER_UPDATE.md) (not necessary if you are setting up your local development environment)

## Apache Web Server
**Note: This is not necessary for development systems.**
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
		AliasMatch "/downloads/sormas-(.*)" "/var/www/sormas/downloads/sormas-$1"

* In case you need to update the site config while the server is running, use the following command to publish the changes without the need for a reload:

        apache2ctl graceful
