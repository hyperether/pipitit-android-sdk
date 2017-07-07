package com.hyperether.pipitit.api.response;

/**
 * Class that have error response form server
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class PipititServerResponse {

    public PipititErrorResponse error;

    public PipititErrorResponse getError() {
        return error;
    }

    public String getCode() {
        return error.getCode();
    }

    public String getDescription() {
        return error.getMessage();
    }

    public boolean hasError() {
        return error != null;
    }
}
