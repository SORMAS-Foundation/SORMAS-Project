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
-dontwarn com.fasterxml.jackson.databind.JsonNode
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn java.awt.Image
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn javax.ejb.ApplicationException
-dontwarn javax.ejb.Remote
-dontwarn javax.naming.ConfigurationException
-dontwarn javax.naming.InitialContext
-dontwarn javax.naming.NamingException
-dontwarn javax.validation.ConstraintViolation
-dontwarn javax.validation.ElementKind
-dontwarn javax.validation.Path$Node
-dontwarn javax.validation.Path
-dontwarn javax.validation.Valid
-dontwarn javax.validation.ValidationException
-dontwarn javax.validation.constraints.Max
-dontwarn javax.validation.constraints.Min
-dontwarn javax.validation.constraints.NotNull
-dontwarn javax.validation.constraints.Pattern
-dontwarn javax.validation.constraints.Size
-dontwarn javax.validation.metadata.ConstraintDescriptor
-dontwarn org.apache.commons.io.input.BOMInputStream
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString
-dontwarn sun.security.x509.AlgorithmId

-keep public class de.symeda.sormas.** {
  *;
}