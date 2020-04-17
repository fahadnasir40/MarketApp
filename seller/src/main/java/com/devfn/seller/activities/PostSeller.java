package com.devfn.seller.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.devfn.common.model.CartItem;
import com.devfn.common.model.OrderModel;
import com.devfn.common.model.PostItem;
import com.devfn.seller.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

public class PostSeller extends AppCompatActivity {

    private ImageView image;
    private TextView name,description,price,deliveryTime,quantity;
    private Button backButton,cartButton,alreadyAddedCartButton;
    private PostItem post;
    private ProgressDialog progressDialog;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_seller);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading cart data. Please wait");


        image = findViewById(R.id.post_img);
        name = findViewById(R.id.post_name);
        price = findViewById(R.id.post_price);
        deliveryTime = findViewById(R.id.post_delivery_time);
        quantity = findViewById(R.id.post_quantity);
        description = findViewById(R.id.post_description);
        backButton = findViewById(R.id.btn_back_post);

        post =  (PostItem) bundle.getSerializable("post_object_seller");


        Picasso.with(this).load(post.getPhoto()).fit()
                .into(image);

        name.setText(post.getName());
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3

        price.setText("Rs. "+ myFormat.format(post.getPrice()));
        quantity.setText("Items Available: "+Integer.toString(post.getQuantity()));
        deliveryTime.setText("Delivery Time: "+post.getDeliveryTime() +" days");

        if(!post.getDescription().equals(""))
            description.setText(post.getDescription());



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(PostSeller.this,MainActivitySeller.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("cart");

    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
