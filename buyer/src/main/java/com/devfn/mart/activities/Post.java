package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.CartItem;
import com.devfn.common.model.OrderModel;
import com.devfn.mart.R;
import com.devfn.common.model.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class Post extends AppCompatActivity {

    private ImageView image;
    private TextView name,description,price,deliveryTime,quantity;
    private Button backButton,cartButton,addToCartButton;
    private PostItem post;
    private OrderModel cart;
    private ProgressDialog progressDialog;
    DatabaseReference cartReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


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
        addToCartButton = findViewById(R.id.btn_addToCart);
        cartButton = findViewById(R.id.btn_cart);
        backButton = findViewById(R.id.btn_back_post);


        if(FirebaseAuth.getInstance().getUid() != null){
            cartButton.setVisibility(View.VISIBLE);
        }


        post =  (PostItem) bundle.getSerializable("post_object");


        Picasso.with(this).load(post.getPhoto()).fit()
                .placeholder(R.drawable.ic_add_shopping_cart_black_24dp)
                .error(R.drawable.ic_close_black_24dp)
                .into(image);

        name.setText(post.getName());
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3

        price.setText("Rs. "+ myFormat.format(post.getPrice()));

        if(post.getQuantity() == 0){
            quantity.setText("Out of Stock");
            quantity.setTextColor(getResources().getColor(R.color.warningColor));
            quantity.setTypeface(quantity.getTypeface(), Typeface.BOLD);
            quantity.setTextSize(16);
            deliveryTime.setVisibility(View.GONE);
            addToCartButton.setEnabled(false);
        }
        else
            quantity.setText("Items Available: "+Integer.toString(post.getQuantity()));

        deliveryTime.setText("Delivery Time: "+post.getDeliveryTime() +" days");

        if(!post.getDescription().equals(""))
            description.setText(post.getDescription());



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(Post.this,MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Post.this,Cart.class);
                startActivity(intent);
                finish();
            }
        });


        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(FirebaseAuth.getInstance().getCurrentUser()==null)
                    showDialog();
                else
                    addItemToCart();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("cart");
        readData();
        if(cartButton.getVisibility() == View.VISIBLE)
            checkCartInfo();
    }

    void readData(){

        if(post != null && FirebaseAuth.getInstance().getUid() != null){

            mHandler.sendMessageDelayed(new Message(), 300);
            progressDialog.show();
            final String userId = FirebaseAuth.getInstance().getUid();

            if(databaseReference != null){
                databaseReference.child(userId).addListenerForSingleValueEvent(valueEventListener);
            }
        }
    }

    private void checkCartInfo() {


        if(FirebaseAuth.getInstance().getUid() != null) {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userId = firebaseUser.getUid();
            cartReference = FirebaseDatabase.getInstance().getReference("cart");
            cartReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cartButton.setBackground(getResources().getDrawable(R.drawable.cart_filled));
                    } else {
                        cartButton.setBackground(getResources().getDrawable(R.drawable.ic_shopping_cart_black_24dp));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void addItemToCart(){

        if(post.getQuantity() > 0)
        {
            if(cart == null){
                cart = new OrderModel();
                String key = databaseReference.push().getKey();
                cart.setOrderNo(key);
                cart.setDeliverUserId(FirebaseAuth.getInstance().getUid());
            }


            CartItem cartItem = new CartItem(post.getPostId(),1,post.getPrice());
            cart.addItems(cartItem);
            cart.setTotalOrderPrice(cart.getTotalOrderPrice() + post.getPrice());


            databaseReference.child(FirebaseAuth.getInstance().getUid()).child(cart.getOrderNo()).setValue(cart).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Post.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }

            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        addToCartButton.setText("Item Added to Cart");
                        addToCartButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_black_24dp, 0, 0, 0);
                        progressDialog.dismiss();
                    }
                }});
        }
        else{
            Toast.makeText(Post.this,"No Item Available",Toast.LENGTH_SHORT).show();
        }

    }

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();

                if(cart == null){
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }

            }
        }
    };

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.hasChildren()){
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    cart = ds.getValue(OrderModel.class);
                }

                if(cart != null){
                    if(cart.findPost(post)){
                        addToCartButton.setText("Item Added to Cart");
                        addToCartButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_black_24dp, 0, 0, 0);
                    }
                }

                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

            Toast.makeText(Post.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
        }
    };


    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sign In to your Account");

        builder.setMessage("Please sign in to your account before adding item to the cart.");

        builder.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();

                Intent intent = new Intent(Post.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
