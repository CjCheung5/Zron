package com.example.buddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayList ageSelection = new ArrayList();
    String[] langSelection = {"Chinese", "Spanish", "English","Spanish", "French", "Deutsch"};
    ArrayList localeSelection = new ArrayList();
    String[] gameSelection = {"World of Warcraft", "Overwatch", "Call of Duty", "League of Lengend", "Fortnite", "Pokemon GO"};
    String[] styleSelection = {"Casual", "Softcore", "Hardcore", "Working Class"};

    String email, pw, name, age, language, location, style, game1, game2, game3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tvEmail = findViewById(R.id.tvEmail);
        final EditText etEmail = findViewById(R.id.etEmail);
        TextView tvPassword = findViewById(R.id.tvPassword);
        final EditText etPassword = findViewById(R.id.etPassword);
        TextView tvName = findViewById(R.id.tvName);
        final EditText etName = findViewById(R.id.etName);
        TextView tvAge = findViewById(R.id.tvAge);
        final Spinner spinAge = findViewById(R.id.spinAge);
        TextView tvLang = findViewById(R.id.tvLang);
        final Spinner spinLang = findViewById(R.id.spinLang);
        TextView tvLocation = findViewById(R.id.tvLocation);
        final Spinner spinLocation = findViewById(R.id.spinLocation);
        TextView tvGame = findViewById(R.id.tvGame);
        final Spinner spinGame = findViewById(R.id.spinGame);
        final Spinner spinGame2 = findViewById(R.id.spinGame2);
        spinGame2.setEnabled(false);
        final Spinner spinGame3 = findViewById(R.id.spinGame3);
        spinGame3.setEnabled(false);
        Button btnAdd = findViewById(R.id.btnAdd);
        TextView tvStyle = findViewById(R.id.tvStyle);
        final Spinner spinStyle = findViewById(R.id.spinStyle);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        final ProgressBar pbLoading = findViewById(R.id.loading);


        //Age

        for (int i=0; i<=100; i++){
            ageSelection.add(i);
        }

        ArrayAdapter ageAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ageSelection);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAge.setAdapter(ageAdapter);
        spinAge.setOnItemSelectedListener(this);

        // Language

        ArrayAdapter langAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, langSelection);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLang.setAdapter(langAdapter);
        spinLang.setOnItemSelectedListener(this);

        // Location

        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale: locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !localeSelection.contains(country)) {
                localeSelection.add(country);
            }
        }
        Collections.sort(localeSelection);

        ArrayAdapter localeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, localeSelection);
        localeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLocation.setAdapter(localeAdapter);
        spinLocation.setOnItemSelectedListener(this);

        // Game

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spinGame2.isEnabled()){
                    spinGame2.setEnabled(true);
                    spinGame2.setVisibility(View.VISIBLE);
                }
                else {
                    spinGame3.setEnabled(true);
                    spinGame3.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayAdapter gameAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gameSelection);
        localeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinGame.setAdapter(gameAdapter);
        spinGame.setOnItemSelectedListener(this);
        spinGame2.setAdapter(gameAdapter);
        spinGame2.setOnItemSelectedListener(this);
        spinGame3.setAdapter(gameAdapter);
        spinGame3.setOnItemSelectedListener(this);


        //Style

        ArrayAdapter styleAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, styleSelection);
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStyle.setAdapter(styleAdapter);
        spinStyle.setOnItemSelectedListener(this);

        //Submit


        //log data into server
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString().trim();
                pw = etPassword.getText().toString().trim();
                name = etName.getText().toString().trim();
                age = spinAge.getSelectedItem().toString().trim();
                language = spinLang.getSelectedItem().toString().trim();
                location = spinLocation.getSelectedItem().toString().trim();

                if (spinGame.isEnabled()){
                    game1 = spinGame.getSelectedItem().toString();

                }
                if (spinGame2.isEnabled()){
                    game2 = spinGame2.getSelectedItem().toString();
                };
                if (spinGame3.isEnabled()){
                    game3 = spinGame3.getSelectedItem().toString();
                };

                style = spinStyle.getSelectedItem().toString().trim();

                BackendlessUser user = new BackendlessUser();
                if (email.isEmpty() || pw.isEmpty()){
                    Toast.makeText(Register.this, "Please fill in email and password", Toast.LENGTH_SHORT).show();
                } else {
                    user.setProperty("email", email);
                    user.setPassword(pw);
                    user.setProperty("age", age);
                    user.setProperty("name", name);
                    user.setProperty("language", language);
                    user.setProperty("location", location);
                    user.setProperty("game1", game1);
                    user.setProperty("game2", game2);
                    user.setProperty("game3", game3);
                    user.setProperty("style", style);


                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            Toast.makeText(Register.this, "You have successfully registered", Toast.LENGTH_SHORT).show();

                            Backendless.UserService.logout(new AsyncCallback<Void>() {
                                @Override
                                public void handleResponse(Void response) {
                                    Backendless.UserService.login(email, pw, new AsyncCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser response) {
                                            User.user = response;
                                            Intent intent = new Intent(Register.this, MainActivity.class);
                                            startActivity(intent);
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Toast.makeText(Register.this, fault.toString(), Toast.LENGTH_LONG).show();

                                        }
                                    }, true);

                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {

                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(Register.this, fault.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                };

            }
        });



    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
