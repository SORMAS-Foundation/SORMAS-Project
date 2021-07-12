# Troubleshooting

Please consult this collection of solutions to common problems if you have any issues before issuing a support request or asking developers for help. Also note that this resource has only been added recently and will be extended in the future. If you have encountered (and fixed) any issue that you think would be worth adding to this list, please don't hesitate to let us know!

## Android Application FAQ

**Q:** I don't see a logout option anywhere in the mobile app. How can I change my user?  
**A:** The logout option is hidden by default because users in the field often don't know their own passwords, but their devices are instead set up by a supervisor. If you want to change your user, go to the Settings screen and tap the version number five times to bring up additional options, including the logout option.


## Debug performance problems

**Do not expose private data**: Whenever you debug problems on an instance with productive data, please check the logged information to not expose personal data like real person name, birth date etc. to the public. Never provide such data somewhere on Github or any other online tool!

### Switch on Performance Logging in SORMAS

To debug the method of a slow function, the performance logging can to be activated to find which method(s) are time consuming. This helps the developers to find problems quicker and if there are several problems at once or performance problems that manifest in Java execution time instead of slow SQL queries.

1. Change the log level of `PerformanceLoggingInterceptor` to `DEBUG` in Logback (default path: `/opt/domains/sormas/config/logback.xml`). The config change will be recognized during runtime within 30s. After that you will see detailed log entries in the SORMAS log.

2. Undo this logging change when you do not need it any more, since it can reduce the overall performance of SORMAS.

### Log slow SQL queries in PostgreSQL

Enable the logging of slow SQL queries in your PostgreSQL Server in the `postgresql.conf`.

1. Toggle the following parameter to a value that fits your nee, for example to: `log_min_duration_statement = 10000`

2. Remember to restart the PostgreSQL service or reload the config.

### Run analysis of a SQL query (with SORMAS-Docker)

Provide an analysis of a slow running query to help the developers to see where the query is getting slow and how to fix it.

1. **SORMAS-Docker** already logs slow SQL queries per default. See the log output on its host VM with: `docker logs sormas-docker_postgres_1`

2. Copy the SQL statement, replace all parameters (`$x`) with the values (see following log statement) and place the SQL query on the system (outside Docker container on host):
```bash
sudo bash
cd /var/lib/docker/psqldata
vi explain.sql
 
# hit i (INSERT)
# Paste this into the file:  EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) 
# Paste the complete SQL statement
# Hit ESC and :wq to save the file
```

3. Execute the SQL  (inside Docker container):
```bash
sudo bash
docker exec -ti sormas-docker_postgres_1 bash
cd /var/lib/postgresql/data
su postgres
psql -XqAt -d sormas -f explain.sql > analyze.json
```

4. Copy the output to your home dir to be able to copy it from the VM to your local system (on VM, not inside the docker container!): `mv analyze.json /home/user.name/`

5. Create a visual report here to be able to share the analysis: <https://explain.dalibo.com/>.


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
