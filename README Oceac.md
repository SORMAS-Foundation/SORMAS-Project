#------- Install Java 11 
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9
sudo apt-add-repository 'deb https://repos.azul.com/zulu/deb/ stable main'
sudo apt-get update
sudo apt-get install zulu11

#------- PostgreSQL 
# Create the file repository configuration:
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

# Import the repository signing key:
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

# Update the package lists:
sudo apt-get update

# Install the latest version of PostgreSQL.
# If you want a specific version, use 'postgresql-12' or similar instead of 'postgresql':
sudo apt-get -y install postgresql
# ----
sudo apt-get install libpq-dev
sudo apt-get install postgresql-server-dev-all
sudo apt install pgxnclient
#Check for GCC:
gcc --version # and install if missing
sudo pgxn install temporal_tables



mkdir /root/deploy
mkdir /root/deploy/sormas
cd /root/deploy/sormas
SORMAS_VERSION=1.63.1
wget https://github.com/hzi-braunschweig/SORMAS-Project/releases/download/v${SORMAS_VERSION}/sormas_${SORMAS_VERSION}.zip
unzip sormas_${SORMAS_VERSION}.zip
mv deploy/ $(date +%F)
rm sormas_${SORMAS_VERSION}.zip
chmod +x $(date +%F)/server-setup.sh