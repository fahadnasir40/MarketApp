package com.devfn.seller.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devfn.common.model.OrderModel;
import com.devfn.seller.R;
import com.devfn.seller.adapters.OrdersViewPageAdapter;
import com.devfn.seller.fragments.FragmentOrdersAll;
import com.devfn.seller.fragments.FragmentOrdersCancelled;
import com.devfn.seller.fragments.FragmentOrdersDelivered;
import com.devfn.seller.fragments.FragmentOrdersPending;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Orders extends AppCompatActivity {

    private Toolbar toolbar;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button backButton;
    private DatabaseReference ordersReference;
    private List<OrderModel> ordersList;
    private FragmentOrdersAll allFragment;
    private FragmentOrdersPending pendingFragment;
    private FragmentOrdersDelivered deliveredFragment;
    private FragmentOrdersCancelled cancelledFragment;
    OrdersViewPageAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        toolbar = findViewById(R.id.home_toolbar);


        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabs_orders);
        viewPager = findViewById(R.id.view_pager);
        backButton = findViewById(R.id.btn_back_orders);

        ordersList = new ArrayList<>();
        fragmentAdapter = new OrdersViewPageAdapter(getSupportFragmentManager());

        allFragment = new FragmentOrdersAll();
        deliveredFragment = new FragmentOrdersDelivered();
        cancelledFragment = new FragmentOrdersCancelled();
        pendingFragment = new FragmentOrdersPending();

        fragmentAdapter.AddFragment(allFragment,"All Orders");
        fragmentAdapter.AddFragment(pendingFragment,"Pending");
        fragmentAdapter.AddFragment(deliveredFragment,"Delivered");
        fragmentAdapter.AddFragment(cancelledFragment,"Cancelled");

        allFragment = (FragmentOrdersAll) fragmentAdapter.getItem(0);
        pendingFragment = (FragmentOrdersPending) fragmentAdapter.getItem(1);
        deliveredFragment = (FragmentOrdersDelivered) fragmentAdapter.getItem(2);
        cancelledFragment = (FragmentOrdersCancelled) fragmentAdapter.getItem(3);


        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Orders.this,MainActivitySeller.class);
                startActivity(intent);
                finish();
            }
        });


        ordersReference = FirebaseDatabase.getInstance().getReference("orders");


        getOrdersData();
    }

    private void getOrdersData() {
        ordersReference.addChildEventListener(childEventListener);
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            for(DataSnapshot ds: dataSnapshot.getChildren()){

                OrderModel orderModel = ds.getValue(OrderModel.class);
                addItemToFragments(orderModel);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            clearListsOfFragments();
            for(DataSnapshot ds: dataSnapshot.getChildren()){
                OrderModel orderModel = ds.getValue(OrderModel.class);
                addItemToFragments(orderModel);

            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            clearListsOfFragments();
            for(DataSnapshot ds: dataSnapshot.getChildren()){
                OrderModel orderModel = ds.getValue(OrderModel.class);
                addItemToFragments(orderModel);

            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void clearListsOfFragments() {
        allFragment.clearList();
        pendingFragment.clearList();
        deliveredFragment.clearList();
        cancelledFragment.clearList();
    }

    private void addItemToFragments(OrderModel orderModel) {

        allFragment.addItemsInOrder(orderModel);
        switch(orderModel.getOrderStatus()){
            case "Cancelled":
                cancelledFragment.addItemsInOrder(orderModel);
                return;
            case "Pending":
            case "Shipping":
                pendingFragment.addItemsInOrder(orderModel,false);
                return;
            case "Delivered":
                deliveredFragment.addItemsInOrder(orderModel);
                return;
            default:
                return;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
