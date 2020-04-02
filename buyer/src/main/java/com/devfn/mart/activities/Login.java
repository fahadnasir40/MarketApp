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
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.mart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private TextInputEditText email;
    private TextInputEditText password;
    private TextView forgetPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        Button btnSignIn = findViewById(R.id.sign_in);
        Button btnSignUp = findViewById(R.id.sign_up);
        Button skip = findViewById(R.id.btn_skip);

        TextView forgotPassword = findViewById(R.id.forgetPassword);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,ResetPassword.class);
                startActivity(intent);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                startActivity(intent);
                finish();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });


        emailLayout = findViewById(R.id.textInputLayoutEmail);
        passwordLayout = findViewById(R.id.textInputLayoutPassword);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgetPassword = findViewById(R.id.forgetPassword);

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

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){

                    firebaseAuth.getCurrentUser().reload(); // reloads user fields, like emailVerified:

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                    finish();
                    startActivity(intent);

                }
                else {
                    password.setError("Invalid Email or Password");
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
