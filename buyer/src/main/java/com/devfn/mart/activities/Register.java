package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devfn.mart.R;
import com.devfn.common.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {



    private FirebaseAuth firebaseAuth;
    private User user;

    ProgressBar progressBar;

    TextInputLayout nameLayout;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextInputLayout confirmPasswordLayout;

    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText password;
    TextInputEditText confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        nameLayout = findViewById(R.id.textInputLayoutName);
        emailLayout = findViewById(R.id.textInputLayoutEmail);
        passwordLayout = findViewById(R.id.textInputLayoutPassword);
        confirmPasswordLayout = findViewById(R.id.textInputLayoutConfirmPassword);

        name = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        progressBar = findViewById(R.id.register_progressbar);

        firebaseAuth = FirebaseAuth.getInstance();

        checkValidations();

        Button regBtn = findViewById(R.id.registerBtn);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                registerUser();
            }
        });


    }

    void registerUser(){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(!(netInfo != null && netInfo.isConnected())){
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!name.getText().toString().equals("") && !email.getText().toString().equals("")
                && !password.getText().toString().equals("")
                && !confirmPassword.getText().toString().equals("")
                && password.getText().toString().equals(confirmPassword.getText().toString()) &&
                checkValidations())
            createUser();
        else {
            progressBar.setVisibility(View.GONE);
            if(name.getText().toString().equals(""))nameLayout.setError("You need to enter full name");
            if(email.getText().toString().equals(""))emailLayout.setError("You need to enter email");
            if(password.getText().toString().equals(""))passwordLayout.setError("You need to enter password");
            if(confirmPassword.getText().toString().equals(""))confirmPasswordLayout.setError("You need to re-enter password");
            if(!password.getText().toString().equals(confirmPassword.getText().toString()))confirmPasswordLayout.setError("Does not match Password");

            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
        }
    }

    boolean checkValidations()
    {
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(name.getText().toString().equals("")){
                        nameLayout.setError("You need to enter full name");
                    }
                    else {
                        nameLayout.setError(null);
                    }
                }
                else {
                    nameLayout.setError(null);
                }
            }
        });

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
                    else if(password.getText().toString().length() < 6){
                        passwordLayout.setError("Password should be at least 6 characters");
                    }
                    else {
                        passwordLayout.setError(null);
                    }
                }else {
                    passwordLayout.setError(null);
                }
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(confirmPassword.getText().toString().equals("")){
                        confirmPasswordLayout.setError("You need to re-enter password");
                    }
                    else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                        confirmPasswordLayout.setError("Does not match Password");
                    }
                    else {
                        confirmPasswordLayout.setError(null);
                    }
                }else {
                    confirmPasswordLayout.setError(null);
                }
            }
        });
        if(nameLayout.getError() == null && emailLayout.getError() == null
                && passwordLayout.getError() == null && confirmPasswordLayout.getError() == null){
            return true;
        }else {
            return false;
        }
    }

    void createUser(){

        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) { registerUerToDatabase(); }else
                {
                    if(progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void loginUser(String userName,String password){

        firebaseAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(user != null){

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.getText().toString()).build();
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Bundle bundle = new Bundle();
                                                    String callObject = "call_from_register_account";
                                                    bundle.putSerializable("call_user_object", user);
                                                    bundle.putSerializable("call_intent", callObject);

                                                    Intent intent = new Intent(Register.this, DeliveryDetails.class);
                                                    intent.putExtras(bundle);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                                                    startActivity(intent);
                                                    finish();
                                                } else {

                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void registerUerToDatabase()
    {
        DatabaseReference mDatabase;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth.getCurrentUser().reload();
        String Name = name.getText().toString();

        user = new User();

        user.setFullName(Name);
        user.setEmail(firebaseAuth.getCurrentUser().getEmail());
        user.setUserId(firebaseAuth.getCurrentUser().getUid());


        mDatabase.child("roles").child("buyers").child(FirebaseAuth.getInstance().getUid()).setValue(FirebaseAuth.getInstance().getUid());
        mDatabase.child("users").child(FirebaseAuth.getInstance().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loginUser(email.getText().toString(),password.getText().toString());
                }
            }
        });

    }


}
