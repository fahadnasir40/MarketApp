package com.devfn.mart.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.devfn.mart.R;
import com.devfn.mart.model.PostItem;

import java.text.NumberFormat;

public class Post extends AppCompatActivity {

    private ImageView image;
    private TextView name,description,price,deliveryTime,quantity;
    private Button backButton,cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        PostItem post =  (PostItem) bundle.getSerializable("post_object");

        image = findViewById(R.id.post_img);
        name = findViewById(R.id.post_name);
        price = findViewById(R.id.post_price);
        deliveryTime = findViewById(R.id.post_delivery_time);
        quantity = findViewById(R.id.post_quantity);
        description = findViewById(R.id.post_description);

//        image.setImageResource(post.getPhoto());
        name.setText(post.getName());
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true); // this will also round numbers, 3

        price.setText("Rs. "+ myFormat.format(post.getPrice()));
        quantity.setText("Items Available: "+Integer.toString(post.getQuantity()));
        deliveryTime.setText("Delivery Time: "+post.getDeliveryTime());
        description.setText(post.getDescription());


        backButton = findViewById(R.id.btn_back_post);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(Post.this,MainActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        cartButton = findViewById(R.id.btn_cart);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Post.this,Cart.class);
                startActivity(intent);
            }
        });
    }
}
