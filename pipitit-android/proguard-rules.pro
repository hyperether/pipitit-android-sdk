# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Nebojsa\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class javax.** { *; }
-keep class org.** { *; }

-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-dontwarn android.support.v4.**

-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn android.support.v7.**

-keep class org.json.** { *; }
-keep interface org.json.** { *; }
-dontwarn org.json.**

-keep class org.codehaus.jackson.** { *; }
-keep interface org.codehaus.jackson.** { *; }
-dontwarn org.codehaus.jackson.**

-keep class com.hyperether.pipitit.**{ *; }
-keep interface com.hyperether.pipitit.** { *; }
-dontwarn com.hyperether.pipitit.**

# Gson
-keep interface com.google.gson.** { *; }
-keep class com.google.gson.** { *; }