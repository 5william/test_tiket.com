package com.example.williamtestiket.Component;


import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class BaseAPI {
    //Key of response code of API
    private static String _CODE = "status";
    //Key of response message of API
    private static String _MESSAGE = "message";

    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    public static void baseRequest(final int method, final Activity activity, final String url, final JSONObject request, final ServerListener listener) {
        Log.e(TAG, "request url: " +  url + " : " + request.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(response);
                Log.e(TAG,url + " : " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {

                    Log.e(TAG,url + " : " + new String(networkResponse.data));
                }
                if (error instanceof NetworkError) {
                    listener.onFailed(500, "No connection");
                } else if (error instanceof TimeoutError) {
                    listener.onFailed(400, "Timeout Error");
                } else if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(jsonError);
                        listener.onFailed(error.networkResponse.statusCode, response.optString(_MESSAGE));
                    } catch (JSONException e) {
                        listener.onFailed(400, error.getMessage());
                    }
                } else {
                    listener.onFailed(400, error.getMessage() != null ? error.getMessage() : "Error");
                }
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

        };
        VolleySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest, BaseAPI.class.getSimpleName());
    }




    public interface ServerListener {
        void onSuccess(JSONObject response);
        void onFailed(int errorCode, String message);
    }

}
