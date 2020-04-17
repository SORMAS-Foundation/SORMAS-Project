# Sormas Testing Suite


# Integration into Jenkins CI/CD

The integration of Katalon Studio in Jenkins is quite simple:

## Prequisites

System / OS packages needed to be installed:

- xvfb 
- firefox(-esr)


Jenkins plugins needed to be installed:

- katalon
- github
- xvfb
- git

## Jenkins configuration (FreeStyle element / mandatory)

![alt text](images/J_config_scm.png "GitHub config")

![alt text](images/J_config_build.png "Build config")

![alt text](images/J_config_post_build.png "PostBuild config")
