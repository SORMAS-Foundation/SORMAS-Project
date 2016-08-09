

THIS NEEDS TO BE UPDATED FROM glassfish-config.bat



#  Voraussetzungen:
#  * Payara 4.1.1 installiert und korrekte Java-Version in asenv.conf eingetragen
#  * Datenbank eingerichtet
#  * glassfish-config-Verzeichnis vorbereitet und das Script wird aus ebendiesem Verzeichnis heraus ausgeführt (caseplus-base/build.xml:collect-all)
#  * Verzeichnis /var/log/glassfish/po-eap existiert und gehört dem User glassfish
#
# Nach Abschluss
#  * chown -R glassfish:glassfish /var/log/glassfish/po-eap
#  * domain.xml prüfen (RAM usw.)

#

# ------ Config BEGIN ------

# GLASSFISH
GLASSFISH_HOME=/opt/payara-162-caseplus-po-eap/glassfish
DOMAIN_NAME=po-eap
DOMAIN_DIR=${GLASSFISH_HOME}/domains/${DOMAIN_NAME}
LOG_HOME=/var/log/glassfish/caseplus-po-eap
PORT_BASE=7000
PORT_ADMIN=7048

# DB
DB_USER=po_eap_user
DB_USER_AUDITLOG=po_eap_user
DB_NAME=po_eap_db
DB_NAME_AUDITLOG=po_eap_auditlog_db
DB_SERVER=localhost
DB_PORT=5432
#DB_PW
#DB_PW_AUDITLOG
read -p "Password for ${DB_NAME}? -> " DB_PW
read -p "Password for ${DB_NAME_AUDITLOG}? -> " DB_PW_AUDITLOG

#von Hand ausführen:
#su postgres
#psql
#CREATE USER po_eap_user WITH PASSWORD '...' CREATEDB;
#CREATE DATABASE po_eap_db WITH OWNER = po_eap_user ENCODING = 'UTF8';
#CREATE DATABASE po_eap_auditlog_db WITH OWNER = po_eap_user ENCODING = 'UTF8';
#\q
#exit

# MAIL
MAIL_FROM=noreply@symeda.de

# ------ Config END ------

echo '--- bevor es los geht, sind alle Werte richtig konfiguriert?'
echo 
echo 'GF_HOME: ${GLASSFISH_HOME}'
echo 'Domain Name: ${DOMAIN_NAME}'
echo 'Domain Home: ${DOMAIN_DIR}'
echo 'Log Home: ${LOG_HOME}'
echo 'Port Base: ${PORT_BASE}'
echo 'Admin Port: ${PORT_ADMIN}'

read -p "Press [Enter] to continue..."

#Installationverzeichnis auf glassfish ändern, damit beim Kopieren auch der richtige Besiter erhalten bleibt.
#chown -R glassfish:glassfish /root/deploy/

# gf-modules patchen
##alte Versionen entfernen

rm ${GLASSFISH_HOME}/modules/jboss-logging.jar

