package it.motta.mbdage.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class MakeHttpRequest {

    public static final String baseIP = "http://3.17.164.66/api/";

    public static void sendPost(Context mContext, String url, Map<String, String> sendParams, Response.Listener<String> response, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                return sendParams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public static void sendPost(Context mContext, String url, Map<String, String> sendParams, Response.Listener<String> response) {
        sendPost(mContext, url, sendParams, response, null);
    }

    public static void sendGet(Context mContext, String url, Map<String, String> sendParams, Response.Listener<String> response, Response.ErrorListener errorListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                return sendParams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public static void sendGet(Context mContext, String url, Map<String, String> sendParams, Response.Listener<String> response) {
        sendGet(mContext, url, sendParams, response, null);
    }
}