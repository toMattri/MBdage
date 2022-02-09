package it.motta.mbdage.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

public class MakeHttpRequest {

    //   public static final String BASE_IP = "http://3.17.164.66/api/";
    public static final String BASE_IP = "http://192.168.1.27/mbdage/api/";
  //  public static final String BASE_IP = "http://192.168.1.63/mbdage/api/";
    public static final String REGISTER = "register.php";
    public static final String REGISTER_TOKEN = "registerToken.php";
    public static final String LOGIN = "login.php";
    public static final String GET_VARCHI = "varchi.php";
    public static final String GET_PASSAGGI = "passaggi.php";
    public static final String OPEN_VARCO = "openVarco.php";
    public static final String CREA_VARCO = "creaVarco.php";


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