package com.devfn.mart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devfn.mart.R;
import com.devfn.mart.models.NotificationModel;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<NotificationModel> notificationsList;
    private DatabaseReference databaseReference;
    private Button backButton;
    private TextView clearNotifications;
    private TextView emptyNotifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationsList = new ArrayList<>();

        emptyNotifications = findViewById(R.id.tv_notification_empty_notifications);
        backButton = findViewById(R.id.btn_back_notifications);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Notification.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        emptyNotifications.setVisibility(View.VISIBLE);

    }
}
