package com.devfn.mart.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devfn.mart.R;

public class Checkout extends AppCompatActivity {

    private Button confirmOrder,backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

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

                Intent intent = new Intent(Checkout.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Your order has been placed",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}
