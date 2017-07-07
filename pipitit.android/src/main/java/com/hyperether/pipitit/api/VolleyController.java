package com.hyperether.pipitit.api;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import okhttp3.OkHttpClient;

/**
 * Class for managing api request
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.0 - 4/27/2017
 */
public class VolleyController {
    public static final String TAG = VolleyController.class.getSimpleName();

    private static VolleyController INSTANCE;
    private RequestQueue mRequestQueue;

    private VolleyController() {
    }

    public static synchronized VolleyController getInstance() {
        if (INSTANCE == null)
            INSTANCE = new VolleyController();
        return INSTANCE;
    }

    public static void clear() {
        if (INSTANCE != null) {
            if (INSTANCE.mRequestQueue != null) {
                INSTANCE.mRequestQueue.stop();
                INSTANCE.mRequestQueue = null;
            }
        }
        INSTANCE = null;
    }

    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(new OkHttpClient()));
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag, Context context) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue(context).add(req);
    }
}
