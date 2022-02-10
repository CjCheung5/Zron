package com.example.buddyapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.PublishMessageInfo;
import com.backendless.messaging.PublishOptions;

import org.w3c.dom.Text;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends BaseAdapter{

    Context context;

    List<Message> messageList = new ArrayList<>();


    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add (Message message){
        messageList.add(message);
    }
    public void addAll (Collection<Message> messages){
        messageList.addAll(messages);
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final MessageViewHolder messageHolder = new MessageViewHolder();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Long timeStamp = messageList.get(i).getTimeStamp();
        String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(timeStamp);
        String message = messageList.get(i).getMessage().toString();

        if (messageList.get(i).getPublisherID().equals(User.user.getUserId())) {
            view = layoutInflater.inflate(R.layout.list_item_user, null);

            messageHolder.messageBody = (TextView) view.findViewById(R.id.tvUserMessagebody);
            messageHolder.sentBy = (TextView) view.findViewById(R.id.tvUserSentby);
            messageHolder.timeStamp = (TextView) view.findViewById(R.id.tvUserTimestamp);
            view.setTag(messageHolder);

            messageHolder.messageBody.setText(message);
            messageHolder.timeStamp.setText(date);

            return view;
        }
        else{
            view = layoutInflater.inflate(R.layout.list_item_general, null);

            messageHolder.messageBody = (TextView) view.findViewById(R.id.tvMessagebody);
            messageHolder.sentBy = (TextView) view.findViewById(R.id.tvSentby);
            messageHolder.timeStamp = (TextView) view.findViewById(R.id.tvTimestamp);
            view.setTag(messageHolder);

            messageHolder.messageBody.setText(message);
            messageHolder.timeStamp.setText(date);

            Backendless.UserService.findById(messageList.get(i).getPublisherID(), new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser response) {
                    messageHolder.sentBy.setText(response.getProperty("name").toString());
                }

                @Override
                public void handleFault(BackendlessFault fault) {

                }
            });

            return view;
        }
    };

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
            Log.i("Notify Data Entry", "New Data change notification");
    }

}
