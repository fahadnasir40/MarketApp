package com.devfn.seller.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.seller.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LoginSeller extends AppCompatActivity {

        private FirebaseAuth firebaseAuth;

        private TextInputLayout emailLayout;
        private TextInputLayout passwordLayout;
        private DatabaseReference databaseReference,userReference;
        private TextInputEditText email;
        private TextInputEditText password;
        private ProgressBar progressBar;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_seller);


            firebaseAuth = FirebaseAuth.getInstance();

            if(FirebaseAuth.getInstance().getUid() != null){
                Intent intent = new Intent(LoginSeller.this, MainActivitySeller.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                startActivity(intent);
                finish();
            }
            Button btnSignIn = findViewById(R.id.sign_in);

            emailLayout = findViewById(R.id.textInputLayoutEmail);
            passwordLayout = findViewById(R.id.textInputLayoutPassword);

            email = findViewById(R.id.email);
            password = findViewById(R.id.password);


            progressBar = findViewById(R.id.login_progressbar);

            btnSignIn   .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();

                    if(!(netInfo != null && netInfo.isConnected())){
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    loginUser();
                }
            });

            databaseReference = FirebaseDatabase.getInstance().getReference("roles");
            userReference = FirebaseDatabase.getInstance().getReference("users");

        }

        void loginUser()
        {
            if(!email.getText().toString().equals("")
                    && !password.getText().toString().equals("") &&
                    checkValidations())
                loginAttempt();
            else {

                if(email.getText().toString().equals(""))emailLayout.setError("You need to enter email");
                if(password.getText().toString().equals(""))passwordLayout.setError("You need to enter password");
                progressBar.setVisibility(View.GONE);

                //Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        }

        boolean checkValidations()
        {
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        if(email.getText().toString().equals("")){
                            emailLayout.setError("You need to enter Email");
                        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                            emailLayout.setError("Enter valid Email");
                        }
                        else {
                            emailLayout.setError(null);
                        }
                    }else {
                        emailLayout.setError(null);
                    }
                }
            });

            password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        if(password.getText().toString().equals("")){
                            passwordLayout.setError("You need to enter password");
                        }
                        else {
                            passwordLayout.setError(null);
                        }
                    }else {
                        passwordLayout.setError(null);
                    }
                }
            });
            return emailLayout.getError() == null
                    && passwordLayout.getError() == null;
        }

        private void loginAttempt(){

            databaseReference.child("seller").child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String SellerEmail = dataSnapshot.getValue(String.class);
                    if(SellerEmail.equals(email.getText().toString()))
                    {
                        firebaseLogin();
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginSeller.this,"User does not exist",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginSeller.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        }

        void firebaseLogin(){

            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);

                    if(task.isSuccessful()){

                        firebaseAuth.getCurrentUser().reload(); // reloads user fields, like emailVerified:

                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "getInstanceId failed", task.getException());
                                            return;
                                        }

                                        // Get new Instance ID token
                                        String token = task.getResult().getToken();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                                        String uid = FirebaseAuth.getInstance().getUid();
                                        ref.child(uid).child("device_token").setValue(token);
                                    }
                                });


                        Intent intent = new Intent(LoginSeller.this, MainActivitySeller.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                        finish();
                        startActivity(intent);

                    }
                    else {
                        password.setError("Invalid Email or Password");

                    }
                }
            });
        }

}


