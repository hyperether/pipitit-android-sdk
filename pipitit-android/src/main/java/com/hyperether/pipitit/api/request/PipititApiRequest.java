package com.hyperether.pipitit.api.request;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hyperether.pipitit.api.VolleyController;
import com.hyperether.pipitit.api.VolleyResponse;
import com.hyperether.pipitit.cache.PipititLogger;

import org.json.JSONObject;

/**
 * Class is making API request and wait for response from server
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/28/2017
 */
public class PipititApiRequest {

    private static final String TAG = PipititApiRequest.class.getSimpleName();
    private static final int DEFAULT_TIMEOUT = 15000;
    private static final int DEFAULT_RETRY = 0;
    private VolleyResponse callback;

    public PipititApiRequest(VolleyResponse listener) {
        this.callback = listener;
    }

    /**
     * Make {@link JsonObjectRequest} and add in {@link VolleyController}. Retry for this request is
     * set to {@link #DEFAULT_RETRY}, and time out to {@link #DEFAULT_TIMEOUT}
     *
     * @param request request body
     * @param requestTag request description
     * @param context context
     */
    public void addJsonRequest(final PipititServerRequest request, String requestTag,
                               Context context) {
        addJsonRequest(request, requestTag, context, DEFAULT_TIMEOUT, DEFAULT_RETRY,
                Request.Method.POST);
    }

    /**
     * Make {@link JsonObjectRequest} and add in {@link VolleyController}. Retry for this request is
     * set to {@link #DEFAULT_RETRY}, and time out to {@link #DEFAULT_TIMEOUT}
     *
     * @param request request body
     * @param requestTag request description
     * @param context context
     */
    public void addJsonRequest(final PipititServerRequest request, String requestTag, int method,
                               Context context) {
        addJsonRequest(request, requestTag, context, DEFAULT_TIMEOUT, DEFAULT_RETRY, method);
    }

    /**
     * Make {@link JsonObjectRequest} and add in {@link VolleyController}
     *
     * @param request request body
     * @param requestTag request description
     * @param context context
     */
    public void addJsonRequest(final PipititServerRequest request, String requestTag,
                               Context context, int timeout, int retryNum, int method) {

        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    method,
                    request.getUrl(),
                    request.getJsonObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            PipititLogger.d(TAG, "RESPONSE = " + request.getUrl() + " : " +
                                    response.toString());
                            callback.onSuccess(response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String message = "";
                            int statusCode = -1;
                            if (error != null && error.networkResponse != null) {
                                message = new String(error.networkResponse.data);
                                statusCode = error.networkResponse.statusCode;
                            }
                            callback.onError(statusCode, message);
                            PipititLogger.d(TAG,
                                    "ERROR = " + request.getUrl() + " : " + error.toString());
                        }
                    });

            jsObjRequest.setShouldCache(false);
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(timeout, retryNum,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyController.getInstance().addToRequestQueue(jsObjRequest, requestTag, context);

            PipititLogger.d(TAG, request.getUrl() + " " + (request.getJsonObject() != null ?
                    request.getJsonObject().toString() : "null"));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            PipititLogger.e(TAG, "Error", throwable);
        }
    }
}
