package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.OrderModel;
import com.devfn.mart.R;
import com.devfn.mart.adapters.OrderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Order extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> ordersList;
    private DatabaseReference databaseReference;
    private TextView emptyOrders;
    private Button backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        emptyOrders = findViewById(R.id.orders_tv_no_orders_found);
        backButton = findViewById(R.id.btn_back_orders);
        recyclerView = findViewById(R.id.rv_orders);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Order.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ordersList = new ArrayList<>();
        orderAdapter = new OrderAdapter(ordersList,this);
        databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        readOrdersData();
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(ordersList.size()==0)
                if (emptyOrders.getVisibility() == View.GONE)
                    emptyOrders.setVisibility(View.VISIBLE);

        }
    };

    void readOrdersData(){

        ordersList.clear();

        databaseReference.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren()){

                    OrderModel order = ds.getValue(OrderModel.class);
                    ordersList.add(0,order);
                }

                if(emptyOrders.getVisibility() == View.VISIBLE)
                    emptyOrders.setVisibility(View.GONE);

                recyclerView.setAdapter(orderAdapter);
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Order.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        mHandler.sendMessageDelayed(new Message(), 1500);
    }



}
