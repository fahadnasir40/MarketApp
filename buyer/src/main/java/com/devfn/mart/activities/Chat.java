package com.devfn.mart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devfn.common.model.ChatMessage;
import com.devfn.mart.R;
import com.devfn.mart.adapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity {

    private Button backButton;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private EditText editText;
    private ImageButton sendButton;
    private List<ChatMessage> chatList;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        backButton = findViewById(R.id.btn_back_chat);
        sendButton = findViewById(R.id.btn_chat_send_message);
        editText = findViewById(R.id.chat_message_text);
        recyclerView = findViewById(R.id.rv_chat);
        chatList = new ArrayList<>();

        messageAdapter = new MessageAdapter(chatList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    sendMessage();
             }
        });

        databaseRef = FirebaseDatabase.getInstance().getReference("chats");

    }

    private void sendMessage() {

        String message = editText.getText().toString();

        if(!message.equals("")){
            String uid = FirebaseAuth.getInstance().getUid();
            ChatMessage chatMessage = new ChatMessage(uid,message,"1",String.valueOf(System.currentTimeMillis()));
//            databaseRef.child(uid).setValue(chatMessage);
            chatList.add(chatMessage);
            messageAdapter.notifyDataSetChanged();
            editText.setText("");

        }

    }
}
