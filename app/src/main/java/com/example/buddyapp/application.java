package com.example.buddyapp;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class application extends Application {
    public static final String APPLICATION_ID = "8C8BABFC-4C48-4D3B-8A6D-3E5EFDB628B9";
    public static final String API_KEY = "85C7ECA2-B2C6-4175-98A0-86B6BFA67094";
    public static final String SERVER_URL = "https://api.backendless.com";


    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(getApplicationContext(), APPLICATION_ID, API_KEY);
    }


}
