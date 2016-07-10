# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/alimehrpour/Development/Android/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
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
-keepattributes SourceFile,LineNumberTable

-keep class com.mixpanel.android.* { *; }
-dontwarn com.mixpanel.android.**

-keep class com.parse.* { *; }
-dontwarn com.parse.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepattributes Annotation,SourceFile,LineNumberTable
-keepattributes Signature

-keep interface com.parse.** { *; }
-keep class com.parse.** { *; }
-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }