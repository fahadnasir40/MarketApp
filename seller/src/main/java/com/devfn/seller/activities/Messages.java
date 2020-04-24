package com.devfn.seller.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.common.model.User;
import com.devfn.seller.R;
import com.devfn.seller.adapters.MessageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages extends AppCompatActivity {

    private Button backButton;
    private DatabaseReference chatReference,databaseReference;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<HashMap<String,Object>> chatData;
    private Switch userStatus;
    private FirebaseUser user;
    private boolean isInvisible;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        user = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Chat Details. Please wait");
        backButton = findViewById(R.id.btn_back_messages);
      //  message = findViewById(R.id.messages_item);
        recyclerView = findViewById(R.id.rv_messages);
        userStatus = findViewById(R.id.messages_status);

        chatData = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(chatData,this);
        recyclerView.setAdapter(messageAdapter);
        userStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && status!=null){
                    if(!setStatus("Online"))
                        userStatus.setChecked(false);
                }
                else{
                    if(isInvisible){
                        setStatus("Offline");
                    }
                    else{
                        showDialog();
                    }
                }
            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Messages.this,MainActivitySeller.class);
                startActivity(intent);
                finish();
            }
        });

        chatReference = FirebaseDatabase.getInstance().getReference("chats");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        readData();
    }



    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Turn off visibility status");

        builder.setMessage("You will not be visible to other users until you turn it on.");

        builder.setPositiveButton("Turn Off", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                if(!setStatus("Offline"))
                    userStatus.setChecked(true);

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                userStatus.setChecked(true);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean setStatus(String value) {
        if(isNetworkConnected()){
            if(value.equals("Offline"))
                databaseReference.child("users").child(user.getUid()).child("visibility").setValue("invisible");
            else
                databaseReference.child("users").child(user.getUid()).child("visibility").setValue("visible");

            databaseReference.child("users").child(user.getUid()).child("status").setValue(value);
            databaseReference.child("users").child(user.getUid()).child("statusTimestamp").setValue(String.valueOf(System.currentTimeMillis()));
            return true;
        }
        else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return  false;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    private void readData() {

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    chatData.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        ChatModel chatModel = ds.getValue(ChatModel.class);
                        if(chatModel!=null){

                            Map<String,ChatMessage> map = chatModel.getMessages();
                            Collection<ChatMessage> values = map.values();
                            List<ChatMessage> array = new ArrayList<>(values);

                            Collections.sort(array, new Comparator<ChatMessage>() {
                                public int compare(ChatMessage o1, ChatMessage o2) {
                                    Date a = new Timestamp(Long.parseLong(o1.getTimeStamp()));
                                    Date b = new Timestamp(Long.parseLong(o2.getTimeStamp()));
                                    return b.compareTo(a);
                                }
                            });
                            HashMap<String,Object> data = new HashMap<>();
                            data.put("senderName",chatModel.getSenderName());

                            for(ChatMessage message:array) {
                                if(!message.getStatus().equals("3-1") ){
                                    data.put("messageObject",message);
                                    data.put("senderName",chatModel.getSenderName());
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
                            data.put("senderId",chatModel.getSenderId());
                            data.put("seenCounter",seenCounter);
                            chatData.add(data);

                        }
                    }
                    messageAdapter.notifyDataSetChanged();
                }
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        databaseReference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User userModel = dataSnapshot.getValue(User.class);
                    status = userModel.getStatus();
                    if (status != null) {

                        if (userModel.getVisibility() != null && userModel.getVisibility().equals("invisible")) {
                            isInvisible = true;
                            userStatus.setChecked(false);
                            userStatus.setTextOff("Offline");
                            userStatus.setText("Offline");
                        } else {
                            isInvisible = false;
                            userStatus.setTextOn("Online");
                            userStatus.setText("Online");
                            userStatus.setChecked(true);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
