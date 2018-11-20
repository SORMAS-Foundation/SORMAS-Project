#*******************************************************************************
# SORMAS® - Surveillance Outbreak Response Management & Analysis System
# Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#*******************************************************************************
#  Requirements:
#  * Payara 4.1.2.172 installed and proper Java-Version configured in asenv.conf 
#  * Database created
#  * setup directory prepared


# ------ Config BEGIN ------

# DEVELOPMENT ENVIRONMENT OR PRODUCTIVE SERVER?
#DEV_SYSTEM=true
DEV_SYSTEM=false

# WINDOWS: You may need to prefix the directories with /c
TEMP_DIR=/opt/sormas-temp
GENERATED_DIR=/opt/sormas-generated
PAYARA_HOME=/opt/payara-172/glassfish
DOMAIN_NAME=sormas
DOMAINS_HOME=/opt/domains
DOMAIN_DIR=${DOMAINS_HOME}/${DOMAIN_NAME}
# keep this directory if possible - otherwise also adjust directory in logback.xml
LOG_HOME=/var/log/glassfish/sormas
DOWNLOAD_DIR=/var/www/sormas/downloads
PORT_BASE=6000
PORT_ADMIN=6048

# DB
DB_USER=sormas_user
DB_USER_AUDIT=sormas_user
DB_PW=sormas_db
DB_PW_AUDIT=sormas_db
DB_NAME=sormas_db
DB_NAME_AUDIT=sormas_audit_db
DB_SERVER=localhost
DB_PORT=5432

# MAIL
MAIL_FROM=dummy@sormas.org

# ------ Config END ------

echo "--- all values set properly?"
echo "WINDOWS: You may need to prefix the directories with /c"
echo "WINDOWS: setfacl and chown messages can be ignored"
echo "------"
echo "Temp Directory: ${TEMP_DIR}"
echo "Generated Directory: ${GENERATED_DIR}"
echo "Payara Home: ${PAYARA_HOME}"
echo "Domain Name: ${DOMAIN_NAME}"
echo "Domain Home: ${DOMAIN_DIR}"
echo "Log Home: ${LOG_HOME}"
echo "Port Base: ${PORT_BASE}"
echo "Admin Port: ${PORT_ADMIN}"

read -p "Press [Enter] to continue..."

# create needed directories and set user rights
mkdir -p ${DOMAINS_HOME}
mkdir -p ${TEMP_DIR}
mkdir -p ${GENERATED_DIR}
mkdir -p ${LOG_HOME}
mkdir -p ${DOWNLOAD_DIR}
chown -R payara:payara ${LOG_HOME}
setfacl -m u:postgres:rwx ${TEMP_DIR} 
setfacl -m u:payara:rwx ${TEMP_DIR}
setfacl -m u:postgres:rwx ${GENERATED_DIR}
setfacl -m u:payara:rwx ${GENERATED_DIR}

# setting ASADMIN_CALL and creating domain
${PAYARA_HOME}/bin/asadmin create-domain --domaindir ${DOMAINS_HOME} --portbase ${PORT_BASE} --nopassword ${DOMAIN_NAME}
ASADMIN="${PAYARA_HOME}/bin/asadmin --port ${PORT_ADMIN}"

read -p "Press [Enter] to continue..."

