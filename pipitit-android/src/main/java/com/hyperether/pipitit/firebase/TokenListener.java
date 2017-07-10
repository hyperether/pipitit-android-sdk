package com.hyperether.pipitit.firebase;

/**
 * Listener is used for registration and deregistration push  token-a.
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/29/2017
 */
public interface TokenListener {

    /**
     * When we finish registration
     */
    void onSuccess(String token);

    /**
     * When something is wrong with registration
     */
    void onError();
}
