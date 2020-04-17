package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.devfn.mart.R;
import com.devfn.mart.adapters.ItemAdapter;
import com.devfn.common.model.PostItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<PostItem> postsList;
    private Button cartButton;
    private Toolbar toolbar;
    private TextView signInButton;
    ProgressDialog progressDialog;

    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    TextView noItemsText;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(FirebaseAuth.getInstance().getUid() != null)
        {

            signInButton.setVisibility(View.GONE);
            cartButton.setVisibility(View.VISIBLE);

            getMenuInflater().inflate(R.menu.menu_toolbar, menu);
            MenuItem userMenuItem = menu.findItem(R.id.menu_welcome_user);
            userMenuItem.setTitle("Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            return super.onCreateOptionsMenu(menu);

        }


        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_log_out:
                logout();
                return true;
            case R.id.menu_orders:
                Intent intent = new Intent(MainActivity.this,Order.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){

        FirebaseAuth.getInstance().signOut();

        databaseReference.removeEventListener(childEventListener);

        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);


        cartButton = findViewById(R.id.btn_cart);

        postsList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        noItemsText = findViewById(R.id.tv_home_no_items);
        signInButton = findViewById(R.id.tv_home_sign_in);


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

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
                finish();
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
        if(progressDialog.isShowing())
            progressDialog.dismiss();

    }

}

