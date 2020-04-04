package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.devfn.mart.R;
import com.google.firebase.auth.FirebaseAuth;


public class SplashScreen extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }

}
