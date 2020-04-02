package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.CartItem;
import com.devfn.common.model.CartItemInterface;
import com.devfn.common.model.PostItem;
import com.devfn.common.model.User;
import com.devfn.mart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;

public class DeliveryDetails extends AppCompatActivity {


    TextInputLayout contactLayout;
    TextInputLayout addressLayout;
    TextInputLayout postalCodeLayout;
    TextInputLayout cityLayout;

    TextInputEditText contact;
    TextInputEditText address;
    TextInputEditText postalCode;
    TextInputEditText city;

    private User currentUser;
    private Button continueButton,backButton;
//    private TextView skipButton;
    private DatabaseReference databaseReference;
    private String call;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);


        contactLayout = findViewById(R.id.textInputLayoutContactNo);
        addressLayout = findViewById(R.id.textInputLayoutDeliveryAddress);
        postalCodeLayout = findViewById(R.id.textInputLayoutPostalCode);
        cityLayout = findViewById(R.id.textInputLayoutCity);

        contact = findViewById(R.id.et_delivery_contact_no);
        address = findViewById(R.id.et_delivery_address);
        postalCode = findViewById(R.id.et_delivery_postal_code);
        city = findViewById(R.id.et_delivery_city);

        continueButton = findViewById(R.id.btn_delivery_continue);
        backButton = findViewById(R.id.btn_delivery_back);
     //   skipButton = findViewById(R.id.tv_btn_delivery_skip);

        final Intent intent = this.getIntent();
        bundle = intent.getExtras();

        currentUser  =  (User) bundle.getSerializable("call_user_object");

        call = (String) bundle.getSerializable("call_object");
        if(call!=null){
            if(call.equals("Call From Checkout")){
                backButton.setVisibility(View.VISIBLE);
                // bundle.getSerializable("call_object");
            }
        }




        checkValidations();

//        skipButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DeliveryDetails.this,MainActivity.class);
//                finish();
//                startActivity(intent);
//            }
//
//        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(call!=null && call.equals("Call From Checkout")){

                    Intent intent1 = new Intent(DeliveryDetails.this,Checkout.class);
                    intent1.putExtras(bundle);
                    finish();
                    startActivity(intent1);

                }
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        continueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                writeDeliveryDetails();
            }

        });


    }


    void writeDeliveryDetails(){


        if(!contact.getText().toString().equals("") && !address.getText().toString().equals("")
                && !city.getText().toString().equals("")
                && !postalCode.getText().toString().equals("")
                &&
                checkValidations()) {

            currentUser.setContactNo(contact.getText().toString());
            currentUser.setAddress(address.getText().toString());
            currentUser.setPostalCode(postalCode.getText().toString());
            currentUser.setCity(city.getText().toString());
            writeDeliveryDetailsInDB();

        }
        else {

            if(contact.getText().toString().equals(""))contactLayout.setError("Contact Number is required");
            if(address.getText().toString().equals(""))addressLayout.setError("Address is required");
            if(postalCode.getText().toString().equals(""))postalCodeLayout.setError("Postal code is required");
            if(city.getText().toString().equals(""))cityLayout.setError("City name is required");
        }
    }
    
    private void writeDeliveryDetailsInDB() {

        if (databaseReference != null && currentUser != null) {

            databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        if(call!=null && call.equals("Call From Checkout")){

                            Intent intent1 = new Intent(DeliveryDetails.this,Checkout.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack


                            intent1.putExtras(bundle);
                            finish();
                            startActivity(intent1);

                        }
                        else {
                            Intent intent = new Intent(DeliveryDetails.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                            finish();
                            startActivity(intent);
                        }

                    }
                }
            });

        }
    }


    boolean checkValidations()
    {
        contactLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(contact.getText().toString().equals("")){
                        contactLayout.setError("Contact Number is required");
                    }
                    else {
                        contactLayout.setError(null);
                    }
                }
                else {
                    contactLayout.setError(null);
                }
            }
        });


        postalCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(postalCode.getText().toString().equals("")){
                        postalCodeLayout.setError("Postal code is required");
                    }
                }else {
                    postalCodeLayout.setError(null);
                }
            }
        });

        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(address.getText().toString().equals("")){
                        addressLayout.setError("Address is required");
                    }
                    else {
                        addressLayout.setError(null);
                    }
                }else {
                    addressLayout.setError(null);
                }
            }
        });

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(city.getText().toString().equals("")){
                        cityLayout.setError("City name is required");
                    }
                    else {
                        cityLayout.setError(null);
                    }
                }else {
                    cityLayout.setError(null);
                }
            }
        });


        if(contactLayout.getError() == null && postalCodeLayout.getError() == null
                && addressLayout.getError() == null && cityLayout.getError() == null){
            return true;
        }else {
            return false;
        }
    }

}
