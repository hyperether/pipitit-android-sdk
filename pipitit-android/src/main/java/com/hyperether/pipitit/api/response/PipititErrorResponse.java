package com.hyperether.pipitit.api.response;

/**
 * Error response returned from server ((HTTP) response codes)
 * <p>
 * Error response has {@link #code} the status code is  part of the HTTP/1.1 standard and {@link
 * #message} code description
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class PipititErrorResponse {

    /**
     * Code description
     */
    private String message;

    /**
     * Status code
     */
    private String code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
