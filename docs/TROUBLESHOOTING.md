
# Troubleshooting

Please consult this collection of solutions to common problems if you have any issues before issuing a support request or asking developers for help. Also note that this resource has only been added recently and will be extended in the future. If you have encountered (and fixed) any issue that you think would be worth adding to this list, please don't hesitate to let us know!

- [Android Application FAQ](#android-application-faq)
- [Identify Performance Problems in Production](#identify-performance-problems-in-production)
  - [Log Slow Database Queries](#log-slow-database-queries)
  - [Create Analysis of Slow Database Query (SORMAS-Docker)](#create-analysis-of-slow-database-query-sormas-docker)
  - [Log Slow Java Code](#log-slow-java-code)
- [Analyze Performance Problems](#analyze-performance-problems)
  - [Analyze a Slow Query](#analyze-a-slow-query)
  - [Analyze Java Code Performance Logs](#analyze-java-code-performance-logs)
- [IDE Troubleshooting: Android Studio](#ide-troubleshooting-android-studio)
- [IDE Troubleshooting: eclipse](#ide-troubleshooting-eclipse)
  - [Deployment Problems](#deployment-problems)
  - [News Feeds Polling](#news-feeds-polling)
- [Redeployment problems](#redeployment-problems)
- [Malware detection triggers](#malware-detection-triggers)

## Android Application FAQ

**Q:** I don't see a logout option anywhere in the mobile app. How can I change my user?  
**A:** The logout option is hidden by default because users in the field often don't know their own passwords, but their devices are instead set up by a supervisor. If you want to change your user, go to the Settings screen and tap the version number five times to bring up additional options, including the logout option.

**Q:** The app crashes. How can I get a log file?  
**A:** If you are using a release version of the app and need to get error logs, you can do the following:  

1. [Enable developer options in the Android device's settings](https://developer.android.com/studio/debug/dev-options)
2. Use the "Take Bug Report" option. The full report is not needed.
3. The zip file that is created will have a dumpstate-<current date>.txt file that contains the log and some more information
4. Open it and search for de.symeda.sormas to identify the process id. E.g. `de.symeda.sormas.app/de.symeda.sormas.app.login.LoginActivity$_11109#0` -> 11109 is the id
5. Search for all occurences of the process id to filter the file down to lines that contain the actual log of sormas

## Identify Performance Problems in Production

There are two main sources of bad performance in the application: 

1. Slow database queries
2. Slow Java code

### Log Slow Database Queries

Possibly the most generally useful log setting for troubleshooting performance, especially on a production server.

**SORMAS-Docker** already logs slow SQL queries by default. You can view the log output on its host VM with `docker logs sormas-docker_postgres_1`.

You can enable the logging of slow SQL queries in your PostgreSQL server in `postgresql.conf`:

1. Change the value of `log_min_duration_statement` to a value that fits your need (e.g. 10000).
2. Restart the PostgreSQL service or reload the config.
3. Monitor the log file.

### Create Analysis of Slow Database Query (SORMAS-Docker)

You can provide an analysis of a slow running query to help the developers to see where the query is getting slow and how to fix it.

1. Extract the slow SQL statement from slow query log.

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

5. Create a visual report at <https://explain.dalibo.com/> in order to share the analysis. Make sure the raw query and timings are included.

How to analyze this is explained below.

### Log Slow Java Code

If a specific view or action is facing bad performance it may be helpful to log the execution time of the responsible Java code.

1. Open the logback file located in your domain (default path: `/opt/domains/sormas/config/logback.xml`) and change the log level of `PerformanceLoggingInterceptor` to `DEBUG` or `TRACE`. The config change will be recognized during runtime within 30s. After that you will see detailed log entries in the SORMAS log.
2. Reproduce the scenario you want to investigate on the server instance.
3. Set the log level back to its default once the logging has been done since it can reduce the overall performance of SORMAS.

**Caution: Do not expose any private data!** Whenever you debug problems on an instance with productive data, please make sure that the logged information does not contain any personal data like real person names, birth dates, etc. to the public. Never provide such data anywhere on GitHub or any other online tool!

How to analyze the log is explained below.

## Analyze Performance Problems

### Analyze a Slow Query

To get your hands on a specific query executed by a view or action, you can do the following:

1. Get an [analysis from a production system](#create-analysis-of-slow-database-query-sormas-docker)
2. Create your own:
   1. Identify or create a unit test that calls the related backend method
   2. Set the 'hibernate.show_sql' config in the persistence.xml of the test resources to true
   3. Run the unit test and extract the query from the log
   4. Replace any parameters in the query
   5. Execute the query on your local database or a test system pre-faced by 'EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON)'
   6. Create a visual report at <https://explain.dalibo.com/> in order to share the analysis. Make sure the raw query and timings are included.

Important points when **interpreting the visual report**:

* Look for the **red highlights** in the report, especially for nodes that have a long runtime
* Look for nodes that are processing a **lot of rows** (100+ millions). This is often the case for joins
* When you are dealing with a lof of data, most often you only need to output a subset 

**How to improve the query** is most often a process of trial and error. Here are some things to consider:

* Adding indices so postgres does not have to go through all data. You can use a btree index to include multiple columns and sorting
* Getting rid of unnecessary joins. Example: Joining a region of a case to compare it to the region of the user, instead of directly doing this on the region_id field of the case
* Using limit to reduce the output (e.g. the first 100). Make sure there is no sorting done close to the end of the query graph. This is often the case when 'DISTINCT' is used.
  Example: https://github.com/hzi-braunschweig/SORMAS-Project/issues/9054#issuecomment-1420849461
* Splitting the query into separate queries when 'DISTINCT' has to be used / using distinct on a sub-query
* Using sub-queries to influence the query planner. 
  Example: https://github.com/hzi-braunschweig/SORMAS-Project/issues/11465#issuecomment-1425789509

**How to save time** when optimizing the query:

* Make sure you have easy access to a database that allows you to reproduce the bad performance of the query and to **manipulate the query and re-run it directly on the database**.
* Use the explain feature of pgAdmin to quickly output the query graph for debugging purposes
* Make sure you have a unit test (see above) that allows you to create the SQL query from a criteria query without having to re-deploy your server

### Analyze Java Code Performance Logs

After [Logging Slow Java Code](#log-slow-java-code) the debug log file (default path: `/opt/domains/sormas/logs/application.debug`) can be analyzed in detail using the `PerformanceLogAnalysisGenerator`.

The log file's path is specified as the program argument when calling `PerformanceLogAnalysisGenerator`'s `main` method. Processing the log file will
generate three files (`<logfileName>.csv`, `<logfileName>.txt`, `<logfileName>.html`) to further investigate method runtimes.

`<logfileName>.html` provides a navigable overview of methods along with runtime statistics (total, min, max and average time) and calls to sub methods.

Sometimes it is convenient to analyze a number of different scenarios in a row. To do so, produce snippets of the `application.debug` log using `tail` for each
of the scenarios to be investigated:

1. start `tail -f <logfileName> > <snippetDirectory>/<snippet.debug>`
2. replay the steps to be analyzed
3. stop `tail -f`

The `PerformanceLogAnalysisGenerator` can now batch process all of the snippets by pointing to the directory instead of a log file.
Calling `PerformanceLogAnalysisGenerator.main` with argument `<snippetDirectory>` generates the analysis files (`.csv`, `.txt`, `.html`)
for each file `*.debug` in this directory. The generated files will be placed in `<snippetDirectory>`, too.

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
