package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.mart.R;
import com.devfn.mart.adapters.ItemAdapter;
import com.devfn.common.model.PostItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private List<PostItem> postsList;
    private Button cartButton;
    ProgressDialog progressDialog;

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    TextView noItemsText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cartButton = findViewById(R.id.btn_cart);

        postsList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        noItemsText = findViewById(R.id.tv_home_no_items);


        progressDialog.setMessage("Loading Data. Please wait.");


        recyclerView = findViewById(R.id.home_items_list);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Cart.class);
                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");


        readAllPosts();
    }


    void readAllPosts(){

        postsList.clear();

        itemAdapter = new ItemAdapter(postsList,MainActivity.this);
        recyclerView.setAdapter(itemAdapter);

        Query query = databaseReference.orderByChild("datePosted");

        query.addChildEventListener(childEventListener);

        progressDialog.show();

        mHandler.sendMessageDelayed(new Message(), 4000);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            PostItem postItem = dataSnapshot.getValue(PostItem.class);

            postsList.add(0,postItem);      //adding item to the beginning

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            if(noItemsText.getVisibility() == View.VISIBLE)
                noItemsText.setVisibility(View.GONE);

            itemAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            PostItem post = dataSnapshot.getValue(PostItem.class);

            for(int i = 0;i<postsList.size();i++){

                if(postsList.get(i).getPostId().equals(post.getPostId())){
                    postsList.remove(i);
                    postsList.add(i,post);
                    itemAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            PostItem post = dataSnapshot.getValue(PostItem.class);
            for(int  i =0;i<postsList.size();i++){
                if(postsList.get(i).getPostId().equals(post.getPostId()))
                {
                    postsList.remove(i);
                    itemAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

        }
    };

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();

                if(postsList.size() == 0){
                    noItemsText.setVisibility(View.VISIBLE);
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseReference.removeEventListener(childEventListener);

    }

}

