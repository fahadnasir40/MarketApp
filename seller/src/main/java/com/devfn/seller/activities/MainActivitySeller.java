package com.devfn.seller.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devfn.seller.R;

public class MainActivitySeller extends AppCompatActivity {


    private Button createPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        createPostButton = findViewById(R.id.btn_create_post);

        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivitySeller.this,CreatePost.class);
                startActivity(intent);
            }
        });

    }
}
