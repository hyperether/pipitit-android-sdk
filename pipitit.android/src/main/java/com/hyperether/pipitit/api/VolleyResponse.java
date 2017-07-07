package com.hyperether.pipitit.api;

/**
 *  Interface for server Volley responses
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public interface VolleyResponse {

    void onSuccess(String response);

    void onError(int statusCode, String message);
}
