package com.example.buddyapp;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

public class User {

    public static BackendlessUser user = Backendless.UserService.CurrentUser();
}
