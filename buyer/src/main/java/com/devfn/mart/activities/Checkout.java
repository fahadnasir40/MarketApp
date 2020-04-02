package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.CartItemInterface;
import com.devfn.common.model.OrderModel;
import com.devfn.common.model.User;
import com.devfn.mart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Checkout extends AppCompatActivity {

    private Button confirmOrder,backButton;
    private OrderModel orderModel;
    private TextView orderNo,orderDate,totalPrice;
    private TextView name,address,contactNumber,changeDeliveryDetailsButton;

    private DatabaseReference databaseReference,ordersReference,cartReference;
    private User currentUser;
    private ProgressDialog progressDialog,orderProgressDialog;
    private int newOrderNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        progressDialog = new ProgressDialog(this);
        orderProgressDialog = new ProgressDialog(this);
        orderProgressDialog.setMessage("Confirming Order. Please wait.");

        orderNo = findViewById(R.id.checkout_order_no);
        orderDate = findViewById(R.id.checkout_order_date);
        totalPrice = findViewById(R.id.checkout_totalAmount);

        name = findViewById(R.id.checkout_delivery_name);
        address = findViewById(R.id.checkout_delivery_address);
        contactNumber = findViewById(R.id.checkout_delivery_contact);
        changeDeliveryDetailsButton = findViewById(R.id.checkout_change_delivery_details);

        backButton = findViewById(R.id.btn_back_checkout);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Checkout.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        confirmOrder = findViewById(R.id.btn_confirm_order);
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderProgressDialog.show();
                writeOrderInDb();
            }
        });



        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        orderModel = (OrderModel) bundle.getSerializable("order_object");

        if(orderModel!=null){

            final int min = 12;
            final int max = 100000;
            newOrderNumber = new Random().nextInt((max - min) + 1) + min;

            totalPrice.setText("Total Amount: "+ getFormattedNumber(orderModel.getTotalOrderPrice()));
            orderNo.setText("Order Number: M-LHR-"+ newOrderNumber);

            Calendar c = Calendar.getInstance();
            System.out.println("Current time =&gt; "+c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            String formattedDate = df.format(c.getTime());
            orderDate.setText("Order Date: "+formattedDate);

        }


        changeDeliveryDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(currentUser!=null){
                    Bundle bundle2 = new Bundle();

                    bundle2.putSerializable("call_object","Call From Checkout");
                    bundle2.putSerializable("order_object",orderModel);
                    bundle2.putSerializable("call_user_object",currentUser);

                    Intent intent1 = new Intent(Checkout.this,DeliveryDetails.class);
                    intent1.putExtras(bundle2);
                    startActivity(intent1);
                }

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        ordersReference = FirebaseDatabase.getInstance().getReference("orders");
        cartReference = FirebaseDatabase.getInstance().getReference("cart");

        progressDialog.setMessage("Loading Delivery Details.");
        progressDialog.show();

        loadDeliveryDetails();
    }



    void writeOrderInDb(){
        if(newOrderNumber != -1){
            String orderNo = orderModel.getOrderNo();
            orderModel.setOrderNo("M-LHR-"+String.valueOf(newOrderNumber));
            ordersReference.child(orderNo).setValue(orderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        discardCart();
                }
            });

        }

    }


    void setDeliveryDetails(){
        name.setText("Name: "+currentUser.getFullName());
        contactNumber.setText("Contact Number: " + currentUser.getContactNo());
        address.setText("Address: "+currentUser.getAddress()+"\nPostal Code: "+currentUser.getPostalCode()+"\n\nCity: "+currentUser.getCity());
    }


    void loadDeliveryDetails()
    {
        databaseReference.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUser = dataSnapshot.getValue(User.class);
                if(currentUser!=null){
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    setDeliveryDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Checkout.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    String getFormattedNumber(int number){
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        return myFormat.format(number);
    }

    void discardCart() {
        if (cartReference != null)
            cartReference.child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        if(orderProgressDialog.isShowing())
                            orderProgressDialog.dismiss();

                        Intent intent = new Intent(Checkout.this,MainActivity.class);

                        finish();
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(),"Your order has been placed",Toast.LENGTH_LONG).show();

                    }
                }
            });
    }

}
