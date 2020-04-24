package com.devfn.seller.activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devfn.common.model.ChatMessage;
import com.devfn.common.model.ChatModel;
import com.devfn.seller.R;
import com.devfn.seller.adapters.ChatAdapter;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatSeller extends AppCompatActivity {

    private Button backButton;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private EditText editText;
    private ImageButton sendButton;
    private List<ChatMessage> chatList;
    private DatabaseReference databaseRef;
    private ChatModel chatModel;
    private FirebaseUser user;
    private String senderId,senderName;
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
        TextView chatTitle = findViewById(R.id.toolbar_title_chat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatSeller.this,Messages.class);
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
        chatAdapter = new ChatAdapter(chatList,this);
        recyclerView.setAdapter(chatAdapter);

        Intent intent = this.getIntent();
        senderName = intent.getStringExtra("senderName");
        senderId = intent.getStringExtra("senderId");

        chatTitle.setText(senderName);
        databaseRef = FirebaseDatabase.getInstance().getReference("chats");
        if(senderId!=null)
            readData();

    }

    private void readData() {
        databaseRef.child(senderId).addValueEventListener(valueEventListener);
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            chatList.clear();
            if(dataSnapshot.exists()){
                chatModel = dataSnapshot.getValue(ChatModel.class);

                if(chatModel !=null){
                    Map<String, ChatMessage> map = chatModel.getMessages();

                    Collection<ChatMessage> values = map.values();
                    chatList.addAll(values);

                    for(ChatMessage chatMessage:new ArrayList<>(chatList)){
                        if(chatMessage.getStatus().equals("3-2"))
                        {
                            chatList.remove(chatMessage);
                        }
                    }
                    Collections.sort(chatList, new Comparator<ChatMessage>() {
                        public int compare(ChatMessage o1, ChatMessage o2) {
                            Date a = new Timestamp(Long.parseLong(o1.getTimeStamp()));
                            Date b = new Timestamp(Long.parseLong(o2.getTimeStamp()));
                            return b.compareTo(a);
                        }
                    });

                }
                else{
                    chatList.clear();
                }
            }
            else if(chatModel !=null)
                chatModel = null;

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            chatAdapter.notifyDataSetChanged();
            setSeenReceipts();
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    };


    private void setSeenReceipts() {
        if(chatModel !=null){
            DatabaseReference seenReference = FirebaseDatabase.getInstance().getReference("chats");
            for(ChatMessage chatMessage:chatList){
                if(!chatMessage.getChatId().equals(user.getUid())){
                    if(!chatMessage.getIsSeen()){
                        seenReference.child(senderId).child("messages")
                                .child(chatMessage.getKey()).child("isSeen").setValue(true);
                    }
                }
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        if(senderId!=null)
            databaseRef.child(senderId).removeEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(senderId!=null)
            databaseRef.child(senderId).removeEventListener(valueEventListener);
    }

    private void sendMessage() {

        final String message = editText.getText().toString().trim();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats");

        if(!message.equals("")){

            if(chatModel == null){
                String key = ref.child(user.getUid()).push().getKey();
                chatModel = new ChatModel(key,user.getUid(),"2",user.getDisplayName());
                ref.child(senderId).setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
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


    @Override
    protected void onStart() {
        super.onStart();
        if(databaseRef!=null && senderId!=null)
            databaseRef.child(senderId).addValueEventListener(valueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(senderId != null )
            databaseRef.child(senderId).removeEventListener(valueEventListener);
    }

    private void writeMessageInDB(String message) {
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
        final String key = chatRef.child(senderId).child("messages").push().getKey();
        final Map<String,ChatMessage> map = new LinkedHashMap<>();

        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setTimeStamp(String.valueOf(System.currentTimeMillis()));
        chatMessage.setStatus("4");

        chatMessage.setChatId(user.getUid());
        chatMessage.setKey(key);

        map.put(key,chatMessage);
        editText.setText("");

        chatRef.child(senderId).child("messages").child(key).setValue(map.get(key)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatRef.child(senderId).child("messages").child(key).child("status").setValue("3");
                    chatRef.child(senderId).child("messages").child(key).child("timeStamp").setValue(String.valueOf(System.currentTimeMillis()));
                }
                if(task.isCanceled()){
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

        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chats");

        //1. Delete Client
        //2. Delete Seller
        //3. Delivered
        //4. Seen
        //5. 3-1        Delivered & Client Deleted
        //6. 3-2        Delivered & Seller Deleted

        if(chatList.get(position).getStatus().equals("3") || chatList.get(position).getStatus().equals("4") )
         chatReference.child(senderId).child("messages").child(chatList.get(position).getKey()).child("status").setValue("3-2");
        else if(chatList.get(position).getStatus().equals("3-1") )
            chatReference.child(senderId).child("messages").child(chatList.get(position).getKey()).removeValue();
    }

}
