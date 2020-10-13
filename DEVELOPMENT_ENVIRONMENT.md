
# SORMAS Development Environment

## Server
- Use this guide as Dev-Setup **or** refer to [SERVER_SETUP](SERVER_SETUP.md) for a more production-like setup (recommended for core team developers).

## Basics

### Git
- Install [Git for your OS](https://git-scm.com/downloads)
- Recommended: Install a Git client such as [TortoiseGit](https://tortoisegit.org/) if you don't want to handle version control from the command line or separately for the Eclipse and Android Studio projects
- Open the Git Bash and execute the command `git config --global branch.development.rebase true` (which ensures that rebase is used when pulling rather than merge)
- On Windows it's strongly recommended to set line endings to auto: `git config --global core.autocrlf true`

### Java
- Download and install the Java 11 **JDK** (not JRE) for your operating system. We suggest to use Zulu OpenJDK: https://www.azul.com/downloads/zulu/
  * **Linux**: https://docs.azul.com/zulu/zuludocs/#ZuluUserGuide/PrepareZuluPlatform/AttachAPTRepositoryUbuntuOrDebianSys.htm
        
		sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9
		sudo apt-add-repository 'deb https://repos.azul.com/zulu/deb/ stable main'
		sudo apt-get update
		sudo apt-get install zulu11
  - **Windows**: For testing and development environments we suggest downloading and running the installer of the Java 11 **JDK** for 32 or 64 bit client systems (depending on your system).

###### SORMAS just recently moved to Java 11. We still need to support Java 8 for a transition period. Therefore, please just use Java 8 language features for now.

### Payara (Application Server)
- Instead of a locally installed maven, you can also use the maven wrapper: `./mvnw`
- Run "Maven install" on the sormas-base project: `(cd sormas-base && mvn install -DskipTests=true)`
- Install and configure Payara to set up the domain `(cd sormas-cargoserver && mvn cargo:configure)`
    - More details on this setup in [sormas-cargoserver](sormas-cargoserver/README.md)
    - on unix like systems (Linux/MacOS) you'll have to change permissions of the binaries: `chmod -R +x sormas-cargoserver/target/cargo/installs/payara-*/payara5/bin sormas-cargoserver/target/cargo/installs/payara-*/payara5/glassfish/bin`

# IDE

This is enough for CLI based development using `mvn` and friends.
Depending on the IDE of your choice, you can achieve further integration and improve the developer
experience.

 * [Eclipse](#Eclipse)
 * [Intellij](#IntelliJ)
        
## Eclipse
- Install the latest Eclipse version
- Set the default JRE of Eclipse to the installed Zulu Java SDK: [Assigning the default JRE for the workbench ](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-assign_default_jre.htm)
- Clone the SORMAS-Open repository and import the projects to Eclipse
	- If you're using Eclipse to clone, choose "File -> Import -> Git -> Projects from Git" and continue until you're asked to create a new project from the cloned repository; click cancel instead and use "File -> Import -> Maven -> Existing Maven Projects" to import the separate projects into your workspace
	- If you've cloned the repository from the command line or a Git client, you obviously only need to perform the last step
- Install [Payara Tools](https://marketplace.eclipse.org/content/payara-tools)
- Install the [Vaadin Plugin for Eclipse](https://marketplace.eclipse.org/content/vaadin-plugin-eclipse) (no need to install the commercial UI designer)
- Add a Payara server to Eclipse 
    - if you've chosen the setup based on [SERVER_SETUP](SERVER_SETUP.md), you'll have to adapt the following paths accordingly
    - In the **Servers**-Tab create a new server and select **Payara/Payara**
    - Add a **Server runtime environment** 
        - **Payara location** is `sormas-cargoserver/target/cargo/installs/payara-5.194/payara5`
        - **Java location** has to point to a **JDK 8** home path!
    - Set **Domain path** to `sormas-cargoserver/target/cargo/configurations/payara/sormas`
    - **Admin password** is `adminadmin` and go to the next page
    - Add the artifacts `sormas-ear`, `sormas-rest` and `sormas-ui` to **Configured** and **finish** the setup
    - In the Server's **Launch configuration** add the serverlibs to the classpath: `sormas-base/dependencies/target/sormas-serverlibs-serverlibsjar.jar`
- Configure automatic code formatting ("Window -> Preferences"):
    - Go to "Java -> Code Style -> Formatter", import ``sormas-base/java-formatter-profile.xml`` and apply.
    - Go to "Java -> Code Style -> Organize Imports", import ``sormas-base/java-importorder-profile.importorder``, "Number of imports needed for .*" = ``99``, "Number of static imports needed for .*" = ``99``, "Do not create import for types starting with a lowercase letter" = ``checked`` and apply.
    - Go to "Java -> Editor -> Save Actions", activate "Perform the selected actions on save", "Format source code" with "Format all lines", "Organize imports" and apply.

## IntelliJ
- Install the latest Ultimate edition IntelliJ
- Set the project SDK to use the installed Zulu Java 8 SDK
- Clone the SORMAS-Project repository and open the project in IntelliJ
	- make sure the under "File -> Project Structure -> Modules" all the modules (except the android app - this should not be added) are recognized, if not add the modules with +
- Make sure under "File -> Settings -> Plugins" Glassfish integration is enabled (look into the "Installed" tab)
- Install the Vaadin Designer plugin
- Add a Payara server to IntelliJ:
    - if you've chosen the setup based on [SERVER_SETUP](SERVER_SETUP.md), you'll have to adapt the following paths accordingly
	- go to "Run -> Edit configurations"
	- add **new configuration** and choose from the templates **"Glassfish Server local"**
	    - **Configure** the payara5 directory for application server from the cargo with **Glassfish Home**: `sormas-cargoserver/target/cargo/installs/payara-5.194/payara5` (find the current version in [pom.xml](sormas-cargoserver/pom.xml))
	    - add the serverlibs to the libraries section: `sormas-base/dependencies/target/sormas-serverlibs-serverlibsjar.jar`
	- add "http://localhost:6080/sormas-ui" under open browser section and check After launch checkbox
    - in **Glassfish Server Settings** set the **Server domain** to: `sormas-cargoserver/target/cargo/configurations/payara/sormas` 
    - specify **credentials**. Default are username: `admin` and password: `adminadmin`
	- under **Deployment tab** add the artifacts `sormas-ear`, `sormas-rest` and `sormas-ui`
	- under **Logs tab** add new log with location pointing to the domain log (e.g.: `sormas-cargoserver/target/cargo/configurations/payara/sormas/logs/server.log`)
	- the sormas server requires a running postgres database. Find more about the PG setup in [sormas-cargoserver](sormas-cargoserver/README.md)
- **Configure code formatting:**
	- disable "Optimize imports on the fly" (Editor -> General -> Auto Import)
	- install Eclipse Code Formatter for IntelliJ (https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter)
	- open the plugin settings (Other Settings -> Eclipse Code Formatter) and select "Use the Eclipse Code Formatter"
	- under "Eclipse Formatter config file", select ``sormas-base/java-formatter-profile.xml``
	- check optimize imports and, for "Import order", select ``sormas-base/java-importorder-profile.importorder``
	- **Important:** select "Do not format other file types by IntelliJ formatter"
	- go to Preferences -> Editor -> Code style -> Java -> Imports: set class and static names counts for import with * to 99
	- for IntelliJ, code formatting is usually done with Ctrl+Alt+L. For automatic formatting, it's recommended to use the plugin Save Actions and check the first three checkboxes in "General" and the first two checkboxes in "Formatting Actions" (https://plugins.jetbrains.com/plugin/7642-save-actions)

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
