# Troubleshooting

Please consult this collection of solutions to common problems if you have any issues before issuing a support request or asking developers for help. Also note that this resource has only been added recently and will be extended in the future. If you have encountered (and fixed) any issue that you think would be worth adding to this list, please don't hesitate to let us know!

## Android Application FAQ

**Q:** I don't see a logout option anywhere in the mobile app. How can I change my user?  
**A:** The logout option is hidden by default because users in the field often don't know their own passwords, but their devices are instead set up by a supervisor. If you want to change your user, go to the Settings screen and tap the version number five times to bring up additional options, including the logout option.

## Debugging Performance Problems

Performance logging can be used to find out which part of the code or system might be responsible for long-running functions in the application. This helps the developers to identify the source of the problems quicker and find out whether there are several problems at once or performance problems that manifest in Java execution time instead of slow SQL queries.

**Caution: Do not expose any private data!** Whenever you debug problems on an instance with productive data, please make sure that the logged information does not contain any personal data like real person names, birth dates, etc. to the public. Never provide such data anywhere on GitHub or any other online tool!

### Switch on Performance Logging in SORMAS

1. Open the logback file located in your domain (default path: `/opt/domains/sormas/config/logback.xml`) and change the log level of `PerformanceLoggingInterceptor` to `DEBUG` or `TRACE`. The config change will be recognized during runtime within 30s. After that you will see detailed log entries in the SORMAS log.

2. Set the log level back to its default once the logging has been done since it can reduce the overall performance of SORMAS.

### Analyze Performance Logs

Performance logs can be analyzed in detail using the `PerformanceLogAnalysisGenerator`. To use this tool, set the `PerformanceLoggingInterceptor`'s log level
to `TRACE` as described above and reproduce the scenario you want to investigate on the server instance.

After this, process the debug log file (default path: `/opt/domains/sormas/logs/application.debug`) using the `PerformanceLogAnalysisGenerator`. This will
generate three files (`<logfileName>.csv`, `<logfileName>.txt`, `<logfileName>.html`) to further investigate method runtimes.

`<logfileName>.html` provides a navigable overview of methods along with runtime statistics (total, min, max and average time) and calls to sub methods.

### Log Slow SQL Queries in PostgreSQL

You can enable the logging of slow SQL queries in your PostgreSQL server in `postgresql.conf`:

1. Change the value of `log_min_duration_statement` to a value that fits your need (e.g. 10000).

2. Restart the PostgreSQL service or reload the config.

### Run analysis of a SQL Query (SORMAS-Docker)

You can provide an analysis of a slow running query to help the developers to see where the query is getting slow and how to fix it.

1. **SORMAS-Docker** already logs slow SQL queries by default. You can view the log output on its host VM with `docker logs sormas-docker_postgres_1`.

2. Copy the SQL statement, replace all parameters (`$x`) with the values (see the following log statement) and place the SQL query on the system (outside Docker container on host):
```bash
sudo bash
cd /var/lib/docker/psqldata
vi explain.sql
 
# hit i (INSERT)
# Paste this into the file:  EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) 
# Paste the complete SQL statement
# Hit ESC and :wq to save the file
```

3. Execute the SQL (inside Docker container):
```bash
sudo bash
docker exec -ti sormas-docker_postgres_1 bash
cd /var/lib/postgresql/data
su postgres
psql -XqAt -d sormas -f explain.sql > analyze.json
```

4. Copy the output to your home dir on the VM (not inside the Docker container) to be able to copy it from the VM to your local system: `mv analyze.json /home/user.name/`

5. Create a visual report at <https://explain.dalibo.com/> in order to share the analysis.

## IDE Troubleshooting: Android Studio

If for some reason the Android App is not building correctly (for example due to unexpected `ClassNotFoundExceptions`), here is what you should try:
- Clean the Project (Build -> Clean Project)
- Invalidate Caches (File -> Invalidate Caches / Restart...)
- Wipe your Android VM (AVD Manager -> Wipe Data)

If you get this exception: `Unable to load class 'javax.xml.bind.JAXBException'`, the reason is most likely a faulty JDK version. For the androidapp, you need Java JDK 8. To change the JDK, go to File -> Project Structure -> JDK Location and select a valid JDK (on Linux, check the folder `/usr/lib/jvm` and/or install if necessary: `sudo apt install openjdk-8-jdk`)

## IDE Troubleshooting: eclipse

### Deployment Problems

Unfortunately, when using eclipse together with the Payara Tools, there are a number of deployment problems that you might run into. Examples of these include:

* ClassDefNotFoundExceptions after deploying the artifacts and logging in to the web app
* Error messages in eclipse telling you that the deployment failed

There are a couple of things you can do to fix these problems:

* Do a Maven update for all projects
* Stop and restart the server
* Re-deploy the server artifacts

If the problem occurred right after you've pulled new code from GitHub, your safest bet is probably to start with the Maven update. For most other problems, a simple re-deployment or, if necessary, server restart should suffice.

### News Feeds Polling

When running eclipse with JDK 11, you might encounter the following error message: `An internal error occurred during: "Polling news feeds".  javax/xml/bind/JAXBContext`. To fix it, disable `Window --> Preferences --> General --> News --> "Enable automatic news polling"`.

## Redeployment problems

If you face problems that `sormas-ui` or `sormas-rest` cannot call the backend anymore after redeploying, please follow [this instruction](DEVELOPMENT_ENVIRONMENT.md#avoid-redeployment-problems).

## Malware detection triggers
It might happen that a defensive program on your system falsely recognizes files needed to run SORMAS as vulnerability.

Please ignore the following known findings (no quarantine, no deletion):
* File: payara-5.2021.10.zip, Recognized: Trojan:Script/Oneeva.A!ml (found by Windows Defender). Has rarely happened when running server-setup.sh which downloads that file. The script subsequently fails because zip file cannot be extracted.
* File: glassfish/modules/war-util.jar, Recognized: Exploit:Java/CVE-2012-0507.D!ldr (found by Windows Defender in payara-5.2021.10). The deployed OSGi bundle might also be recognized, for example under this path: osgi-cache/felix/bundle365/version0.0/bundle.jar . If the file is quarantined, the paraya domain fails to start, without any exception in the log.
