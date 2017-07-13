package com.hyperether.pipitit.push.fcm;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hyperether.pipitit.cache.PipititLogger;

/**
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class PipititInstanceId extends FirebaseInstanceIdService {
    public static final String TAG = PipititInstanceId.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of the previous token
     * had been compromised. This call is initiated by the InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        PipititLogger.d(TAG, "onTokenRefresh");
    }
}
