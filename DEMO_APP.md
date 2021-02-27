# Creating a demo app for a SORMAS demo server

**Important**: This only applies if you have [setup your own SORMAS server](SERVER_SETUP.md) for **demo** purposes and and want to give users easy access to it.

## Step 1: Adjust the sormas-app.properties
1. Open the the apk file from the SORMAS release with a zip editor (e.g. 7zip).
2. Extract sormas-app.properties and open the the file for editing.
3. Set server.url.default to the URL of your SORMAS server's ReST interface.
4. Set user.name.default and user.password.default to the demo user (needs to be an informant or officer).
5. Overwrite the sormas-app.properties in the apk with your changed version.

## Step 2: Sign the changed apk file
Since the apk file has been changed it needs to be signed again.\\
**Important**: When you change and sign the apk file it is no longer compatible with the original apk file for automatic app update! If you still want to make this work you always have to sign new versions using the same keystore and put the changed apk-file into your SORMAS server for automatic app update.\\

1. Create a keystore using keytool: <code>keytool -genkey -v -keystore my-demo-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias</code>
   > Note: keytool is located in the bin/ directory in your JDK. To locate your JDK from Android Studio, select File > Project Structure, and then click SDK Location and you will see the JDK location.
2. Download uber-apk-signer: <https://github.com/patrickfav/uber-apk-signer/releases.>
   > Note: this is the convenient way to do it. You can also get an Android SDK and follow the instructions given [here](https://developer.android.com/studio/publish/app-signing#signing-manually)
3. Sign the apk file: <code>java -jar uber-apk-signer.jar --ks my-demo-key.jks -ksAlias my-alias --allowResign --apks sormas-version-demo.apk</code>
   > See also: <https://github.com/patrickfav/uber-apk-signer#command-line-interface>
