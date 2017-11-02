
REM  Requirements:
REM  * Payara 4.1.1 installed
REM  * Database created
REM  * glassfish-config-directory prepared


REM ------ Config BEGIN ------

REM GLASSFISH
set GLASSFISH_HOME=\srv\payara-172\glassfish
set DOMAIN_NAME=sormas
set DOMAINS_HOME=\srv\domains
set DOMAIN_DIR=%DOMAINS_HOME%\%DOMAIN_NAME%
set LOG_HOME=\var\log\glassfish\sormas
set PORT_BASE=6000
set PORT_ADMIN=6048

REM DB
set DB_SERVER=localhost
set DB_PORT=5432
set DB_NAME=sormas_db
set DB_USER=sormas_user
set DB_PW=sormas_db
set DB_NAME_AUDIT=sormas_audit_db
set DB_USER_AUDIT=sormas_user
set DB_PW_AUDIT=sormas_db

REM MAIL
set MAIL_FROM=noreply@symeda.de

REM ------ Config END ------

REM placholder for synching with sh script



REM patching gf-modules
REM -- removing old versions

REM del %GLASSFISH_HOME%\modules\jboss-logging.jar

REM -- placing new versions
REM copy /Y .\gf-modules\*.jar %GLASSFISH_HOME%\modules

REM setting ASADMIN_CALL and creating domain
set ASADMIN=CALL %GLASSFISH_HOME%\bin\asadmin --port %PORT_ADMIN%
CALL %GLASSFISH_HOME%/bin/asadmin create-domain --domaindir %DOMAINS_HOME% --portbase %PORT_BASE% %DOMAIN_NAME%

Echo Press [Enter] to continue...
PAUSE >nul

REM copying server-libs
copy /Y .\serverlibs\*.jar %DOMAIN_DIR%\lib

REM copying bundles
mkdir %DOMAIN_DIR%\autodeploy\bundles
copy /Y .\bundles\*.jar %DOMAIN_DIR%\autodeploy\bundles

REM copying libs completed
Echo Press [Enter] to continue...
PAUSE >nul

ECHO %DOMAIN_NAME%Realm { org.wamblee.glassfish.auth.FlexibleJdbcLoginModule required; }; >> %GLASSFISH_HOME%/domains/%DOMAIN_NAME%/config/login.conf


REM placholder for synching with sh script

CALL %GLASSFISH_HOME%/bin/asadmin start-domain --domaindir %DOMAINS_HOME% %DOMAIN_NAME%
Echo Press [Enter] to continue...
PAUSE >nul

REM JDBC pool
%ASADMIN% create-jdbc-connection-pool --restype javax.sql.ConnectionPoolDataSource --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=%DB_PORT%:databaseName=%DB_NAME%:serverName=%DB_SERVER%:user=%DB_USER%:password=%DB_PW%" %DOMAIN_NAME%DataPool
%ASADMIN% create-jdbc-resource --connectionpoolid %DOMAIN_NAME%DataPool jdbc/%DOMAIN_NAME%DataPool

REM Pool for audit log
%ASADMIN% create-jdbc-connection-pool --restype javax.sql.XADataSource --datasourceclassname org.postgresql.xa.PGXADataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=%DB_PORT%:databaseName=%DB_NAME_AUDIT%:serverName=%DB_SERVER%:user=%DB_USER_AUDIT%:password=%DB_PW_AUDIT%" %DOMAIN_NAME%AuditlogPool
%ASADMIN% create-jdbc-resource --connectionpoolid %DOMAIN_NAME%AuditlogPool jdbc/AuditlogPool

REM User datasource without pool (flexible jdbc realm seems to keep connections in cache)
%ASADMIN% create-jdbc-connection-pool --restype javax.sql.DataSource --datasourceclassname org.postgresql.ds.PGSimpleDataSource --isconnectvalidatereq true --nontransactionalconnections true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=%DB_PORT%:databaseName=%DB_NAME%:serverName=%DB_SERVER%:user=%DB_USER%:password=%DB_PW%" %DOMAIN_NAME%UsersDataPool
%ASADMIN% create-jdbc-resource --connectionpoolid %DOMAIN_NAME%UsersDataPool jdbc/%DOMAIN_NAME%UsersDataPool

Echo Press [Enter] to continue...
PAUSE >nul

