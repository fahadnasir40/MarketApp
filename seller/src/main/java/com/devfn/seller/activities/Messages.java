package com.devfn.seller.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.devfn.common.model.ChatModel;
import com.devfn.seller.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Messages extends AppCompatActivity {

    private Button backButton;
    private RelativeLayout message;
    private ChatModel chatModel;
    private DatabaseReference chatReference;
    private ProgressDialog progressDialog;
    private boolean chatFound;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        user = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Chat Details. Please wait");

        backButton = findViewById(R.id.btn_back_messages);
        message = findViewById(R.id.messages_item);
        chatFound = false;

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Messages.this, ChatSeller.class);
                startActivity(intent);
                finish();
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
        readData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void readData() {

//
//        chatReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                   chatModel = dataSnapshot.getValue(ChatModel.class);
//                   setChatData(chatModel);
//                   chatFound = true;
//                }
//
//                if(progressDialog.isShowing())
//                    progressDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressDialog.dismiss();
//            }
//        });
    }

    private void setChatData(ChatModel chatModel) {


    }
}
