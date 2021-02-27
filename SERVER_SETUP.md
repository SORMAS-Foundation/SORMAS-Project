

# Installing a SORMAS Server
**Note: This guide explains how to set up a SORMAS server on Linux and Windows systems, the latter only being intended for usage on development systems. Please also note that certain parts of the setup script will not be executed on Windows.**

## Content
- [Installing a SORMAS Server](#installing-a-sormas-server)
  - [Content](#content)
  - [Related](#related)
  - [Prerequisites](#prerequisites)
    - [Java 11](#java-11)
    - [Postgres Database](#postgres-database)
  - [SORMAS Server](#sormas-server)
  - [Keycloak Server](#keycloak-server)
    - [Keycloak as a Docker container](#keycloak-as-a-docker-container)
    - [Keycloak as a standalone installation](#keycloak-as-a-standalone-installation)
    - [Connect Keycloak to an already running instance of SORMAS](#connect-keycloak-to-an-already-running-instance-of-sormas)
    - [Keycloak configuration](#keycloak-configuration)
  - [Web Server Setup](#web-server-setup)
    - [Apache Web Server](#apache-web-server)
    - [Firewall](#firewall)
    - [Postfix Mail Server](#postfix-mail-server)
    - [Testing the Server Setup](#testing-the-server-setup)
  - [R Software Environment](#r-software-environment)
  - [SORMAS to SORMAS Certificate Setup](#sormas-to-sormas-certificate-setup)
  - [Troubleshooting](#troubleshooting)
    - [Problem: Login fails](#problem-login-fails)
    - [Problem: Server is out of memory](#problem-server-is-out-of-memory)

## Related

* [Creating an App for a Demo Server](DEMO_APP.md)
* [SORMAS Docker Repository](https://github.com/hzi-braunschweig/SORMAS-Docker)

## Prerequisites

### Java 11

* Download and install the Java 11 **JDK** (not JRE) for your operating system. We suggest using the [Zulu OpenJDK](https://www.azul.com/downloads/zulu/).
  * **[Linux](https://docs.azul.com/zulu/zuludocs/#ZuluUserGuide/PrepareZuluPlatform/AttachAPTRepositoryUbuntuOrDebianSys.htm)**:
```bash
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9
sudo apt-add-repository 'deb https://repos.azul.com/zulu/deb/ stable main'
sudo apt-get update
sudo apt-get install zulu11
```
  * **Windows**: For testing and development environments we suggest to download and run the installer of the Java 11 **JDK** for 32 or 64 bit client systems (depending on your system).
* You can check your Java version from the shell/command line using: ``java -version``

### Postgres Database

* Install PostgreSQL (currently 9.5, 9.6 or 10) on your system (manuals for all OS can be found here: <https://www.postgresql.org/download>)
* Set **max_connections = 288** and **max_prepared_transactions = 256** (at least, sum of all connection pools) in ``postgresql.conf`` (e.g. ``/etc/postgresql/10.0/main/postgresql.conf``; ``C:/Program Files/PostgreSQL/10.0/data``) - make sure the property is uncommented
* Install the "temporal tables" extension for Postgres (<https://github.com/arkhipov/temporal_tables>)
  * **Windows**: Download the latest version for your Postgres version: <https://github.com/arkhipov/temporal_tables/releases/latest>, then copy the DLL from the project into the PostgreSQL's lib directory and the .sql and .control files into the directory share\extension.
  * **Linux** (see <https://github.com/arkhipov/temporal_tables#installation)>

```bash
sudo apt-get install libpq-dev
sudo apt-get install postgresql-server-dev-all
sudo apt install pgxnclient
#Check for GCC:
gcc --version # and install if missing
sudo pgxn install temporal_tables
# The packages can be removed afterward
```


## SORMAS Server

* Get the latest SORMAS build by downloading the ZIP archive from the latest release on GitHub: <https://github.com/hzi-braunschweig/SORMAS-Open/releases/latest>
* **Linux**:
  * Unzip the archive, copy/upload its contents to **/root/deploy/sormas/$(date +%F)** and make the setup script executable.
        ```bash
        cd /root/deploy/sormas
        SORMAS_VERSION=1.y.z
        wget https://github.com/hzi-braunschweig/SORMAS-Project/releases/download/v${SORMAS_VERSION}/sormas_${SORMAS_VERSION}.zip
        unzip sormas_${SORMAS_VERSION}.zip
        mv deploy/ $(date +%F)
        rm sormas_${SORMAS_VERSION}.zip
        chmod +x $(date +%F)/server-setup.sh
        ```
* **Windows**:
  * Download & install Git for Windows. This will provide a bash emulation that you can use to run the setup script: <https://gitforwindows.org/>
  * Unzip the ZIP archive (e.g. into you download directory)
  * Open Git Bash and navigate to the setup sub-directory
* Optional: Open ``server-setup.sh`` in a text editor to customize the install paths, database access and ports for the server. The default ports are 6080 (HTTP), 6081 (HTTPS) and 6048 (admin). **Important:** Do not change the name of the database user. The pre-defined name is used in the statements executed in the database.
* Set up the database and a Payara domain for SORMAS by executing the setup script: ``sudo -s ./server-setup.sh`` Press enter whenever asked for it
* **IMPORTANT**: Make sure the script executed successfully. If anything goes wrong you need to fix the problem (or ask for help), then delete the created domain directory and re-execute the script.
* **IMPORTANT**: Adjust the SORMAS configuration for your country in /opt/domains/sormas/sormas.properties
* Adjust the logging configuration in ``/opt/domains/sormas/config/logback.xml`` based on your needs (e.g. configure and activate email appender)
* Linux: [Update the SORMAS domain](SERVER_UPDATE.md)

## Keycloak Server

Keycloak can be set up in two ways:
* as a Docker container (for just using Keycloak approach)
* as a Standalone installation (for doing development in Keycloak like themes, SPIs)

### Keycloak as a Docker container
*To be done only in the situation when SORMAS is already installed on the machine as a standalone installation.*

*For complete Docker setup see the [SORMAS-Docker](https://github.com/hzi-braunschweig/SORMAS-Docker/tree/keycloak-integration) repository.*

**Prerequisites**
* SORMAS Server is installed
* PostgreSQL is installed
* Docker is installed
* Open and edit [keycloak-setup.sh](sormas-base/setup/keycloak/keycloak-setup.sh) with your system's actual values *(on Windows use Git Bash)*.

**Setup**
* Run [keycloak-setup.sh](sormas-base/setup/keycloak/keycloak-setup.sh)
* Update `sormas.properties` file in the SORMAS domain with the property `authentication.provider=KEYCLOAK`


### Keycloak as a standalone installation

**Prerequisites**
* SORMAS Server is installed
* PostgreSQL is installed

**Setup**

Setting Keycloak up as a standalone installation [Server Installation and Configuration Guide](https://www.keycloak.org/docs/11.0/server_installation/#installation)
* Make sure to configure Keycloak with PostgreSQL Database [Relational Database Setup](https://www.keycloak.org/docs/11.0/server_installation/#_database)
* Set up an Admin User
* Copy the `themes` folder content to `${KEYCLOAK_HOME}/themes` [Deploying Themes](https://www.keycloak.org/docs/11.0/server_development/#deploying-themes)
* Deploy the `sormas-keycloak-service-provider` [Using Keycloak Deployer](https://www.keycloak.org/docs/11.0/server_development/#using-the-keycloak-deployer)
* Update the [SORMAS.json](sormas-base/setup/keycloak/SORMAS.json) file by replacing the following placeholders: `${SORMAS_SERVER_URL}`, `${KEYCLOAK_SORMAS_UI_SECRET}`, `${KEYCLOAK_SORMAS_BACKEND_SECRET}`, `${KEYCLOAK_SORMAS_REST_SECRET}`
* Create the SORMAS Realm by importing [SORMAS.json](sormas-base/setup/keycloak/SORMAS.json) see [Create a New Realm](https://www.keycloak.org/docs/11.0/server_admin/#_create-realm)
* Update the `sormas-*` clients by generating new secrets for them
* Update the realm's email settings to allow sending emails to users

To update the SORMAS Server run the following commands

```shell script
${ASADMIN} set-config-property --propertyName=payara.security.openid.clientSecret --propertyValue=${KEYCLOAK_SORMAS_UI_SECRET} --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.clientId --propertyValue=sormas-ui --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.scope --propertyValue=openid --source=domain
${ASADMIN} set-config-property --propertyName=payara.security.openid.providerURI --propertyValue=http://localhost:${KEYCLOAK_PORT}/keycloak/auth/realms/SORMAS --source=domain
${ASADMIN} set-config-property --propertyName=sormas.rest.security.oidc.json --propertyValue="{\"realm\":\"SORMAS\",\"auth-server-url\":\"http://localhost:${KEYCLOAK_PORT}/auth\",\"ssl-required\":\"external\",\"resource\":\"sormas-rest\",\"credentials\":{\"secret\":\"${KEYCLOAK_SORMAS_REST_SECRET}\"},\"confidential-port\":0,\"principal-attribute\":\"preferred_username\",\"enable-basic-auth\":true}" --source=domain
${ASADMIN} set-config-property --propertyName=sormas.backend.security.oidc.json --propertyValue="{\"realm\":\"SORMAS\",\"auth-server-url\":\"http://localhost:${KEYCLOAK_PORT}/auth/\",\"ssl-required\":\"external\",\"resource\":\"sormas-backend\",\"credentials\":{\"secret\":\"${KEYCLOAK_SORMAS_BACKEND_SECRET}\"},\"confidential-port\":0}" --source=domain
```

where:
* `${ASADMIN}` - represents the location to `${PAYARA_HOME}\bin\asadmin`
* `${KEYCLOAK_PORT}` - the port on which keycloak will run
* `${KEYCLOAK_SORMAS_UI_SECRET}` - is the secret generated in Keycloak for the `sormas-ui` client
* `${KEYCLOAK_SORMAS_REST_SECRET}` - is the secret generated in Keycloak for the `sormas-rest` client
* `${KEYCLOAK_SORMAS_BACKEND_SECRET}` - is the secret generated in Keycloak for the `sormas-backend` client

Then update `sormas.properties` file in the SORMAS domain with the property `authentication.provider=KEYCLOAK`

### Connect Keycloak to an already running instance of SORMAS

*after setting up Keycloak as one of the described options above*

In case Keycloak is set up alongside an already running instance of SORMAS, these are the steps to follow to make sure already existing users can access the system:
1. Manually create an admin user in Keycloak for the SORMAS realm [Creating a user](https://www.keycloak.org/docs/11.0/getting_started/index.html#creating-a-user) *(username has to be the same as admin's username in SORMAS)*
2. Login to SORMAS and trigger the **Sync Users** button from the **Users** page
3. This will sync users to Keycloak keeping their original password - see [SORMAS Keycloak Service Provider](sormas-keycloak-service-provider/README.md) for more information about this

### Keycloak configuration

More about the default configuration and how to customize can be found here [Keycloak](sormas-base/doc/keycloak.md)

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
        ```bash
        sudo apt-get install ufw
        sudo ufw default deny incoming
        sudo ufw default allow outgoing
        sudo ufw allow ssh
        sudo ufw allow http
        sudo ufw allow https
        sudo ufw enable
        ```
### Postfix Mail Server

* Install postfix and mailutils:
        ```bash
        apt install aptitude
        aptitude install postfix
        -> choose "satelite system"
        apt install mailutils
        ```
* Configure your system:
        ```bash
        nano /etc/aliases
        -> add "root: enter-your@support-email-here.com"
        nano /opt/domains/sormas/config/logback.xml
        -> make sure "EMAIL_ERROR" appender is active and sends out to your email address
        ```
### Testing the Server Setup

Use SSL Labs to test your server security config: <https://www.ssllabs.com/ssltest>

## R Software Environment

In order to enable disease network diagrams in the contact dashboard, R and several extension packages are required.
Then the Rscript executable has to be configured in the ``sormas.properties`` file.
This can be conveniently accomplished by executing the R setup script from the SORMAS ZIP archive (see [SORMAS Server](#sormas-server)):

* If the SORMAS installation has been customized, ``r-setup.sh`` the install paths may have to be adjusted accordingly with a text editor.
* Execute R setup script:
        ```bash
        chmod +x r-setup.sh
        ./r-setup.sh
        ```
* Follow the instructions of the script.

## SORMAS to SORMAS Certificate Setup

To be able to communicate with other SORMAS instances, there are some additional steps which need to be taken, in order to set
up the certificate and the truststore. Please see the [related guide](GUIDE_SORMAS2SORMAS_CERTIFICATE.md) for detailed instructions regarding
SORMAS to SORMAS setup.
<br/>

## Troubleshooting

### Problem: Login fails

Check that the users table does have a corresponding entry. If not, the database initialization that is done when deploying sormas-ear.ear probably had an error.

### Problem: Server is out of memory

Old servers were set up with a memory size of less than 2048MB. You can change this using the following commands:

```bash
/opt/payara-172/glassfish/bin/asadmin --port 6048 delete-jvm-options -Xmx512m
/opt/payara-172/glassfish/bin/asadmin --port 6048 delete-jvm-options -Xmx1024m
/opt/payara-172/glassfish/bin/asadmin --port 6048 create-jvm-options -Xmx2048m
```

Alternative: You can edit the settings directly in the domain.xml in the config directory of the SORMAS domain. Just search for ``Xmx`` - there should be two entries that need to be changed.