# copying server-libs
cp serverlibs/*.jar ${DOMAIN_DIR}/lib/

# copying bundles
mkdir -p ${DOMAIN_DIR}/autodeploy/bundles
cp -a bundles/*.jar ${DOMAIN_DIR}/autodeploy/bundles/

echo "copying libs completed"
read -p "Press [Enter] to continue..."

cat << END > ${DOMAIN_DIR}/config/login.conf
${DOMAIN_NAME}Realm { org.wamblee.glassfish.auth.FlexibleJdbcLoginModule required; };
END

chown -R payara:payara ${PAYARA_HOME}
read -p "Press [Enter] to continue..."

${PAYARA_HOME}/bin/asadmin start-domain --domaindir ${DOMAINS_HOME} ${DOMAIN_NAME}
read -p "Press [Enter] to continue..."

# General domain settings
${ASADMIN} delete-jvm-options -Xmx512m
${ASADMIN} create-jvm-options -Xmx1024m

# JDBC pool
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.ConnectionPoolDataSource --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME}:serverName=${DB_SERVER}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}DataPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}DataPool jdbc/${DOMAIN_NAME}DataPool

# Pool for audit log
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.XADataSource --datasourceclassname org.postgresql.xa.PGXADataSource --isconnectvalidatereq true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME_AUDIT}:serverName=${DB_SERVER}:user=${DB_USER_AUDIT}:password=${DB_PW_AUDIT}" ${DOMAIN_NAME}AuditlogPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}AuditlogPool jdbc/AuditlogPool

# User datasource without pool (flexible jdbc realm seems to keep connections in cache)
${ASADMIN} create-jdbc-connection-pool --restype javax.sql.DataSource --datasourceclassname org.postgresql.ds.PGSimpleDataSource --isconnectvalidatereq true --nontransactionalconnections true --validationmethod custom-validation --validationclassname org.glassfish.api.jdbc.validation.PostgresConnectionValidation --property "portNumber=${DB_PORT}:databaseName=${DB_NAME}:serverName=${DB_SERVER}:user=${DB_USER}:password=${DB_PW}" ${DOMAIN_NAME}UsersDataPool
${ASADMIN} create-jdbc-resource --connectionpoolid ${DOMAIN_NAME}UsersDataPool jdbc/${DOMAIN_NAME}UsersDataPool

read -p "Press [Enter] to continue..."

${ASADMIN} set server-config.security-service.activate-default-principal-to-role-mapping=true
${ASADMIN} create-auth-realm --classname org.wamblee.glassfish.auth.FlexibleJdbcRealm --property "jaas.context=${DOMAIN_NAME}Realm:sql.password=SELECT password FROM users WHERE username\=? AND aktiv\=true:sql.groups=SELECT userrole FROM userroles INNER JOIN users ON userroles.user_id\=users.id WHERE users.username\=?:sql.seed=SELECT seed FROM users WHERE username\=?:datasource.jndi=jdbc/${DOMAIN_NAME}UsersDataPool:assign-groups=AUTHED_USER:password.digest=SHA-256:charset=UTF-8" ${DOMAIN_NAME}-realm
${ASADMIN} set server-config.security-service.default-realm=${DOMAIN_NAME}-realm

read -p "Press [Enter] to continue..."

${ASADMIN} create-javamail-resource --mailhost localhost --mailuser user --fromaddress ${MAIL_FROM} mail/MailSession

${ASADMIN} create-custom-resource --restype java.util.Properties --factoryclass org.glassfish.resources.custom.factory.PropertiesFactory --property "org.glassfish.resources.custom.factory.PropertiesFactory.fileName=\${com.sun.aas.instanceRoot}/sormas.properties" sormas/Properties

cp sormas.properties ${DOMAIN_DIR}
cp logback.xml ${DOMAIN_DIR}/config/
# fixes outdated certificate
cp cacerts.jks ${DOMAIN_DIR}/config/
chown -R payara:payara ${DOMAIN_DIR}

read -p "Press [Enter] to continue..."

# Logging
${ASADMIN} create-jvm-options -Dlogback.configurationFile=\${com.sun.aas.instanceRoot}/config/logback.xml
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.file=${LOG_HOME}/server.log
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.maxHistoryFiles=14
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=0
${ASADMIN} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.rotationOnDateChange=true
${ASADMIN} set-log-levels org.wamblee.glassfish.auth.HexEncoder=SEVERE
${ASADMIN} set-log-levels javax.enterprise.system.util=SEVERE

read -p "Press [Enter] to continue..."

if [ $DEV_SYSTEM != true ]; then
  #make the payara listen to localhost only
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
fi


${PAYARA_HOME}/bin/asadmin stop-domain --domaindir ${DOMAINS_HOME} ${DOMAIN_NAME}

read -p "Press [Enter] to continue..."

chown -R payara:payara ${PAYARA_HOME}

echo 'setup completed. Please run the server using the init.d script for the proper permissions' 

echo 'Checklist'
echo '  - update-rc.d payara-* defaults already executed?'
echo '  - sormas.properties adjusted to this system?'
echo '  - verify logback.xml'
echo '  - JVM parameters fit?'
echo '  - payara-default-domains deleted?'
echo '  - Java-Version in asenv.conf?'
echo '  - Apache properly configured?'
