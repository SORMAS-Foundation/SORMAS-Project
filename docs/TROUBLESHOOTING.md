# Troubleshooting

Please consult this collection of solutions to common problems if you have any issues before issuing a support request or asking developers for help. Also note that this resource has only been added recently and will be extended in the future. If you have encountered (and fixed) any issue that you think would be worth adding to this list, please don't hesitate to let us know!

## Android Application FAQ

**Q:** I don't see a logout option anywhere in the mobile app. How can I change my user?  
**A:** The logout option is hidden by default because users in the field often don't know their own passwords, but their devices are instead set up by a supervisor. If you want to change your user, go to the Settings screen and tap the version number five times to bring up additional options, including the logout option.

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
