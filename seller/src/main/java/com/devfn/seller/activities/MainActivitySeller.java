package com.devfn.seller.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.PostItem;
import com.devfn.seller.R;
import com.devfn.seller.adapters.ItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivitySeller extends AppCompatActivity {


    private Button createPostButton,ordersButton;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<PostItem> postsList;
    private ItemAdapter itemAdapter;
    private DatabaseReference databaseReference;
    private TextView noItemsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        toolbar = findViewById(R.id.home_toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);
        noItemsText = findViewById(R.id.tv_home_no_items);
        createPostButton = findViewById(R.id.btn_create_post);
        ordersButton = findViewById(R.id.btn_home_orders);

        postsList = new ArrayList<>();
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivitySeller.this,CreatePost.class);
                startActivity(intent);
            }
        });

        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivitySeller.this,Orders.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.home_items_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        checkSessionInfo();
        readAllPosts();


    }

    void checkSessionInfo(){

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            // Get new Instance ID token
                            final String currentToken = task.getResult().getToken();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                            String uid = FirebaseAuth.getInstance().getUid();
                            ref.child(uid).child("device_token").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        String token = dataSnapshot.getValue(String.class);
                                        if(!currentToken.equals(token)){
                                            Toast.makeText(getApplicationContext(),"Session Expired. Please Sign In",Toast.LENGTH_SHORT).show();
                                            logout(true);
                                        }
                                    }
                                    else if(FirebaseAuth.getInstance().getUid() != null){
                                        Toast.makeText(getApplicationContext(),"Session Expired. Please Sign In",Toast.LENGTH_SHORT).show();
                                        logout(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
        }

    }



    void readAllPosts(){

        postsList.clear();

        itemAdapter = new ItemAdapter(postsList, MainActivitySeller.this);
        recyclerView.setAdapter(itemAdapter);

        Query query = databaseReference.orderByChild("datePosted");
        query.addChildEventListener(childEventListener);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            PostItem postItem = dataSnapshot.getValue(PostItem.class);

            postsList.add(0,postItem);      //adding item to the beginning


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
            Toast.makeText(MainActivitySeller.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(FirebaseAuth.getInstance().getUid() != null){
            getMenuInflater().inflate(R.menu.menu_toolbar, menu);
            return super.onCreateOptionsMenu(menu);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_chats:
                Intent intent = new Intent(this,Messages.class);
                startActivity(intent);
                return true;
            case R.id.menu_log_out:
                logout(false);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(boolean sessionExpired){


        if(!sessionExpired){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            String uid = FirebaseAuth.getInstance().getUid();
            ref.child(uid).child("device_token").removeValue();
        }

        databaseReference.removeEventListener(childEventListener);

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(MainActivitySeller.this, LoginSeller.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
