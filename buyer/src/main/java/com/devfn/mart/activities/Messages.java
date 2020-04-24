package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.mart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Messages extends AppCompatActivity {

    private Button backButton;
    private RelativeLayout container;
    private ChatMessage chatMessage;
    private DatabaseReference chatReference;
    private ProgressDialog progressDialog;
    private boolean chatFound;
    private FirebaseUser user;
    private TextView message,date,seenBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        user = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Chat Details. Please wait");
        //progressDialog.show();
        backButton = findViewById(R.id.btn_back_messages);
        container = findViewById(R.id.messages_item);
        message = findViewById(R.id.messages_first_message);
        date = findViewById(R.id.messages_time);
        seenBadge = findViewById(R.id.messages_new_badge);
        chatFound = false;


        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(Messages.this,Chat.class);
            startActivity(intent);
            finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(Messages.this,MainActivity.class);
            startActivity(intent);
            finish();
            }
        });

        chatReference = FirebaseDatabase.getInstance().getReference("chats");
        readData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        if(chatReference!=null && valueEventListener!=null)
            chatReference.removeEventListener(valueEventListener);
    }

    private void readData() {
        chatReference.child(user.getUid()).addValueEventListener(valueEventListener);

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists()){
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                Map<String,ChatMessage> map = chatModel.getMessages();

                if(map == null){
                    if(date.getVisibility() == View.VISIBLE){
                        date.setVisibility(View.GONE);
                        message.setText("Chat Now");
                        message.setTextColor(getResources().getColor(R.color.PaleVioletRed));
                    }
                }
                else{
                    Collection<ChatMessage> values = map.values();
                    List<ChatMessage> array = new ArrayList<>(values);

                    Collections.sort(array, new Comparator<ChatMessage>() {
                        public int compare(ChatMessage o1, ChatMessage o2) {
                            Date a = new Timestamp(Long.parseLong(o1.getTimeStamp()));
                            Date b = new Timestamp(Long.parseLong(o2.getTimeStamp()));
                            return b.compareTo(a);
                        }
                    });

                    for(ChatMessage message:array) {
                        if(!message.getStatus().equals("3-1") ){
                            chatMessage =message;
                            setChatData();
                            break;
                        }
                    }

                    int seenCounter = 0;
                    for(ChatMessage message:array){
                        if(!message.getChatId().equals(user.getUid())){
                            if(!message.getIsSeen())
                                seenCounter++;
                        }
                    }

                    if(seenCounter!=0){
                        if(seenBadge.getVisibility()==View.GONE)
                            seenBadge.setVisibility(View.VISIBLE);
                        seenBadge.setText(String.valueOf(seenCounter));

                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(chatReference!=null && valueEventListener!=null)
            chatReference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(chatReference!=null && valueEventListener!=null)
            chatReference.removeEventListener(valueEventListener);
    }



    private void setChatData() {

        if(date.getVisibility()==View.GONE){

            date.setVisibility(View.VISIBLE);
            message.setTextColor(getResources().getColor(R.color.gray));
        }

            message.setText(chatMessage.getMessage());
            PrettyTime time = new PrettyTime();
            date.setText(time.format(new Date(Long.parseLong(chatMessage.getTimeStamp()))));


    }

}
