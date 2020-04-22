package com.devfn.seller.activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.seller.R;
import com.devfn.seller.adapters.MessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChatSeller extends AppCompatActivity {

    private Button backButton;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private EditText editText;
    private ImageButton sendButton;
    private List<ChatMessage> chatList;
    private DatabaseReference databaseRef;
    private ChatModel chatModel;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading chat history");


        user = FirebaseAuth.getInstance().getCurrentUser();
        backButton = findViewById(R.id.btn_back_chat);
        sendButton = findViewById(R.id.btn_chat_send_message);
        editText = findViewById(R.id.chat_message_text);
        recyclerView = findViewById(R.id.rv_chat);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatSeller.this,MainActivitySeller.class);
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
        chatList = new ArrayList<>();
        messageAdapter = new MessageAdapter(chatList,this);
        recyclerView.setAdapter(messageAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("chats");
        readData();

    }

    private void readData() {
        progressDialog.show();
        databaseRef.child("gNyg95WXHkefQjracnwVj1yHfhm2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                if(dataSnapshot.exists()){
                    chatModel = dataSnapshot.getValue(ChatModel.class);

                    Map<String, ChatMessage> map = chatModel.getMessages();
                    Collection<ChatMessage> values = map.values();

                    chatList.addAll(values);
                    Collections.sort(chatList, new Comparator<ChatMessage>() {
                        public int compare(ChatMessage o1, ChatMessage o2) {
                            Date a = new Timestamp(Long.parseLong(o1.getTimeStamp()));
                            Date b = new Timestamp(Long.parseLong(o2.getTimeStamp()));
                            return b.compareTo(a);
                        }
                    });
                }
                else if(chatModel !=null)
                    chatModel = null;

                if(progressDialog.isShowing())
                    progressDialog.dismiss();

                messageAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              if(progressDialog.isShowing())
                progressDialog.dismiss();
            }
        });
    }

    private void setChatTitle() {




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void sendMessage() {

        final String message = editText.getText().toString();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats");

        if(!message.equals("")){

            if(chatModel == null){
                String key = ref.child(user.getUid()).push().getKey();
                chatModel = new ChatModel(key,user.getUid(),"1",user.getDisplayName());
                ref.child(user.getUid()).setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        writeMessageInDB(message);
                    }
                });
            }
            else{
                writeMessageInDB(message);
            }

        }

    }

    private void writeMessageInDB(String message) {
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
        final String key = chatRef.child(user.getUid()).child("messages").push().getKey();
        Map<String,ChatMessage> map = new LinkedHashMap<>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        chatMessage.setStatus("1");

        chatMessage.setChatId(user.getUid());
        chatMessage.setKey(key);

        map.put(key,chatMessage);
        editText.setText("");
        chatRef.child("gNyg95WXHkefQjracnwVj1yHfhm2").child("messages").child(key).setValue(map.get(key)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    final DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("chats");
                    newRef.child("gNyg95WXHkefQjracnwVj1yHfhm2").child("messages").child(key).child("status").setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                newRef.child("gNyg95WXHkefQjracnwVj1yHfhm2").child("messages").child(key).child("timeStamp").setValue(String.valueOf(System.currentTimeMillis()));

                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(ChatSeller.this,"Error Sending the message",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case 101:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Chat Message", chatList.get(item.getGroupId()).getMessage());
                if(clipboard != null)
                    clipboard.setPrimaryClip(clip);
                return true;
            case 102:
                deleteMessage(item.getGroupId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void deleteMessage(final int position) {
        Toast.makeText(getApplicationContext(), chatList.get(position).getKey(), Toast.LENGTH_SHORT).show();
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chats");
        String id = user.getUid();

        chatReference.child(id).child("messages").child(chatList.get(position).getKey()).removeValue();
    }

}
