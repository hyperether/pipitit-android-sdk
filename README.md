# pipitit-android-sdk

![Pipitit Logo](https://github.com/hyperether/pipitit-android-sdk/blob/master/Pipitit_Logo_450x100.png)

**Android Sdk**

**Gradle import**

    compile ('com.hyperether:pipitit-android:1.1.0aar'){
        exclude group: 'com.google.firebase'
        exclude group: 'com.android.volley'
        exclude group: 'org.codehaus.jackson'
        transitive = true
    }
    
    If you do not have FCM implement in project and you want to use from pipitit sdk you need to add:
    Add rules to your root-level build.gradle file, to include the google-services plugin:
    dependencies {
        classpath 'com.google.gms:google-services:3.0.0'
        // ...
    }
    Then, in your module Gradle file (usually the app/build.gradle), 
    add the apply plugin line at the bottom of the file to enable the Gradle plugin:
    apply plugin: 'com.google.gms.google-services'

**Usage**

        PipititConfig config = new PipititConfig.Builder()
                .setDebug(true|false)
                .setSendingPushEnabled(true|false)
                .setListener(TODO: put listener Activity)
                .setURL(TODO: put service URL)
                .setWebSocketEnabled(true|false)
                .setNotificationWakeUp(true|false)
                .build(context);
        PipititManager.init(getApplication(), config);

**User login**

        PipititManager.getInstance().setUsername(username);
        PipititManager.getInstance().setEmail(email);
            
**User logout**

        PipititManager.clear(getApplication(), true);
            
**Resources setup**

        <string name="com_hyperether_pipitit_server_push" tools:override="true">TODO: ADD</string>
        <string name="pipitit_api_key" tools:override="true">TODO: ADD</string>
            
**Version Support**
            
The Android SDK supports Android 4.1+.


            