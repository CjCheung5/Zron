package com.example.buddyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishMessageInfo;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.rt.messaging.Channel;
import com.backendless.rt.messaging.MessageInfoCallback;

import java.nio.channels.Selector;
import java.util.List;
import java.util.UUID;

public class MessageRoom extends AppCompatActivity {

    private ListView lvMessageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_room);

        final String userID = User.user.getUserId();
        final String sendByTo = userID + getIntent().getStringExtra("profileID");
        final String sendToBy = getIntent().getStringExtra("profileID") + userID;

        final MessageAdapter messageAdapter = new MessageAdapter(this);
        messageAdapter.notifyDataSetChanged();
        lvMessageView = (ListView) findViewById(R.id.lvMessage);
        lvMessageView.setAdapter(messageAdapter);



        String selector = "sendInfo = '" + sendByTo + "'" +" OR sendInfo = '" + sendToBy + "'";

        final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(selector).setSortBy("timeStamp ASC");
        Backendless.Data.of(Message.class).find(queryBuilder, new AsyncCallback<List<Message>>() {
            @Override
            public void handleResponse(List<Message> response) {
                messageAdapter.addAll(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("retrievingConversation", fault.toString());
            }
        });


        // Subscribe the channel
        final Channel channel = Backendless.Messaging.subscribe("chat");
        channel.join();

       /*
        if (channel.isJoined()) {
            Toast.makeText(MessageRoom.this, "You have joined a channel", Toast.LENGTH_LONG).show();

        } else{
            Toast.makeText(MessageRoom.this, "You have not joined a channel", Toast.LENGTH_LONG).show();
        }
        */

        channel.addMessageListener(selector, new MessageInfoCallback() {
            @Override
            public void handleResponse(PublishMessageInfo response) {

                final String message = response.getMessage().toString();
                String messageID = response.getMessageId();
                String publisherID = response.getPublisherId();
                String sendInfo = response.getHeaders().get("sendInfo");
                Long timeStamp = response.getTimestamp();

                final Message newMessage = new Message();
                newMessage.setMessage(message);
                newMessage.setMessageID(messageID);
                newMessage.setPublisherID(publisherID);
                newMessage.setSendInfo(sendInfo);
                newMessage.setTimeStamp(timeStamp);

                messageAdapter.add(newMessage);


                queryBuilder.setWhereClause("messageID = '" + newMessage.getMessageID() + "'");
                Backendless.Persistence.of(Message.class).find(queryBuilder, new AsyncCallback<List<Message>>() {
                    @Override
                    public void handleResponse(List<Message> response) {
                        if (response.isEmpty()){
                            Backendless.Data.of(Message.class).save(newMessage, new AsyncCallback<Message>() {
                                @Override
                                public void handleResponse(Message response) {
                                    Log.i("Message Table", "New Message has been logged");
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Log.e("Message Table", fault.toString());
                                }
                            });
                        }
                        else {
                            Log.i("Message Table", "The Message has been created already");
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {

                    }
                });

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        final EditText etMessageBox = findViewById(R.id.etMessagebox);
        Button btnSendMessage = findViewById(R.id.btnSendMessage);


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishOptions publishOptions = new PublishOptions();
                publishOptions.putHeader("sendInfo", sendByTo);
                publishOptions.setPublisherId(userID);
                String message;
                if(!etMessageBox.getText().toString().isEmpty()){
                    message = etMessageBox.getText().toString().trim();
                    Backendless.Messaging.publish("chat", message, publishOptions, new AsyncCallback<MessageStatus>() {
                        @Override
                        public void handleResponse(MessageStatus response) {
                            Toast.makeText(MessageRoom.this, "Your message has been successfully published", Toast.LENGTH_LONG).show();
                            etMessageBox.getText().clear();

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                        }
                    });
                }
            }
        });

    }
}
