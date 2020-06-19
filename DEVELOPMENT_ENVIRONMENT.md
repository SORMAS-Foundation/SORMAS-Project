
# SORMAS Development Environment

## Server
- Install [your local server](SERVER_SETUP.md) or [a more dev specific one](SERVER_DEV_SETUP.md) (Docker is needed to run Postgresql image)

## Git
- Install [Git for your OS](https://git-scm.com/downloads)
- Recommended: Install a Git client such as [TortoiseGit](https://tortoisegit.org/) if you don't want to handle version control from the command line or separately for the Eclipse and Android Studio projects
- Open the Git Bash and execute the command <code>git config --global branch.development.rebase true</code> (which ensures that rebase is used when pulling rather than merge)

## Eclipse
- Install the latest Eclipse version
- Set the default JRE of Eclipse to the installed Zulu Java SDK: [Assigning the default JRE for the workbench ](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-assign_default_jre.htm)
- Clone the SORMAS-Open repository and import the projects to Eclipse
	- If you're using Eclipse to clone, choose "File -> Import -> Git -> Projects from Git" and continue until you're asked to create a new project from the cloned repository; click cancel instead and use "File -> Import -> Maven -> Existing Maven Projects" to import the separate projects into your workspace
	- If you've cloned the repository from the command line or a Git client, you obviously only need to perform the last step
- Install [Payara Tools](https://marketplace.eclipse.org/content/payara-tools)
- Install the [Vaadin Plugin for Eclipse](https://marketplace.eclipse.org/content/vaadin-plugin-eclipse) (no need to install the commercial UI designer)
- Add a Payara server to Eclipse and enter the credentials you specified when setting up the server
- Configure automatic code formatting ("Window -> Preferences"):
    - Go to "Java -> Code Style -> Formatter", import ``sormas-base/java-formatter-profile.xml`` and apply.
    - Go to "Java -> Code Style -> Organize Imports", import ``sormas-base/java-importorder-profile.importorder``, "Number of imports needed for .*" = ``99``, "Number of static imports needed for .*" = ``99``, "Do not create import for types starting with a lowercase letter" = ``checked`` and apply.
    - Go to "Java -> Editor -> Save Actions", activate "Perform the selected actions on save", "Format source code" with "Format all lines", "Organize imports" and apply.

### Additional Steps
- Make a copy of "build.properties.example" contained in "sormas-base", rename it to "build.properties" and set "glassfish.domain.root" to the location of the sormas domain located in the "glassfish/domains" folder inside your payara installation
- Drag the "build.xml" file contained in "sormas-base" into the Ant view in Eclipse
  - Either: Run "Maven install" on the sormas-base project
  - Or: Execute the "install [default]" ant script (this needs a maven installation on your system with the M2_HOME variable set)
  - Then: Execute the "deploy-serverlibs" ant script
- Highlight all Eclipse projects and choose "Maven -> Update Project" from the right click menu; perform the update for all projects
- Start the Glassfish server and deploy "sormas-ear", "sormas-rest" and "sormas-ui" by dragging the respective projects onto it, or use the "Add and Remove..."-function by right clicking on the server.
- Open your browser and type in "http://localhost:6080/sormas-ui" or "https://localhost:6081/sormas-ui" to test whether everything has been set up correctly (and to use the application)

## IntelliJ
- Install the latest Ultimate edition IntelliJ
- Set the project SDK to use the installed Zulu Java SDK
- Clone the SORMAS-Project repository and open the project in IntelliJ
	- make sure the under "File -> Project Structure -> Modules" all the modules (except the android app - this should not be added) are recognized, if not add the modules with +
- Make sure under "File -> Settings -> Plugins" Glassfish & Ant integrations are enabled (look into the "Installed" tab)
- Install the Vaadin Designer plugin
- Make a copy of "build.properties.example" contained in "sormas-base", rename it to "build.properties" and set "glassfish.domain.root" to the location of the sormas domain located in the "glassfish/domains" folder inside your payara installation
- Run "Maven install" on the sormas-base project
- Add a Payara server to IntelliJ:
	- go to "Run -> Edit configurations"
	- add new configuration and choose from the templates Glassfish server
	- select the payara5 directory for application server - and name the application server field Payara5
	- specify server domain and credentials from the server setup
	- add "http://localhost:6080/sormas-ui" under open browser section and check After launch checkbox
	- under Deployment tab add the artifacts "sormas-ear", "sormas-rest" and "sormas-ui"
	- under Logs tab add new log with location pointing to the domain log (e.g.: payara5\glassfish\domains\sormas\logs\server.log)
	- under Startup/Connection tab make sure you do not pass environment variables (it's a currently open bug in intellij) - ignore warning about debug config not being correct
	- edit your domain config ..\payara5\glassfish\domains\sormas\config\domain.xml and make sure the java-config node contains:
	 ``<java-config classpath-suffix="" debug-enabled="true" debug-options="-agentlib:jdwp=transport=dt_socket,address=6009,server=n,suspend=y" ...``
- Configure code formatting:
	- install Eclipse Code Formatter for IntelliJ (https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter)
	- open the plugin settings (Other Settings -> Eclipse Code Formatter) and select "Use the Eclipse Code Formatter"
	- under "Eclipse Formatter config file", select ``sormas-base/java-formatter-profile.xml``
	- check optimize imports and, for "Iport order", select ``sormas-base/java-importorder-profile.importorder``
	- **Important:** select "Do not format other file types by IntelliJ formatter"
	- go to Preferences -> Editor -> Code style -> Java : set class and static names counts for import with * to 99
	- for IntelliJ, code formatting is usually done with Ctrl+Alt+L. For automatic formatting, it's recommended to use the plugin Save Actions (https://plugins.jetbrains.com/plugin/7642-save-actions)

## Android Studio
**Note: This is only needed for development of the SORMAS Android app
* Install the latest Android Studio version (to avoid errors, start the installation with admin rights)
* Start the application
* To avoid errors, ensure that the path for the Android SDK contains no whitespaces
	* The path could be edited at ``Tools -> SDK Manager -> Android SDK Location``
* Open Android Studio and import the "sormas-app" module from SORMAS-Project
* Create a keystore.properties file in sormas-app (see keystore.properties.example for reference - needed only for app deployment).
* Build the Android Studio project by executing the gradle build (this may be done automatically)
* Add an emulator with SDK version between the minSdkVersion and targetSdkVersion properties from build.gradle
* On first start of the application enter the Sormas rest service URL for the server URL: http://10.0.2.2:6080/sormas-rest/ (see: https://developer.android.com/studio/run/emulator-networking)
* Configure code formatting:
	- install Eclipse Code Formatter for Android studio (https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter)
	- open the plugin settings (Other Settings -> Eclipse Code Formatter) and select "Use the Eclipse Code Formatter"
	- under "Eclipse Formatter config file", select ``sormas-base/java-formatter-profile.xml``
	- check optimize imports and, for "Iport order", select ``sormas-base/java-importorder-profile.importorder``
	- **Important:** select "Do not format other file types by IntelliJ formatter"
	- go to Preferences -> Editor -> Code style -> Java : set class and static names counts for import with * to 99
	- for Android Studio, code formatting is usually done with Ctrl+Alt+L. For automatic formatting, it's recommended to use the plugin Save Actions (https://plugins.jetbrains.com/plugin/7642-save-actions)
