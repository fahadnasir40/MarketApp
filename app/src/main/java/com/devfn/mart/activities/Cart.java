package com.devfn.mart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devfn.mart.R;
import com.devfn.mart.adapters.CartAdapter;
import com.devfn.mart.adapters.ItemAdapter;
import com.devfn.mart.model.PostItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {

    private List<PostItem> postsList;
    private Button backButton,checkoutButton;
    private int totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        postsList = new ArrayList<>();

        PostItem item1 = new PostItem(R.drawable.redmi_note_8_pro,2,42000,234,"Redmi Note 8 pro","Mobile phone","7 days");
        PostItem item2 = new PostItem(R.drawable.sample_image,2,98000,234,"Samsung Galaxy S20 Ultra  ","Best Mobile phone","20 days");
        PostItem item3 = new PostItem(R.drawable.redmi_note_8_pro,2,42000,234,"Redmi Note 8 pro","Mobile phone","7 days");
        PostItem item4 = new PostItem(R.drawable.sample_image,2,98000,234,"Samsung Galaxy S20 Ultra ","Best Mobile phone","20 days");
        postsList.add(item1);
        postsList.add(item2);
        postsList.add(item3);
        postsList.add(item4);


        RecyclerView recyclerView = findViewById(R.id.rv_cart);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        CartAdapter cartAdapter = new CartAdapter(postsList,this);
        recyclerView.setAdapter(cartAdapter);

        backButton = findViewById(R.id.btn_back_cart);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Cart.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        TextView totalPrice = findViewById(R.id.item_cart_total_price);

        totalPrice.setText("Rs. "+ computeTotal());

        checkoutButton = findViewById(R.id.cart_checkout);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(Cart.this,Checkout.class);
                startActivity(intent);

            }
        });

    }

    String computeTotal(){
        for(int i = 0;i<postsList.size();i++){
            totalAmount += postsList.get(i).getPrice();
        }

        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3
        // decimal places

        return myFormat.format(totalAmount);
    }

}
