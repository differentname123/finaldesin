package com.fb.standard.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class App extends Application {
    private static Context context;
    private static RequestQueue  requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

}
