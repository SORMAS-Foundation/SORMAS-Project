sormas
===============
Project Structure

The project consists of the following modules:

    sormas-api: general business logic and definitions for data exchange between app and server
    sormas-app: the android app
    sormas-backend: server entity services, facades, etc.
    sormas-base: base project that also contains build scripts
    sormas-ear: the ear needed to build the application
    sormas-rest: the rest interface
    sormas-ui: the web application

Getting Started
SORMAS Server & Domain

    Download payara 4.1.1.161 (http://bit.ly/2aY0aEX - possible issues with more recent versions) and extract it to a folder on your hard drive (e.g. /srv/payara-sormas)
    Create a PostgreSQL database named "sormas_db" (password: "sormas_db") with user "sormas_user" (password: "sormas_db") as its owner, and run the SQL scripts contained in "/sormas-base/sql/sormas_schema.sql"
    Get the latest SORMAS build from github: https://github.com/hzi-braunschweig/SORMAS-Open/releases/ (deploy.zip and app-debug.apk)
    Open "deploy/glassfish-config.bat" (or glassfish-config.sh on linux) in a text editor and change GLASSFISH_HOME to the location of the Glassfish folder inside your payara installation
    Set up a payara domain called "sormas" by executing glassfish-config.bat from the command line

Updating the SORMAS domain

(ignore this when you are setting up a whole development enviroment, as described below)

    Get the latest SORMAS build from github: https://github.com/hzi-braunschweig/SORMAS-Open/releases/ (deploy.zip and app-debug.apk)
    locate the server domain directory (domain-dir). Mostly /srv/payara-sormas/glassfish/domains/sormas
    make sure the server is not running
    delete the content from the following subfolders: domain-dir/autodeploy/bundles/... domain-dir/osgi-cache/felix
    copy the files from deploy/bundles to domain-dir/autodeploy/bundles
    update database: open sormas_schema.sql and execute ONLY the new part (compare based on table schema_version)
    start server: /srv/payara-sormas/glassfish/bin/startserv.bat
    copy sormas-ear.ear, sormas-rest.war, sormas-ui.war to domain-dir\autodeploy after some seconds the server should be updated (wait until nothing happens in the log)
    try to login at https://localhost:6081/sormas-ui if it doesn't work: restart the server
    update the mobile app with the new apk file

Development Environment

    Install the latest Eclipse version, Git for Windows and (optional) a Git client such as TortoiseGit if you don't want to handle version control from the command line/separately for the Eclipse and Android Studio projects
    Open the Git Bash and execute the command git config --global branch.development.rebase true (which ensures that rebase is used when pulling rather than merge)
    Clone the SORMAS-Open repository and import the projects to Eclipse
        If you're using Eclipse to clone, choose "File -> Import -> Git -> Projects from Git" and continue until you're asked to create a new project from the cloned repository; click cancel instead and use "File -> Import -> Maven -> Existing Maven Projects" to import the separate projects into your workspace
        If you've cloned the repository from the command line or a Git client, you obviously only need to perform the last step
    Highlight all Eclipse projects and choose "Maven -> Update Project" from the right click menu; perform the update for all projects
    Install Glassfish Tools and (recommended) the Vaadin Plugin for Eclipise (make sure to untick the option to also install the commercial UI designer)
    Set up a Glassfish 4 server in Eclipse and enter the credentials you specified when setting up the server
    Make a copy of "build.properties.example" contained in "sormas-base", rename it to "build.properties" and set "glassfish.domain.root" to the location of the sormas domain located in the "glassfish/domains" folder inside your payara installation
    Install the latest Android Studio version (to avoid any errors, make sure to start the installation with admin rights and choose a path for the Android SDK that contains no whitespaces)
    Open Android Studio and import the "sormas-app" project from Eclipse
    Drag the "build.xml" file contained in "sormas-base" into the Ant view in Eclipse and execute the "install [default]", "deploy-serverlibs" and "deploy-bundles" scripts
    Build the Android Studio project by executing the gradle build (this may be done automatically)
    Start the Glassfish server and deploy "sormas-ear", "sormas-rest" and "sormas-ui" by dragging the respective projects onto it
    Open your browser and type in "https://localhost:6081/sormas-ui" to test whether everything has been set up correctly (and to use the application)
