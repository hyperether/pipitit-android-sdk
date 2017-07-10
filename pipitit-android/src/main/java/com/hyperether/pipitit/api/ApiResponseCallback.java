package com.hyperether.pipitit.api;

import com.hyperether.pipitit.api.response.PipititServerResponse;

/**
 * Callback to return answer from api response
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/30/2017
 */
public interface ApiResponseCallback {

    void onSuccess(PipititServerResponse response);

    void onError(String message);
}
