sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9
sudo apt-add-repository 'deb https://repos.azul.com/zulu/deb/ stable main'
sudo apt-get update
sudo apt-get install zulu11
mkdir /opt/sormas
mkdir /opt/sormas/docker
cd /opt/sormas/docker/
git clone https://github.com/hzi-braunschweig/SORMAS-Docker.git
apt-get install docker-compose
cd /opt/sormas/docker/SORMAS-Docker
docker-compose up -d
#list docker ip
docker ps -q | xargs -n 1 docker inspect --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}} {{ .Name }}' | sed 's/ \// /'

mkdir /opt/sormas/project/
cd /opt/sormas/project/
git clone https://github.com/mngoe/SORMAS-Project.git
cd /opt/sormas/project/SORMAS-Project

mkdir /root/deploy
mkdir /root/deploy/sormas
cd /root/deploy/sormas
SORMAS_VERSION=1.65.0
wget https://github.com/hzi-braunschweig/SORMAS-Project/releases/download/v${SORMAS_VERSION}/sormas_${SORMAS_VERSION}.zip
unzip sormas_${SORMAS_VERSION}.zip
mv deploy/ $(date +%F)
rm sormas_${SORMAS_VERSION}.zip
chmod +x $(date +%F)/server-setup.sh
cd $(date +%F)
cp /opt/sormas/project/SORMAS-Project/sormas-base/setup/server-ynote-setup.sh ./
## Modifier l'IP du server Docker PostGreSQL dans le fichier .sh
chmod +x ./server-ynote-setup.sh
chmod +x ./server-update.sh
./server-ynote-setup.sh
cp /opt/sormas/project/SORMAS-Project/sormas-preprod.properties /opt/domains/sormas/sormas.properties
cp /opt/sormas/project/SORMAS-Project/OCEAC.jpg /opt/sormas/documents/OCEAC.jpg
chmod +x /opt/domains/sormas/start-payara-sormas.sh
chmod +x /opt/domains/sormas/stop-payara-sormas.sh
./server-update.sh


#Install apache 
apt-get install certbot python3-certbot-apache
apt-get install apache2
a2enmod ssl
a2enmod rewrite
a2enmod proxy
a2enmod proxy_http
a2enmod headers
systemctl restart apache2

nano /etc/apache2/sites-available/oceac.ynote.africa.conf

<VirtualHost *:80>
        ServerName oceac.ynote.africa
        RewriteEngine On
        RewriteCond %{HTTPS} !=on
        RewriteRule ^/(.*) https://oceac.ynote.africa/$1 [R,L]
</VirtualHost>
<IfModule mod_ssl.c>
<VirtualHost *:443>
        ServerName oceac.ynote.africa
        ErrorLog /var/log/apache2/error.log
		LogLevel warn
		LogFormat "%h %l %u %t \"%r\" %>s %b _%D_ \"%{User}i\"  \"%{Connection}i\"  \"%{Referer}i\" \"%{User-agent}i\"" combined_ext
		CustomLog /var/log/apache2/access.log combined_ext
		
		SSLEngine on
		SSLCertificateFile /etc/letsencrypt/live/oceac.ynote.africa/fullchain.pem
		SSLCertificateKeyFile /etc/letsencrypt/live/oceac.ynote.africa/privkey.pem

		# disable weak ciphers and old TLS/SSL
		SSLProtocol all -SSLv3 -TLSv1 -TLSv1.1
		SSLCipherSuite ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-ECDSA-CHACHA20-POLY1305:ECDHE-RSA-CHACHA20-POLY1305:DHE$
		SSLHonorCipherOrder off

		ProxyRequests Off
		ProxyPass /sormas-ui http://localhost:6080/sormas-ui
		ProxyPassReverse /sormas-ui http://localhost:6080/sormas-ui
		ProxyPass /sormas-rest http://localhost:6080/sormas-rest
		ProxyPassReverse /sormas-rest http://localhost:6080/sormas-rest
</VirtualHost>
</IfModule>