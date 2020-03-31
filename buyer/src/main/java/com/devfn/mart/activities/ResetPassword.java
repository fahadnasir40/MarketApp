package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devfn.mart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private Button backButton;

    TextInputLayout emailLayout;
    TextInputEditText email;
    Button reset;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        backButton = findViewById(R.id.btn_back_reset_password);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPassword.this,Login.class);
                startActivity(intent);
                finish();
            }
        });


        emailLayout = findViewById(R.id.textInputLayoutEmail);
        email = findViewById(R.id.email);
        reset = findViewById(R.id.btn_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

        checkEmail();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().equals("") && checkEmail()) {

                    firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password Reset Link Sent", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPassword.this, Login.class);
                                finish();
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else {
                    if(email.getText().toString().equals(""))emailLayout.setError("You need to enter email");
                }
            }
        });

    }


    boolean checkEmail()
    {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(email.getText().toString().equals("")){
                        emailLayout.setError("You need to enter Email");
                    }if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                        emailLayout.setError("Enter valid Email");
                    }
                    emailLayout.setError(null);

                }else {
                    emailLayout.setError(null);
                }
            }
        });
        if(emailLayout.getError() == null){
            return true;
        }else {
            return false;
        }
    }



}
