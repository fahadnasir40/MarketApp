package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.CartItem;
import com.devfn.common.model.OrderModel;
import com.devfn.common.model.CartItemInterface;
import com.devfn.mart.R;
import com.devfn.mart.adapters.CartAdapter;
import com.devfn.common.model.PostItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Cart extends AppCompatActivity implements CartItemInterface {

    private List<PostItem> postsList;
    private List<CartItem> cartItemList;
    private Button backButton, checkoutButton;
    private TextView totalPrice, discardAll;
    private ProgressDialog progressDialog;
    private OrderModel orderModel;
    private int totalAmount = 0;

    DatabaseReference databaseReference;
    DatabaseReference postReference;
    CartAdapter cartAdapter;


    private boolean layoutGone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        postsList = new ArrayList<>();
        cartItemList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        totalPrice = findViewById(R.id.item_cart_total_price);
        discardAll = findViewById(R.id.tv_btn_discard_cart);
        checkoutButton = findViewById(R.id.cart_checkout);

        RecyclerView recyclerView = findViewById(R.id.rv_cart);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(postsList,cartItemList, this);
        recyclerView.setAdapter(cartAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("cart");
        postReference = FirebaseDatabase.getInstance().getReference("posts");


        readCartData();


        backButton = findViewById(R.id.btn_back_cart);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        totalPrice.setText("Rs. " + totalAmount);


        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkQuantityOrdered()){

                    if(orderModel!=null){
                        Bundle bundle2 = new Bundle();

                        bundle2.putSerializable("order_object",orderModel);

                        Intent intent = new Intent(Cart.this, Checkout.class);
                        intent.putExtras(bundle2);
                        startActivity(intent );
                    }

                }
                else{
                    cartAdapter.notifyDataSetChanged();
                    Toast.makeText(Cart.this,"Item not available",Toast.LENGTH_SHORT).show();
                }
            }
        });


        discardAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        emptyLayout();
        progressDialog.show();
    }



    boolean checkQuantityOrdered(){

        for(CartItem cartItem:cartItemList){
            for(PostItem postItem:postsList){
                if(cartItem.getPostId().equals(postItem.getPostId())){

                    if(cartItem.getQuantityOrdered() > postItem.getQuantity())
                        return false;

                }

            }
        }

        return true;
    }



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                emptyLayout();

            }
        }
    };

    void readCartData() {

        progressDialog.setMessage("Loading Cart. Please wait.");
        postsList.clear();
        cartItemList.clear();


        mHandler.sendMessageDelayed(new Message(), 2000);

        if (databaseReference != null) {
            databaseReference.child(FirebaseAuth.getInstance().getUid()).addChildEventListener(childEventListener);
        }

    }

    private  ChildEventListener childEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            orderModel = dataSnapshot.getValue(OrderModel.class);

            HashMap<String, CartItem> map = orderModel.getItems();

            Collection<CartItem> values = map.values();
            cartItemList.addAll(values);
            getPostsData(values,false);

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            totalAmount = 0;
            cartItemList.clear();
            orderModel = dataSnapshot.getValue(OrderModel.class);
            HashMap<String,CartItem> map = orderModel.getItems();

            Collection<CartItem> cart = map.values();
            cartItemList.addAll(cart);

            for(CartItem cartItem:cart){
                for(PostItem postItem:postsList){
                    if(cartItem.getPostId().equals(postItem.getPostId())){
                        totalAmount += postItem.getPrice() * cartItem.getQuantityOrdered();
                        break;
                    }

                }
            }
            setTotalPrice();

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    void getPostsData(Collection<CartItem> values, final boolean childChanged){

        List<CartItem> cartItemList = new ArrayList<>(values);

        for(int i = 0;i<values.size();i++){
            try{

                postReference.orderByKey().equalTo(cartItemList.get(i).getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            PostItem post = ds.getValue(PostItem.class);

                            if(!postsList.contains(post) && !childChanged){
                                postsList.add(post);
                            }

                            if(childChanged ){
                                postsList.add(post);
                                totalAmount += (post.getPrice() * orderModel.getQuantityOrdered(post));

                            }

                        }

                        if(!childChanged) {

                            totalAmount = orderModel.getTotalOrderPrice();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            if (layoutGone && orderModel != null)
                                showLayout();
                        }

                        setTotalPrice();
                        cartAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Cart.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }
               });
            }
            catch (Exception e){
                discardCart();
            }

        }


    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Discard All Items");

        builder.setMessage("This will remove all the items from the cart.");

        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();

                discardCart();
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


    void emptyLayout() {

        if (postsList.size() == 0) {


            LinearLayout linearLayout = findViewById(R.id.Ll_cart);
            linearLayout.setVisibility(View.GONE);
            checkoutButton.setVisibility(View.GONE);
            discardAll.setVisibility(View.GONE);

            LinearLayout linearLayout1 = findViewById(R.id.ll_item_not_found);
            linearLayout1.setVisibility(View.VISIBLE);
            layoutGone = true;

        }

    }

    void showLayout() {

        if (postsList.size() != 0 && layoutGone) {

            LinearLayout linearLayout = findViewById(R.id.Ll_cart);
            linearLayout.setVisibility(View.VISIBLE);
            checkoutButton.setVisibility(View.VISIBLE);
            discardAll.setVisibility(View.VISIBLE);

            LinearLayout linearLayout1 = findViewById(R.id.ll_item_not_found);
            linearLayout1.setVisibility(View.GONE);
            layoutGone = false;
        }

    }


    @Override
    public void RemoveItemFromCart(final PostItem post) {

        if (orderModel != null) {

            databaseReference.child(FirebaseAuth.getInstance().getUid()).child(orderModel.getOrderNo()).child("items").child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        databaseReference.child(FirebaseAuth.getInstance().getUid()).child(orderModel.getOrderNo()).child("totalOrderPrice").setValue(totalAmount - post.getPrice());
                        totalAmount -= (post.getPrice() * orderModel.getQuantityOrdered(post));

                        totalPrice.setText("Rs. " + totalAmount);
                        postsList.remove(post);
                        cartAdapter.notifyDataSetChanged();
                        if (postsList.size() == 0) {
                            discardCart();
                        }
                        setTotalPrice();
                    }
                }
            });
        }
    }

    @Override
    public void RefreshTotal(PostItem postItem){

        if(orderModel!=null){

            databaseReference.child(FirebaseAuth.getInstance().getUid()).child(orderModel.getOrderNo())
                    .child("items").child(postItem.getPostId()).child("quantityOrdered")
                    .setValue(orderModel.getQuantityOrdered(postItem)).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        databaseReference.child(FirebaseAuth.getInstance().getUid()).child(orderModel.getOrderNo()).child("totalOrderPrice").setValue(totalAmount);
                    }
                }
            });

        }

    }


    @Override
    public void RefreshItemPrice(final PostItem postItem, final int oldPrice) {
        if(orderModel!=null){

            databaseReference.child(FirebaseAuth.getInstance().getUid()).child(orderModel.getOrderNo())
                    .child("items").child(postItem.getPostId()).child("priceOrdered")
                    .setValue(postItem.getPrice()).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        totalAmount  += postItem.getPrice() - oldPrice;
                        databaseReference.child(FirebaseAuth.getInstance().getUid()).child(orderModel.getOrderNo()).child("totalOrderPrice").setValue(totalAmount);
                        setTotalPrice();
                    }
                }
            });

        }
    }

    void discardCart() {
        if (databaseReference != null)
            databaseReference.child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        if (postsList != null) {
                            postsList.clear();
                            cartAdapter.notifyDataSetChanged();
                            emptyLayout();
                        }
                    }
                }
            });
    }

    String getFormattedNumber(int number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        return myFormat.format(number);

    }



    void setTotalPrice(){
        totalPrice.setText("Rs. "+ getFormattedNumber(totalAmount));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseReference.removeEventListener(childEventListener);
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
