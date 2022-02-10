package com.example.buddyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.servercode.annotation.Async;

import java.util.ArrayList;
import java.util.UUID;

public class MatchedProfileDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        final Button btnMP = findViewById(R.id.btnMP);
        Button btnMPUser = findViewById(R.id.btnMPUser);

        TextView tvMP1 = findViewById(R.id.tvMP1);
        final TextView tvMP1a = findViewById(R.id.tvMP1a);
        final TextView tvMP1b = findViewById(R.id.tvMP1b);
        TextView tvMP2 = findViewById(R.id.tvMP2);
        final TextView tvMP2a = findViewById(R.id.tvMP2a);
        final TextView tvMP2b = findViewById(R.id.tvMP2b);
        TextView tvMP3 = findViewById(R.id.tvMP3);
        final TextView tvMP3a = findViewById(R.id.tvMP3a);
        final TextView tvMP3b = findViewById(R.id.tvMP3b);
        TextView tvMP4 = findViewById(R.id.tvMP4);
        final TextView tvMP4a = findViewById(R.id.tvMP4a);
        final TextView tvMP4b = findViewById(R.id.tvMP4b);
        TextView tvMP5 = findViewById(R.id.tvMP5);
        final TextView tvMP5a = findViewById(R.id.tvMP5a);
        final TextView tvMP5b = findViewById(R.id.tvMP5b);
        final TextView tvMP6 = findViewById(R.id.tvMP6);
        final TextView tvMP6a = findViewById(R.id.tvMP6a);

        final TextView tvMP7 = findViewById(R.id.tvMP7);
        final TextView tvMP7a = findViewById(R.id.tvMP7a);

        final TextView tvMP8 = findViewById(R.id.tvMP8);
        final TextView tvMP8a = findViewById(R.id.tvMP8a);

        final TextView tvMPResult = findViewById(R.id.tvMPResult);
        final Button btnChat = findViewById(R.id.btnChat);


        btnMPUser.setText(User.user.getProperty("name").toString().substring(0,2));
        tvMP1b.setText(User.user.getProperty("name").toString());
        tvMP2b.setText(User.user.getProperty("age").toString());
        tvMP3b.setText(User.user.getProperty("language").toString());
        tvMP4b.setText(User.user.getProperty("location").toString());
        tvMP5b.setText(User.user.getProperty("style").toString());
        final ArrayList userGameAL = new ArrayList();
        if (!User.user.getProperty("game1").equals("null")) {userGameAL.add(User.user.getProperty("game1"));}
        if (!User.user.getProperty("game2").equals("null")) {userGameAL.add(User.user.getProperty("game2"));}
        if (!User.user.getProperty("game3").equals("null")) {userGameAL.add(User.user.getProperty("game3"));}

        if (getIntent().getExtras() != null){
            final String profileID = getIntent().getStringExtra("profileID");

            tvMPResult.setText(getIntent().getStringExtra("matchedRate") + " %");
            Backendless.Data.of(BackendlessUser.class).findById(getIntent().getStringExtra("profileID"), new AsyncCallback<BackendlessUser>() {
               @Override
               public void handleResponse(BackendlessUser response) {
                   btnMP.setText(response.getProperty("name").toString().substring(0,2));
                   tvMP1a.setText(response.getProperty("name").toString());
                   tvMP2a.setText(response.getProperty("age").toString());
                   tvMP3a.setText(response.getProperty("language").toString());
                   if (tvMP3a.getText().equals(tvMP3b.getText())){
                       tvMP3a.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                       tvMP3b.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                   };

                   tvMP4a.setText(response.getProperty("location").toString());
                   if (tvMP4a.getText().equals(tvMP4b.getText())){
                       tvMP4a.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                       tvMP4b.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                   };
                   tvMP5a.setText(response.getProperty("style").toString());
                   if (tvMP5a.getText().equals(tvMP5b.getText())){
                       tvMP5a.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                       tvMP5b.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                   };

                   ArrayList matchedGameAL = new ArrayList();

                   if (userGameAL.contains(response.getProperty("game1"))){
                       matchedGameAL.add(response.getProperty("game1"));
                   }
                   if (userGameAL.contains(response.getProperty("game2"))){
                       matchedGameAL.add(response.getProperty("game2"));
                   }
                   if (userGameAL.contains(response.getProperty("game3"))){
                       matchedGameAL.add(response.getProperty("game3"));
                   };


                   if((matchedGameAL.size() - 1) >= 0){
                       tvMP6.setVisibility(View.VISIBLE);
                       tvMP6a.setVisibility(View.VISIBLE);
                       tvMP6a.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                       tvMP6a.setText(matchedGameAL.get(0).toString());
                   }
                   if((matchedGameAL.size() - 1) >= 1){
                       tvMP7.setVisibility(View.VISIBLE);
                       tvMP7a.setVisibility(View.VISIBLE);
                       tvMP7a.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                       tvMP7a.setText(matchedGameAL.get(1).toString());
                   }
                   if((matchedGameAL.size() - 1) >= 2){
                       tvMP8.setVisibility(View.VISIBLE);
                       tvMP8a.setVisibility(View.VISIBLE);
                       tvMP8a.setBackgroundColor(getResources().getColor(R.color.colorMPmatcheditem));
                       tvMP8a.setText(matchedGameAL.get(2).toString());
                   }

               }

               @Override
               public void handleFault(BackendlessFault fault) {

               }
           });

            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MatchedProfileDetail.this, MessageRoom.class);
                    intent.putExtra("profileID", profileID);
                    startActivity(intent);
                }
            });
       }
        else{
            Toast.makeText(MatchedProfileDetail.this, "No Data Received", Toast.LENGTH_LONG).show();
        }
    };
}
