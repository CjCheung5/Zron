package com.example.buddyapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class Search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final Button btnUser = findViewById(R.id.btnUser);
        final Button btnMatch1 = findViewById(R.id.btnMatch1);
        final Button btnMatch2 = findViewById(R.id.btnMatch2);
        final Button btnMatch3 = findViewById(R.id.btnMatch3);
        final Button btnMatch4 = findViewById(R.id.btnMatch4);

         final BackendlessUser user = User.user;
         final String userID = user.getUserId();
        final Object userGame1 = user.getProperty("game1");
        final Object userGame2 = user.getProperty("game2");
        final Object userGame3 = user.getProperty("game3");
        final Object age = user.getProperty("age");
        final Object location = user.getProperty("location");
        final Object language = user.getProperty("language");
        final Object style = user.getProperty("style");

        btnUser.setText(user.getProperty("name").toString());





        //matching
        Backendless.Data.of(BackendlessUser.class).find(new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(final List<BackendlessUser> response) {
                response.remove(user);

                class matchingTask extends AsyncTask<String, String, String>{

                    @Override
                    protected String doInBackground(String... strings) {
                        for (BackendlessUser item :
                                response) {
                            int counter = 0;

                            //Game counter
                            ArrayList al = new ArrayList();
                            al.add(item.getProperty("game1"));
                            al.add(item.getProperty("game2"));
                            al.add(item.getProperty("game3"));

                            if (al.contains(userGame1)){
                                counter++;
                            }
                            if (al.contains(userGame2)){
                                counter++;
                            }
                            if (al.contains(userGame3)){
                                counter++;
                            }

                            //Other properties counter
                            if (user.getProperty("language").equals(item.getProperty("language"))){
                                counter++;
                            }
                            if (user.getProperty("location").equals(item.getProperty("location"))){
                                counter++;
                            }
                            if (user.getProperty("age").toString().compareTo(item.getProperty("age").toString()) <= 5){
                                counter++;
                            }
                            if (user.getProperty("style").equals(item.getProperty("style"))){
                                counter++;
                            }


                            //creating the entry in matchedProfile table
                            final matchedProfile mp = new matchedProfile();
                            mp.setUserID(userID);
                            mp.setMatchedProfileID(item.getObjectId());
                            mp.setMatchedRate(String.valueOf(Math.round(counter*100)/8));

                            String whereClause = "userID = '" + mp.getUserID() + "' and matchedProfileID = '" + mp.getMatchedProfileID() + "'";
                            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                            queryBuilder.setWhereClause(whereClause);

                            //prevent repeated matching profile entry
                            Backendless.Persistence.of(matchedProfile.class).find(queryBuilder, new AsyncCallback<List<matchedProfile>>() {
                                @Override
                                public void handleResponse(List<matchedProfile> response) {
                                    if (response.isEmpty()){
                                        Backendless.Data.of(matchedProfile.class).save(mp, new AsyncCallback<matchedProfile>() {
                                            @Override
                                            public void handleResponse(matchedProfile response) {
                                                Toast.makeText(Search.this, "contact has been successfully created", Toast.LENGTH_LONG).show();
                                            }

                                            @Override
                                            public void handleFault(BackendlessFault fault) {
                                                Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    else{
                                        if(response.get(0).getMatchedRate().equals(mp.getMatchedRate())){
                                            return;
                                        }else{
                                            Backendless.Data.of(matchedProfile.class).save(mp, new AsyncCallback<matchedProfile>() {
                                                @Override
                                                public void handleResponse(matchedProfile response) {
                                                    Toast.makeText(Search.this, "contact has been updated", Toast.LENGTH_LONG).show();
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                        return "Executed";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        //Display matched result interface

                        DataQueryBuilder qb = DataQueryBuilder.create();
                        qb.setWhereClause("userID = '" + userID + "'");
                        qb.setSortBy("matchedRate DESC");
                        Backendless.Data.of(matchedProfile.class).find(qb, new AsyncCallback<List<matchedProfile>>() {
                            @Override
                            public void handleResponse(List<matchedProfile> response) {
                                if (!response.isEmpty()){
                                    final matchedProfile matchedProfile1 = response.get(0);
                                    final matchedProfile matchedProfile2 = response.get(1);
                                    final matchedProfile matchedProfile3 = response.get(2);
                                    final matchedProfile matchedProfile4 = response.get(3);

                                    Backendless.Data.of(BackendlessUser.class).findById(response.get(0).getMatchedProfileID(), new AsyncCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser response) {
                                            btnMatch1.setText(response.getProperty("name").toString() + " " + matchedProfile1.getMatchedRate() + "%");
                                            btnMatch1.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(Search.this, MatchedProfileDetail.class);
                                                    intent.putExtra("profileID", matchedProfile1.getMatchedProfileID());
                                                    intent.putExtra("matchedRate", matchedProfile1.getMatchedRate());
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    Backendless.Data.of(BackendlessUser.class).findById(response.get(1).getMatchedProfileID(), new AsyncCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser response) {
                                            btnMatch2.setText(response.getProperty("name").toString()+ " " + matchedProfile2.getMatchedRate() + "%");
                                            btnMatch2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(Search.this, MatchedProfileDetail.class);
                                                    intent.putExtra("profileID", matchedProfile2.getMatchedProfileID());
                                                    intent.putExtra("matchedRate", matchedProfile2.getMatchedRate());
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    Backendless.Data.of(BackendlessUser.class).findById(response.get(2).getMatchedProfileID(), new AsyncCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser response) {
                                            btnMatch3.setText(response.getProperty("name").toString()+ " " + matchedProfile3.getMatchedRate() + "%");
                                            btnMatch3.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(Search.this, MatchedProfileDetail.class);
                                                    intent.putExtra("profileID", matchedProfile3.getMatchedProfileID());
                                                    intent.putExtra("matchedRate", matchedProfile3.getMatchedRate());
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    Backendless.Data.of(BackendlessUser.class).findById(response.get(3).getMatchedProfileID(), new AsyncCallback<BackendlessUser>() {
                                        @Override
                                        public void handleResponse(BackendlessUser response) {
                                            btnMatch4.setText(response.getProperty("name").toString()+ " " + matchedProfile4.getMatchedRate() + "%");
                                            btnMatch4.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(Search.this, MatchedProfileDetail.class);
                                                    intent.putExtra("profileID", matchedProfile4.getMatchedProfileID());
                                                    intent.putExtra("matchedRate", matchedProfile4.getMatchedRate());
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(Search.this, "Matching Failed", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
                matchingTask mt = new matchingTask();
                mt.execute();
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Search.this, fault.toString(), Toast.LENGTH_LONG).show();
            }
        });

    };
};