%ASADMIN% set server-config.security-service.activate-default-principal-to-role-mapping=true
%ASADMIN% create-auth-realm --classname org.wamblee.glassfish.auth.FlexibleJdbcRealm --property "jaas.context=%DOMAIN_NAME%Realm:sql.password=SELECT password FROM users WHERE username\=? AND aktiv\=true:sql.groups=SELECT userrole FROM userroles INNER JOIN users ON userroles.user_id\=users.id WHERE users.username\=?:sql.seed=SELECT seed FROM users WHERE username\=?:datasource.jndi=jdbc/%DOMAIN_NAME%UsersDataPool:assign-groups=AUTHED_USER:password.digest=SHA-256:charset=UTF-8" %DOMAIN_NAME%-realm
%ASADMIN% set server-config.security-service.default-realm=%DOMAIN_NAME%-realm

%ASADMIN% set server-config.http-service.sso-enabled=true
%ASADMIN% set server-config.http-service.virtual-server.server.sso-cookie-secure=true

Echo Press [Enter] to continue...
PAUSE >nul

%ASADMIN% create-javamail-resource --mailhost localhost --mailuser user --fromaddress %MAIL_FROM% mail/MailSession

%ASADMIN% create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property "org.glassfish.resources.custom.factory.PropertiesFactory.fileName=domains/%DOMAIN_NAME%/sormas.properties" sormas/Properties

copy .\%sormas.properties %DOMAIN_DIR%

copy /Y .\logback.xml %DOMAIN_DIR%\config

Echo Press [Enter] to continue...
PAUSE >nul

REM Logging
%ASADMIN% create-jvm-options -Dlogback.configurationFile=${com.sun.aas.instanceRoot}/config/logback.xml
REM %ASADMIN% set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.file=/logs/server.log
%ASADMIN% set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.maxHistoryFiles=14
%ASADMIN% set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=0
%ASADMIN% set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationOnDateChange=true
%ASADMIN% set-log-levels org.wamblee.glassfish.auth.HexEncoder.level=SEVERE
%ASADMIN% set-log-levels javax.enterprise.system.util.level=SEVERE

Echo Press [Enter] to continue...
PAUSE >nul

REM Login-Auditierung aktivieren
REM %ASADMIN% set configs.config.server-config.security-service.audit-enabled=true
REM %ASADMIN% create-audit-module --classname=de.symeda.glassfish.audit.LoginAttemptAuditModule --target=server-config LoginAttemptAudit

Echo Press [Enter] to continue...
PAUSE >nul

Echo make the glassfish listen to localhost only (commented out)
REM %ASADMIN% set configs.config.server-config.http-service.virtual-server.server.network-listeners=http-listener-1
REM %ASADMIN% delete-network-listener --target=server-config http-listener-2
REM %ASADMIN% set configs.config.server-config.network-config.network-listeners.network-listener.admin-listener.address=127.0.0.1
REM %ASADMIN% set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.address=127.0.0.1
REM %ASADMIN% set configs.config.server-config.iiop-service.iiop-listener.orb-listener-1.address=127.0.0.1
REM %ASADMIN% set configs.config.server-config.iiop-service.iiop-listener.SSL.address=127.0.0.1
REM %ASADMIN% set configs.config.server-config.iiop-service.iiop-listener.SSL_MUTUALAUTH.address=127.0.0.1
REM %ASADMIN% set configs.config.server-config.jms-service.jms-host.default_JMS_host.host=127.0.0.1
REM %ASADMIN% set configs.config.server-config.admin-service.jmx-connector.system.address=127.0.0.1

Echo Press [Enter] to continue...
PAUSE >nul

REM Applications deployen
REM copy /Y .\applications\*.*ar %DOMAIN_DIR%\autodeploy

REM Echo Press [Enter] to continue...
REM PAUSE >nul


REM Templates einfügen
REM mkdir %DOMAIN_DIR%\templates
REM xcopy /Y /E .\templates\* %DOMAIN_DIR%\templates

REM Echo Press [Enter] to continue...
REM PAUSE >nul


CALL %GLASSFISH_HOME%/bin/asadmin stop-domain --domaindir %DOMAINS_HOME% %DOMAIN_NAME%

Echo Press [Enter] to restart server now. Cancel with STRG+C
PAUSE >nul

CALL %GLASSFISH_HOME%/bin/asadmin start-domain --domaindir %DOMAINS_HOME% %DOMAIN_NAME%

Echo Checklist
Echo   - logback.xml installed?
Echo   - JVM parameters fit?
Echo   - payara default domains deleted?