##neue Version einfügen
cp gf-modules/*.jar ${GLASSFISH_HOME}/modules/


ASADMIN="${GLASSFISH_HOME}/bin/asadmin --port ${PORT_ADMIN}"

${GLASSFISH_HOME}/bin/asadmin create-domain --portbase ${PORT_BASE} ${DOMAIN_NAME}
read -p "Press [Enter] to continue..."

#server-libs bereit legen
cp serverlibs/*.jar ${DOMAIN_DIR}/lib/

#bundles bereit legen
mkdir -p ${DOMAIN_DIR}/autodeploy/bundles
cp -a bundles/*.jar ${DOMAIN_DIR}/autodeploy/bundles/

#Libs kopieren beendet
read -p "Press [Enter] to continue..."


cat << END > ${GLASSFISH_HOME}/domains/${DOMAIN_NAME}/config/login.conf
${DOMAIN_NAME}Realm { org.wamblee.glassfish.auth.FlexibleJdbcLoginModule required; };
END

chown -R glassfish:glassfish ${GLASSFISH_HOME}
read -p "Press [Enter] to continue..."

${GLASSFISH_HOME}/bin/asadmin start-domain ${DOMAIN_NAME}
read -p "Press [Enter] to continue..."

# regulärer Pool
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.ConnectionPoolDataSource --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME}:serverName=${DB_SERVER}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}DataPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}DataPool jdbc/${DOMAIN_NAME}DataPool

# Pool für Auditlog
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.XADataSource --datasourceclassname org.postgresql.xa.PGXADataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME_AUDITLOG}:serverName=${DB_SERVER}:user=${DB_USER_AUDITLOG}:password=${DB_PW_AUDITLOG}" ${DOMAIN_NAME}AuditlogPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}AuditlogPool jdbc/${DOMAIN_NAME}AuditlogPool

# ohne Connection Pool, weil die flexible jdbc realm scheinbar die Connection im Cache hält
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.DataSource --datasourceclassname org.postgresql.ds.PGSimpleDataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME}:serverName=${DB_SERVER}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}UsersDataPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}UsersDataPool jdbc/${DOMAIN_NAME}UsersDataPool
read -p "Press [Enter] to continue..."

${ASADMIN} set server-config.security-service.activate-default-principal-to-role-mapping=true
${ASADMIN} create-auth-realm --classname org.wamblee.glassfish.auth.FlexibleJdbcRealm --property "jaas.context=${DOMAIN_NAME}Realm:sql.password=SELECT password FROM realm_user WHERE useremail\=? AND aktiv\=true:sql.groups=SELECT groups  FROM realm_user_groups  WHERE useremail\=?:sql.seed=SELECT seed FROM realm_user WHERE useremail\=?:datasource.jndi=jdbc/${DOMAIN_NAME}UsersDataPool:assign-groups=AUTHED_USER:password.digest=SHA-256:charset=UTF-8" ${DOMAIN_NAME}-realm
${ASADMIN} set server-config.security-service.default-realm=${DOMAIN_NAME}-realm

${ASADMIN} set server-config.http-service.sso-enabled=true
${ASADMIN} set server-config.http-service.virtual-server.server.sso-cookie-secure=true
read -p "Press [Enter] to continue..."

${ASADMIN} create-javamail-resource --mailhost localhost --mailuser user --fromaddress ${MAIL_FROM} mail/MailSession

${ASADMIN} create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property "org.glassfish.resources.custom.factory.PropertiesFactory.fileName=domains/${DOMAIN_NAME}/${DOMAIN_NAME}.properties" po-eap/Properties
cp ./${DOMAIN_NAME}.properties ${DOMAIN_DIR}
chown glassfish:glassfish ${DOMAIN_DIR}/${DOMAIN_NAME}.properties

cp logback.xml ${DOMAIN_DIR}/config/
chown glassfish:glassfish ${DOMAIN_DIR}/config/logback.xml

read -p "Press [Enter] to continue..."

# Logging
${ASADMIN} create-jvm-options -Dlogback.configurationFile=\${com.sun.aas.instanceRoot}/config/logback.xml
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.file=${LOG_HOME}/server.log
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.maxHistoryFiles=14
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=0
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationOnDateChange=true
${ASADMIN} set-log-levels org.wamblee.glassfish.auth.HexEncoder.level=SEVERE
read -p "Press [Enter] to continue..."

#Login-Auditierung aktivieren
${ASADMIN} set configs.config.server-config.security-service.audit-enabled=true
${ASADMIN} create-audit-module --classname=de.symeda.glassfish.audit.LoginAttemptAuditModule --target=server-config LoginAttemptAudit
read -p "Press [Enter] to continue..."

#nur auf localhost hören
${ASADMIN} set configs.config.server-config.http-service.virtual-server.server.network-listeners=http-listener-1
${ASADMIN} delete-network-listener --target=server-config http-listener-2
${ASADMIN} set configs.config.server-config.network-config.network-listeners.network-listener.admin-listener.address=127.0.0.1
${ASADMIN} set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.address=127.0.0.1
${ASADMIN} set configs.config.server-config.iiop-service.iiop-listener.orb-listener-1.address=127.0.0.1
${ASADMIN} set configs.config.server-config.iiop-service.iiop-listener.SSL.address=127.0.0.1
${ASADMIN} set configs.config.server-config.iiop-service.iiop-listener.SSL_MUTUALAUTH.address=127.0.0.1
${ASADMIN} set configs.config.server-config.jms-service.jms-host.default_JMS_host.host=127.0.0.1
${ASADMIN} set configs.config.server-config.admin-service.jmx-connector.system.address=127.0.0.1
read -p "Press [Enter] to continue..."

#Applications deployed
cp -a applications/*.*ar ${DOMAIN_DIR}/autodeploy/
read -p "Press [Enter] to continue..."


#templates einfügen
mkdir ${DOMAIN_DIR}/templates/
cp -a templates/* ${DOMAIN_DIR}/templates/
read -p "Press [Enter] to continue..."


${GLASSFISH_HOME}/bin/asadmin stop-domain ${DOMAIN_NAME}
read -p "Press [Enter] to continue..."

##nicht den Server starten, weil er dann als root ausgeführt wird
#${GLASSFISH_HOME}/bin/asadmin start-domain ${DOMAIN_NAME}

chown -R glassfish:glassfish ${GLASSFISH_HOME}

echo 'Installation abgeschlossen. Bitte Server mit init.d Skript starten wegen der Berechtigungen' 

echo 'Folgendes noch überprüfen'
echo '  - update-rc.d payara-* defaults bereits ausgeführt?'
echo '  - caseplus.properties korrekt an das System angepasst?'
echo '  - logback.xml überprüfen'
echo '  - JVM-Dimensionierung angemessen?'
echo '  - payara-default-domains gelöscht?'
echo '  - SQL ausgeführt?'
echo '  - Java-Version in asenv.conf?'
echo '  - Apache korrekt konfiguriert?'
echo '  - Konfiguration aus der alten Domain (properties, logback) übernehmen?'
