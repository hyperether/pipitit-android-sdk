# pipitit-android-sdk

**Android Sdk**

**Gradle import**

    compile ('com.hyperether:pipitit-android:1.0.8aar'){
        exclude group: 'com.google.firebase'
        exclude group: 'com.android.volley'
        exclude group: 'org.codehaus.jackson'
        transitive = true
    }

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


            