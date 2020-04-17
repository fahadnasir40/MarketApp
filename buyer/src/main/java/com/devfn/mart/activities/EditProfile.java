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

import com.devfn.common.model.OrderModel;
import com.devfn.common.model.User;
import com.devfn.mart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends AppCompatActivity {

    private User user;
    private TextInputEditText fullName,address,contact,postalCode,city;
    private TextInputLayout nameLayout,contactLayout,addressLayout,postalCodeLayout,cityLayout;

    private Button saveButton,backButton;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Details. Please Wait");

        nameLayout = findViewById(R.id.textInputLayoutEditName);
        contactLayout = findViewById(R.id.textInputLayoutEditContactNo);
        addressLayout = findViewById(R.id.textInputLayoutEditDeliveryAddress);
        postalCodeLayout = findViewById(R.id.textInputLayoutEditPostalCode);
        cityLayout = findViewById(R.id.textInputLayoutEditCity);

        fullName = findViewById(R.id.et_edit_profile_name);
        address = findViewById(R.id.et_edit_delivery_address);
        contact = findViewById(R.id.et_edit_delivery_contact_no);
        postalCode = findViewById(R.id.et_edit_delivery_postal_code);
        city = findViewById(R.id.et_edit_delivery_city);

        backButton = findViewById(R.id.btn_back_edit_profile);
        saveButton = findViewById(R.id.btn_edit_save);

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        user = (User) bundle.getSerializable("edit_profile_user_object");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this,UserProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                startActivity(intent);
                finish();
            }
        });

        checkValidations();

        if(user !=null){
            setUserData();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDetailsOfUser();

            }
        });

    }

    private void saveDetailsOfUser() {
        if(!fullName.getText().toString().equals("") && !contact.getText().toString().equals("") && !address.getText().toString().equals("")
                && !city.getText().toString().equals("")
                && !postalCode.getText().toString().equals("")
                &&
                checkValidations()) {
            progressDialog.show();
            String previousName = user.getFullName();

            if(!previousName.equals(fullName.getText().toString()))
            {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName.getText().toString()).build();
                firebaseUser.updateProfile(profileUpdates);
            }

            user.setFullName(fullName.getText().toString());
            user.setContactNo(contact.getText().toString());
            user.setAddress(address.getText().toString());
            user.setPostalCode(postalCode.getText().toString());
            user.setCity(city.getText().toString());

            databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Profile Changed Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfile.this,UserProfile.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(task.isCanceled()) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfile.this,"Error saving details",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else {

            if(fullName.getText().toString().equals(""))nameLayout.setError("Full Name is required");
            if(contact.getText().toString().equals(""))contactLayout.setError("Contact Number is required");
            if(address.getText().toString().equals(""))addressLayout.setError("Address is required");
            if(postalCode.getText().toString().equals(""))postalCodeLayout.setError("Postal code is required");
            if(city.getText().toString().equals(""))cityLayout.setError("City name is required");
        }
    }


    private void setUserData() {
        if(user != null){
            fullName.setText(user.getFullName());
            address.setText(user.getAddress());
            postalCode.setText(user.getPostalCode());
            city.setText(user.getCity());
            contact.setText(user.getContactNo());
        }

    }


    boolean checkValidations()
    {
        nameLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(fullName.getText().toString().equals("")){
                        nameLayout.setError("Full Name is required");
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

