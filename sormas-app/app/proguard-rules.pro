# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\dev\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate

-keep public class de.symeda.sormas.**  { *; }
-keep public enum de.symeda.sormas.**  { *; }

# exclude LBDS libs - their usage via reflection isn't properly detected
-keep public class org.hzi.sormas.**  { *; }

-dontwarn javax.**
-dontwarn java.beans.**
-dontwarn java.awt.Image
-dontwarn kotlin.reflect.jvm.ReflectJvmMapping
-dontwarn org.apache.commons.io.input.BOMInputStream
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn reactor.blockhound.integration.BlockHoundIntegration
-dontwarn sun.security.x509.AlgorithmId