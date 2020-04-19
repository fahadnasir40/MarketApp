package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.CartItem;
import com.devfn.common.model.OrderModel;
import com.devfn.common.model.PostItem;
import com.devfn.mart.R;
import com.devfn.mart.adapters.OrderDetailsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderDetails extends AppCompatActivity {


    private RecyclerView recyclerView;
    private Button backButton;
    private OrderDetailsAdapter orderDetailsAdapter;
    private DatabaseReference databaseReference,orderReference;
    private TextView orderNo,totalPrice,shippingAddress,dateOrdered,cancelOrder,orderStatus,noteTitle,noteDetails;
    private List<PostItem> postsList;
    private List<CartItem> cartItemList;
    private OrderModel order;
    private ProgressDialog progressDialog;


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderNo = findViewById(R.id.order_details_tv_order_no);
        totalPrice = findViewById(R.id.order_details_item_price);
        shippingAddress = findViewById(R.id.order_details_shipping_address);
        dateOrdered = findViewById(R.id.order_details_tv_order_date);
        orderStatus = findViewById(R.id.item_order_details_status);
        noteTitle = findViewById(R.id.item_order_details_status_note_title);
        noteDetails =findViewById(R.id.item_order_details_status_note);

        recyclerView = findViewById(R.id.rv_orders_details);
        backButton = findViewById(R.id.btn_back_orders_details);
        cancelOrder = findViewById(R.id.order_details_cancel_order);

        postsList = new ArrayList<>();
        cartItemList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        order = (OrderModel) bundle.getSerializable("order_object_details");

        if(order!=null){
            setOrderDetails();
            if(order.getOrderStatus().equals("Cancelled"))
                cancelOrder.setVisibility(View.GONE);

        }



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        orderDetailsAdapter = new OrderDetailsAdapter(postsList,cartItemList,this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OrderDetails.this,Order.class);
                startActivity(intent1);
                finish();
            }
        });

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        orderReference = FirebaseDatabase.getInstance().getReference("orders");

        readPostData();
    }


    void setOrderDetails(){

        if(order.getSellerMessage() != null){
            if(noteTitle.getVisibility() == View.GONE){
                noteTitle.setVisibility(View.VISIBLE);
                noteDetails.setVisibility(View.VISIBLE);
            }
            noteDetails.setText(order.getSellerMessage());
        }


        orderNo.setText("Order No. "+ order.getOrderNo());
        totalPrice.setText("Rs. "+ getFormattedNumber(order.getTotalOrderPrice()));
        dateOrdered.setText("Placed on " + order.getOrderDate());
        shippingAddress.setText(order.getDeliveryAddress()+" - "+ order.getDeliveryCity() +" - "+order.getDeliveryPostalCode()+"\n");
        orderStatus.setText(order.getOrderStatus());

    }

    void readPostData(){

        cartItemList.clear();
        postsList.clear();

        if(order!=null){

            Collection<CartItem> items = order.getItems().values();
            cartItemList.addAll(items);

            for(CartItem cartItem:cartItemList){
                databaseReference.child(cartItem.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            PostItem postItem = dataSnapshot.getValue(PostItem.class);
                            postsList.add(postItem);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            recyclerView.setAdapter(orderDetailsAdapter);
            orderDetailsAdapter.notifyDataSetChanged();
        }

    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Cancel Order");

        builder.setMessage("Do you want to cancel your order?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
                cancelOrderInDb();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void cancelOrderInDb() {

        progressDialog.setMessage("Cancelling Order. Please wait.");
        progressDialog.show();

        if(order.getOrderStatus().equals("Pending")){
            order.setOrderStatus("Cancelled");
            orderReference.child(FirebaseAuth.getInstance().getUid()).child(order.getOrderId()).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        Collection<CartItem> items = order.getItems().values();
                        List<CartItem> cartList = new ArrayList<>(items);

                        for(final CartItem cartItem:cartItemList){
                            databaseReference.child(cartItem.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    PostItem postItem = dataSnapshot.getValue(PostItem.class);
                                    if(postItem!=null){
                                        postItem.setQuantity(postItem.getQuantity() + cartItem.getQuantityOrdered());
                                        databaseReference.child(cartItem.getPostId()).setValue(postItem);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }


                    Intent intent = new Intent(OrderDetails.this,Order.class);
                    Toast.makeText(getApplicationContext(),"Order Cancelled",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(intent);
                    finish();
                    }
                    else{
                        progressDialog.dismiss();
                    }

                }
            });
        }

    }


    String getFormattedNumber(int number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        return myFormat.format(number);
    }
}
