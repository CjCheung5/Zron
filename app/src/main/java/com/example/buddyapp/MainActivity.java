package com.example.buddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.persistence.local.*;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.buddyapp.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnMain = findViewById(R.id.btnMainSearchExisting);
        final Button btnMainLogout = findViewById(R.id.btnMainLogout);
        final Button btnMainSearchNew = findViewById(R.id.btnMainSearchNew);
        final TextView tvWelcome = findViewById(R.id.tvWelcome);

        String userToken = UserTokenStorageFactory.instance().getStorage().get();


        //isValidLogin
        AsyncCallback<Boolean> isValidLogin = new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if (response) {
                    btnMainLogout.setVisibility(View.VISIBLE);
                   String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();

                   Backendless.Data.of(BackendlessUser.class).findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                       @Override
                       public void handleResponse(BackendlessUser response) {
                           User.user = response;
                               if (User.user.getProperty("name").toString() != null){
                                   tvWelcome.setText("Welcome Back! " + (String)User.user.getProperty("name"));
                               };
                           };

                       @Override
                       public void handleFault(BackendlessFault fault) {
                       }
                   });

                    //SearchNew Button
                    btnMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    //Search Button
                    btnMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           Intent intent = new Intent(MainActivity.this, Search.class);
                           startActivity(intent);
                        }
                    });

                    btnMainLogout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Backendless.UserService.logout(new AsyncCallback<Void>() {
                                @Override
                                public void handleResponse(Void response) {
                                    Toast.makeText(MainActivity.this, "You have successfully Logout", Toast.LENGTH_LONG);
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });


                        }
                    });

                }
                else{
                    //Search Button
                    btnMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this, "Please Login your account", Toast.LENGTH_LONG ).show();
            }
        };
        Backendless.UserService.isValidLogin(isValidLogin);


    }
}
