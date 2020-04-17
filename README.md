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

### GitHub config

Repository URL: URL to Katalon Sormas tests

![alt text](images/J_config_scm.png "GitHub config")

### Build config

Command arguments: `-browserType="Firefox" -retry=0 -statusDelay=15 -testSuitePath="Test Suites/Login/LoginRoles" -apiKey=<your api key>`

Xvfb-run configuration (for Linux): `-s "-screen 0 1024x768x24"`

![alt text](images/J_config_build.png "Build config")

### Post build config

Archive: `Reports/**`

JUnit-Tests: `Reports/**/*.xml`

![alt text](images/J_config_post_build.png "PostBuild config")
