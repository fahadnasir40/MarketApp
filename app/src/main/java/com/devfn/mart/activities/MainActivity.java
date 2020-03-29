package com.devfn.mart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.devfn.mart.R;
import com.devfn.mart.adapters.ItemAdapter;
import com.devfn.mart.model.PostItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<PostItem> postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        postsList = new ArrayList<>();

        PostItem item1 = new PostItem(1,2,850,234,"Redmi Note 8 pro","Mobile phone","7 days");
        PostItem item2 = new PostItem(1,2,90000,234,"Samsung Galaxy S20 Ultra ","Best Mobile phone","20 days");
        PostItem item3 = new PostItem(1,2,850,234,"Redmi Note 8 pro","Mobile phone","7 days");
        PostItem item4 = new PostItem(1,2,90000,234,"Samsung Galaxy S20 Ultra ","Best Mobile phone","20 days");
        postsList.add(item1);
        postsList.add(item2);
        postsList.add(item3);
        postsList.add(item4);


        RecyclerView recyclerView = findViewById(R.id.home_items_list);



        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        ItemAdapter itemAdapter = new ItemAdapter(postsList,this);
        recyclerView.setAdapter(itemAdapter);
    }


}

