sink('install-packages.log')
update.packages(repos="https://cloud.r-project.org", ask=FALSE)
install.packages(c("epicontacts", "outbreaks", "RPostgreSQL", "GGally", "network", "sna", "visNetwork", "dplyr"), repos="https://cloud.r-project.org")
