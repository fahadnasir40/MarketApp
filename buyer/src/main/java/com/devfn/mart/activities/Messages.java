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
import android.widget.Toast;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.mart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Map;

public class Messages extends AppCompatActivity {

    private Button backButton;
    private RelativeLayout container;
    private ChatMessage chatMessage;
    private DatabaseReference chatReference;
    private ProgressDialog progressDialog;
    private boolean chatFound;
    private FirebaseUser user;
    private TextView message,date;

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
    }

    private void readData() {
        chatReference.child(user.getUid()).child("messages").addValueEventListener(valueEventListener);

    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            GenericTypeIndicator<Map<String,ChatMessage>> genericTypeIndicator = new GenericTypeIndicator<Map<String, ChatMessage>>() {};
            Map<String,ChatMessage> map = dataSnapshot.getValue(genericTypeIndicator);
            if(map != null){
                for(ChatMessage cm:map.values()){
                    if(!cm.getStatus().equals("3-1") ){
                        chatMessage = cm;
                        setChatData();
                        break;
                    }
                }
            }
            else{
                if(date.getVisibility() == View.VISIBLE){
                    date.setVisibility(View.GONE);
                    message.setText("Chat Now");
                    message.setTextColor(getResources().getColor(R.color.PaleVioletRed));

                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

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
