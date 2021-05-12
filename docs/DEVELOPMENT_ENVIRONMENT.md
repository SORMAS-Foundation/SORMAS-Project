
# Development Environment Setup

This step-by-step guide explains how to set up your development environment, using either Eclipse or Intellij for the backend and web UI and Android Studio for the mobile app. Please follow it strictly to make sure that development will run as smoothly as possible and your code adheres to our guidelines.

**Please note that these instructions are optimized for Windows and Linux systems.** If you're developing on a Mac and, we would be glad to get your feedback about how this guide can be extended with OS-specific instructions in our developer chat on [Gitter](https://gitter.im/SORMAS-Project).

## Step 1: Install a Local SORMAS Server
Please follow the [Server Setup Instructions](SERVER_SETUP.md) to set up a local SORMAS instance that you will use to test your code. Alternatively, you can also use [Maven Cargo](../sormas-cargoserver/README.md), or a [Docker installation](SERVER_DEV_SETUP.md) (not recommended at this time).

## Step 2: Check Out the SORMAS Repository
- [Download and install the latest Git version](https://git-scm.com/downloads) for your operating system
- *Optional:* Install a Git client such as [TortoiseGit](https://tortoisegit.org/) or [GitHub Desktop](https://desktop.github.com/) if you don't want to handle version control from the command line or within your IDE
- *Optional:* Clone the SORMAS-Open repository with `git clone https://github.com/hzi-braunschweig/SORMAS-Project.git`; if you want to use Git from within your IDE, you can also clone the repository in Step 4
- Open Git Bash and execute the following command to ensure that rebase is used when pulling the development branch rather than merge: `git config --global branch.development.rebase true`

## Step 3: Install Java
Download and install the **Java 11 JDK** (not JRE) for your operating system. We suggest using [Zulu OpenJDK](https://www.azul.com/downloads/?version=java-11-lts&package=jdk). If you're running Linux, please refer to the [official documentation](https://docs.azul.com/zulu/zuludocs/ZuluUserGuide/PrepareZuluPlatform/AttachAPTRepositoryUbuntuOrDebianSys.htm) on how to install Zulu OpenJDK on your system.

## Step 4: Install and Configure Your IDE

### IntelliJ
- Download and install the latest [IntelliJ IDEA Ultimate](https://www.jetbrains.com/lp/intellij-frameworks/); (newer than version of 2020-04-15 to enable debugging, see https://youtrack.jetbrains.com/issue/IDEA-216528)
- Set the project SDK to the installed JDK
- *Optional:* Clone the SORMAS-Open repository if you haven't done so already
- Open the project in Intellij; make sure that under `File -> Project Structure -> Modules` all modules EXCEPT sormas-app are recognized; if not, add the missing modules with the `+` button
- Navigate to `File -> Settings -> Plugins` and make sure that Glassfish & Ant integrations are enabled
- Install the [Vaadin 6-8 plugin](https://plugins.jetbrains.com/plugin/13199-vaadin-6-8); the commercial Vaadin Designer is not needed
- Make a copy of `sormas-base/build.properties.example`, rename it to `build.properties` and set `glassfish.domain.root` to the location of the SORMAS domain inside your Payara installation
- Run `mvn install` on the `sormas-base` project (e.g. by opening the Maven view and executing `sormas-base -> Lifecycle -> install`)
- Add a Payara server to IntelliJ:
  - Open `Run -> Edit Configurations`, add a new configuration and choose the Glassfish server template
  - Click on `Configure` next to `Application server` and create a new server configuration by selecting your Payara installation directory
  - Check the `After launch` checkbox and specify the browser that you want SORMAS to open in once the server has been deployed
  - Enter `http://localhost:6080/sormas-ui` into the `URL` field
  - Make sure that the correct JRE is specified (your Java 11 JDK)
  - Enter the path to the SORMAS domain and the credentials that you've specified when setting up the server
  - Open the `Deployment` tab and add the artifacts `sormas-ear`, `sormas-rest` and `sormas-ui`
  - Open the `Logs` tab and add a new log file pointing to the `logs/server.log` file in your SORMAS domain
  - Open the `Startup/Connection` tab and make sure that `Pass environment variables` is NOT checked; ignore warnings about the debug configuration not being correct
  - Open the `config/domain.xml` file in your domain directory and make sure that the `java-config` node contains the following code: `<java-config classpath-suffix="" debug-enabled="true" debug-options="-agentlib:jdwp=transport=dt_socket,address=6009,server=n,suspend=y" ...`
- Open the Ant window, click on the `+` icon and select the `sormas-base/build.xml` file
- Execute the `install` and `deploy-serverlibs` Ant scripts
- Set the default working directory for run configurations by navigating to `Run -> Edit Configurations -> Templates -> Application` and setting `Working directory` to `$MODULE_WORKING_DIR$`

### Eclipse
- Download and install the latest [Eclipse IDE for Enterprise Java and Web Developers](https://www.eclipse.org/downloads/packages)
- Set the default JRE of Eclipse to the installed JDK: [Assigning the default JRE for the workbench](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-assign_default_jre.htm)
- *Optional:* Clone the SORMAS-Open repository if you haven't done so already via `File -> Import -> Git -> Projects from Git` and cancel the process when you're asked to create a new project from the cloned repository
- Import the projects from the SORMAS-Open repository into your workspace via `File -> Import -> Maven -> Existing Maven Projects`
- Install the [Payara Tools plugin](https://marketplace.eclipse.org/content/payara-tools)
- Install the [Vaadin Plugin for Eclipse](https://marketplace.eclipse.org/content/vaadin-plugin-eclipse); the commercial UI designer is not needed
- Add a Payara server to Eclipse and enter the credentials you specified when setting up the local SORMAS server
- Make a copy of `sormas-base/build.properties.example`, rename it to `build.properties` and set `glassfish.domain.root` to the location of the SORMAS domain inside your Payara installation
- Drag the `sormas-base/build.xml` file into the Ant view in Eclipse
- Either run `mvn install` on the `sormas-base` project or execute the `install [default]` Ant script (this needs a Maven installation on your system with the M2_HOME variable set)
- Execute the `deploy-serverlibs` Ant script
- Highlight all Eclipse projects and choose `Maven -> Update Project` from the right-click menu; perform the update for all projects
- Start the Glassfish server and deploy `sormas-ear`, `sormas-rest` and `sormas-ui` by dragging the respective projects onto it, or use the `Add and Remove...` function by right-clicking on the server
- Open your browser and type in `http://localhost:6080/sormas-ui` to test whether the server and IDE have been set up correctly

### Android Studio
**Please note: You only need to install Android Studio if you're developing code for the Android app. This is likely the case when you're adding new fields or entities to the system, or if you specifically want to work on the mobile app.**

- Download and install the latest [Android Studio version](https://developer.android.com/studio)
  - Please make sure to run the installer with admin rights if you're using Windows
  - Ensure that the Android SDK installation path does not contain whitespaces; you can also change this later via `Tools -> SDK Manager -> Android SDK Location`
- Open Android Studio and import the `sormas-app` module from the SORMAS-Open repository
- Make a copy of `keystore.properties.example` and rename it to `keystore.properties`
- Build the Android Studio project by executing the Gradle build (this may be done automatically)
- Add an emulator and set the SDK version to the `minSdkVersion` or `targetSdkVersion` from `build.gradle`; we suggest to test your code on both, but `minSdkVersion` should be preferred to ensure compatibility to the minimum supported SDK
- Click on `Run 'app'` to install and run the app on your emulator; enter `http://10.0.2.2:6080/sormas-rest` as the server URL when you start the newly installed app for the first time

**Important:** Whenever you do or pull changes in the `sormas-api` project that you want to use in the mobile app or that are referenced there already, you need to execute the `install` Ant script to notify the `sormas-app` project of the changes.

## Step 5: Configure Code Formatting and Import Settings
In order to ensure a consistent code style and prevent so-called edit wars, we have set up custom configuration files for automatic code formatting and import ordering. Please make sure to adhere to the following steps for your IDE(s) before you start developing.

### IntelliJ and Android Studio
- Install the [Eclipse Code Formatter for Intellij/Android Studio plugin](https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter)
- Open the plugin settings via `File -> Settings -> Other Settings -> Eclipse Code Formatter` and select `Use the Eclipse Code Formatter`
- Under `Eclipse formatter config`, choose `Eclipse workspace/project folder or config file` and select `sormas-base/java-formatter-profile.xml`
- Check `Optimize Imports`
- Under `Import order`, choose `From file` and select `sormas-base/java-importorder-profile.importorder`
- Make sure that `Do not format other file types by IntelliJ formatter` is selected
- Go to `Editor -> Code Style -> Java -> Imports` and set `Class count to use import with '*'` and `Names count to use static import with '*'` to 99
- Navigate to `Editor -> General -> Auto Import` and disable `Optimize imports on the fly`

Optional, but strongly recommended:
- Install the [Save Actions plugin](https://plugins.jetbrains.com/plugin/7642-save-actions) that automatically applies code formatting and import reordering whenever you save a file - otherwise you will manually have to do so (by default with Ctrl+Alt+L)
- Open the plugin settings via `File -> Settings -> Other Settings -> Save Actions` and make sure that the *first three checkboxes* under `General` and the *first two checkboxes* under `Formatting Actions` are selected

### Eclipse
- Open `Window -> Preferences`
- Navigate to `Java -> Code Style -> Formatter`, import `sormas-base/java-formatter-profile.xml` and apply the changes
- Navigate to `Java -> Code Style -> Organize Imports` and import `sormas-base/java-importorder-profile.importorder`
- On the same screen, set `Number of imports needed for .*` and `Number of static imports needed for .*` to 99
- On the same screen, make sure that `Do not create import for types starting with a lowercase letter` is checked and apply the changes
- Navigate to `Java -> Editor -> Save Actions` and make sure that the following options are selected: `Perform the selected actions on save`, `Format source code`, `Format all lines` and `Organize imports`

## Issues which can appear during installation process of the project

1. If debug mode does not work: To replace opt\payara5\glassfish\modules\launcher.jar with sormas-base/setup/launcher.jar

2. For Windows: Please check your java_version. In case if you have the multiple java_versions installed on the system, it will always show to you the first version installed.
   I had the java 8 instead of 11.
   In order to fix it, go to environment variables, and move the 11 version up. And rerun the script. Seems that the console is reading those variables at the starting point, and the values of it can be updated only after console/script restart.

3. For Windows: Pay attention to the postgres SQL files rights permissions after unziping the downloaded ZIP archive. Files physically were present but next script error has been generated:
   psql:setup.sql:7: ERROR:  could not open extension control file "C:/Program Files/PostgreSQL/10/share/extension/temporal_tables.control": No such file or directory
   -I checked the file rights, and under windows they has AV attribute, however, all others has only A attribute. When I was trying to open them with Notepad++ it was saying that such file does not exist. Do you want to create it? If `yes` will be pressed - another message saying that the file exists, appeared. Very strange scenario...

4. All the postgres commands (of added users, etc.) which were added at first startup of the application - will raise errors in case if such entity exists. Just ignore those errors at repeated installation of .\server-setup.sh

5. Check always the port number 6048 which can be occupied by an old instance of payara.
   -> For every installation, kill all Java/javaw processes and check the availability of 6048 port number.
   -> Delete files with generated domain folders and payara. In order to have a clean installation of each next ./server-setup.sh run.

6. M2_HOME need to be set. By default, for newer version, it is set to MAVEN_HOME. But Ant script is looking for M2_HOME

7. For eclipse formatted plugin, there is an issue for Idea: https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter - `cannot save settings Path to custom eclipse folder is not valid` - it works only when settings were saved from down to up. And not vice versa.
