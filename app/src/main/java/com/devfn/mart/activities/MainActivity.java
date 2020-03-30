package com.devfn.mart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devfn.mart.R;
import com.devfn.mart.adapters.ItemAdapter;
import com.devfn.mart.model.PostItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<PostItem> postsList;
    private Button cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cartButton = findViewById(R.id.btn_cart);

        postsList = new ArrayList<>();

        PostItem item1 = new PostItem(R.drawable.redmi_note_8_pro,2,42000,234,"Redmi Note 8 pro","Mobile phone","7 days");
        PostItem item2 = new PostItem(R.drawable.sample_image,2,98000,234,"Samsung Galaxy S20 Ultra ","Best Mobile phone","20 days");
        PostItem item3 = new PostItem(R.drawable.redmi_note_8_pro,2,42000,234,"Redmi Note 8 pro","Mobile phone","7 days");
        PostItem item4 = new PostItem(R.drawable.sample_image,2,98000,234,"Samsung Galaxy S20 Ultra","Best Mobile phone","20 days");

        postsList.add(item1);
        postsList.add(item2);
        postsList.add(item3);
        postsList.add(item4);


        RecyclerView recyclerView = findViewById(R.id.home_items_list);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        ItemAdapter itemAdapter = new ItemAdapter(postsList,this);
        recyclerView.setAdapter(itemAdapter);


        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Cart.class);
                startActivity(intent);
            }
        });
    }


}

