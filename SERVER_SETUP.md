

# Installing a SORMAS Server
**Note: This guide explains how to set up a SORMAS server on Linux and Windows systems, the latter only being intended for usage on development systems. Please also note that certain parts of the setup script will not be executed on Windows.**

## Content
* [Prerequisites](#prerequisites)
  * [Java 8](#java-8)
  * [Postgres Database](#postgres-database)
* [SORMAS Server](#sormas-server)
* [Web Server Setup](#web-server-setup)
  * [Apache Web Server](#apache-web-server)
  * [Firewall](#firewall)
  * [Postfix Mail Server](#postfix-mail-server)
  * [Security](#security)
* [Troubleshooting](#troubleshooting)

## Related
* [Creating an App for a Demo Server](DEMO_APP.md)

## Prerequisites

### Java 8

* Download and install the latest Java 8 **JDK** (not JRE) for your operating system. We suggest to use Zulu OpenJDK: https://www.azul.com/downloads/zulu/
  * **Linux**: https://docs.azul.com/zulu/zuludocs/#ZuluUserGuide/PrepareZuluPlatform/AttachAPTRepositoryUbuntuOrDebianSys.htm
        
		sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9
		sudo apt-add-repository 'deb http://repos.azulsystems.com/ubuntu stable main'
		sudo apt-get update
		sudo apt-get install zulu-8
  * **Windows**: For testing and development environments we suggest to download and run the installer of the Java 8 **JDK** for 32 or 64 bit client systems (depending on your system).
* You can check your Java version from the shell/command line using: ``java -version``

### Postgres Database

* Install PostgreSQL (currently 9.5, 9.6 or 10) on your system (manuals for all OS can be found here: https://www.postgresql.org/download)
* set **max_prepared_transactions = 64** (at least) in postgresql.conf (e.g. ``/etc/postgresql/10.0/main/postgresql.conf``; ``C:/Program Files/PostgreSQL/10.0/data``) - make sure the property is uncommented
* Install the "temporal tables" extension for Postgres (https://github.com/arkhipov/temporal_tables)
    * **Windows**: Download the latest version for your Postgres version: https://github.com/arkhipov/temporal_tables/releases/latest, then copy the DLL from the project into the PostgreSQL's lib directory and the .sql and .control files into the directory share\extension.	
    * **Linux** (see https://github.com/arkhipov/temporal_tables#installation):
        * ``sudo apt-get install libpq-dev``
        * ``sudo apt-get install postgresql-server-dev-all``
        * ``sudo apt install pgxnclient``
        * Check for GCC: ``gcc --version`` and install if missing
        * ``sudo pgxn install temporal_tables``
        * The packages can be removed afterward
	   
## SORMAS Server	

* Get the latest SORMAS build by downloading the ZIP archive from the latest release on GitHub: https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest 
* **Linux**:
  * Unzip the archive and copy/upload its contents to **/root/deploy/sormas/$(date +%F)**
  * ``cd /root/deploy/sormas/$(date +%F)``
  * Make the setup script executable with ``chmod +x server-setup.sh``
* **Windows**:
  * Download & install Git for Windows. This will provide a bash emulation that you can use to run the setup script: https://gitforwindows.org/
  * Unzip the ZIP archive (e.g. into you download directory)
  * Open Git Bash and navigate to the setup sub-directory
* Optional: Open ``server-setup.sh`` in a text editor to customize the install paths, database access and ports for the server. The default ports are 6080 (HTTP), 6081 (HTTPS) and 6048 (admin)
* Set up the database and a Payara domain for SORMAS by executing the setup script: ``sudo -s ./server-setup.sh`` Press enter whenever asked for it
* **IMPORTANT**: Make sure the script executed successfully. If anything goes wrong you need to fix the problem (or ask for help), then delete the created domain directory and re-execute the script.
* **IMPORTANT**: Adjust the SORMAS configuration for your country in /opt/domains/sormas/sormas.properties
* Adjust the logging configuration in ``/opt/domains/sormas/config/logback.xml`` based on your needs (e.g. configure and activate email appender)
* Linux: [Update the SORMAS domain](SERVER_UPDATE.md)

## Web Server Setup

### Apache Web Server
**Note: This is not necessary for development systems.** When you are using SORMAS in a production environment you should use a http server like Apache 2 instead of putting the Payara server in the first line.
Here are some things that you should do to configure the Apache server as a proxy:

* Activate all needed modules:

		a2enmod ssl
		a2enmod rewrite
		a2enmod proxy
		a2enmod proxy_http
		a2enmod headers
* Create a new site /etc/apache2/sites-available/your.sormas.server.url.conf (e.g. sormas.org.conf)
* Force SSL secured connections: redirect from http to https:

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
* Configure logging:

        ErrorLog /var/log/apache2/error.log
        LogLevel warn
        LogFormat "%h %l %u %t \"%r\" %>s %b _%D_ \"%{User}i\"  \"%{Connection}i\"  \"%{Referer}i\" \"%{User-agent}i\"" combined_ext
        CustomLog /var/log/apache2/access.log combined_ext
* SSL key config:

        SSLEngine on
        SSLCertificateFile    /etc/ssl/certs/your.sormas.server.url.crt
        SSLCertificateKeyFile /etc/ssl/private/your.sormas.server.url.key
        SSLCertificateChainFile /etc/ssl/certs/your.sormas.server.url.ca-bundle
		
        # disable weak ciphers and old TLS/SSL
        SSLProtocol all -SSLv3 -TLSv1 -TLSv1.1
        SSLCipherSuite ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE$
        SSLHonorCipherOrder off		
* Add a proxy pass to the local port:

		ProxyRequests Off
		ProxyPass /sormas-ui http://localhost:6080/sormas-ui
		ProxyPassReverse /sormas-ui http://localhost:6080/sormas-ui
		ProxyPass /sormas-rest http://localhost:6080/sormas-rest
		ProxyPassReverse /sormas-rest http://localhost:6080/sormas-rest
* Configure security settings:

		Header always set X-Content-Type-Options "nosniff"
		Header always set X-Xss-Protection "1; mode=block"
		# Disable Caching
		Header always set Cache-Control "no-cache, no-store, must-revalidate, private"
		Header always set Pragma "no-cache"
		
		Header always set Content-Security-Policy \
            "default-src 'none'; \
            object-src 'self'; \
            script-src 'self' 'unsafe-inline' 'unsafe-eval'; \
            connect-src https://fonts.googleapis.com https://fonts.gstatic.com 'self'; \
            img-src *; \
            style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; \
            font-src https://fonts.gstatic.com 'self'; \
            frame-src 'self'; \
            worker-src 'self'; \
            manifest-src 'self'; \
            frame-ancestors 'self'		

		# The Content-Type header was either missing or empty.
		# Ensure each page is setting the specific and appropriate content-type value for the content being delivered.
		AddType application/vnd.ms-fontobject    .eot
		AddType application/x-font-opentype      .otf
		AddType image/svg+xml                    .svg
		AddType application/x-font-ttf           .ttf
		AddType application/font-woff            .woff
* Activate output compression (very important!): 

        <IfModule mod_deflate.c>
                AddOutputFilterByType DEFLATE text/plain text/html text/xml
                AddOutputFilterByType DEFLATE text/css text/javascript
                AddOutputFilterByType DEFLATE application/json
                AddOutputFilterByType DEFLATE application/xml application/xhtml+xml
                AddOutputFilterByType DEFLATE application/javascript application/x-javascript
                DeflateCompressionLevel 1
        </IfModule></code>

* Provide the android apk:

        Options -Indexes
		AliasMatch "/downloads/sormas-(.*)" "/var/www/sormas/downloads/sormas-$1"

* For the Apache 2 security configuration we suggest the following settings (``/etc/apache2/conf-available/security.conf``):

		ServerTokens Prod
		ServerSignature Off
		TraceEnable Off

		Header always set Strict-Transport-Security "max-age=15768000; includeSubDomains; preload"
		Header unset X-Frame-Options
		Header always set X-Frame-Options SAMEORIGIN
		Header unset Referrer-Policy
		Header always set Referrer-Policy "same-origin"
		Header edit Set-Cookie "(?i)^((?:(?!;\s?HttpOnly).)+)$" "$1;HttpOnly"
		Header edit Set-Cookie "(?i)^((?:(?!;\s?Secure).)+)$" "$1;Secure"

		Header unset X-Powered-By
		Header unset Server
		
		
* In case you need to update the site config while the server is running, use the following command to publish the changes without the need for a reload:

        apache2ctl graceful
		
### Firewall

* The server should only publish the ports that are needed. For SORMAS this is port 80 (HTTP) and 443 (HTTPS). In addition you will need the SSH port to access the server for admin purposes.
* We suggest to use UFW (Uncomplicated Firewall) which provides a simple interface to iptables:

		sudo apt-get install ufw
		sudo ufw default deny incoming
		sudo ufw default allow outgoing
		sudo ufw allow ssh
		sudo ufw allow http
		sudo ufw allow https
		sudo ufw enable

### Postfix Mail Server

* Install postfix and mailutils:

		apt install aptitude
		aptitude install postfix
		-> choose "satelite system"
		apt install mailutils
	
* Configure your system:

		nano /etc/aliases
		-> add "root: enter-your@support-email-here.com"
		nano /opt/domains/sormas/config/logback.xml
		-> make sure "EMAIL_ERROR" appender is active and sends out to your email address

### Testing the Server Setup

Use SSL Labs to test your server security config: https://www.ssllabs.com/ssltest


## Troubleshooting

### Problem: Login fails

Check that the users table does have a corresponding entry. If not, the database initialization that is done when deploying sormas-ear.ear probably had an error.

### Problem: Server is out of memory

Old servers were set up with a memory size of less than 2048MB. You can change this using the following commands:

	/opt/payara-172/glassfish/bin/asadmin --port 6048 delete-jvm-options -Xmx512m
	/opt/payara-172/glassfish/bin/asadmin --port 6048 delete-jvm-options -Xmx1024m
	/opt/payara-172/glassfish/bin/asadmin --port 6048 create-jvm-options -Xmx2048m

Alternative: You can edit the settings directly in the domain.xml in the config directory of the SORMAS domain. Just search for ``Xmx`` - there should be two entries that need to be changed.
