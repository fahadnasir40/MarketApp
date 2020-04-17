package com.devfn.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devfn.common.model.User;
import com.devfn.mart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {


    private DatabaseReference userReference;
    private TextView fullName,email,address,contact,postalCode,city;
    private Button backButton,editProfile;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullName = findViewById(R.id.tv_profile_name);
        email = findViewById(R.id.tv_profile_email);
        address = findViewById(R.id.tv_profile_delivery_address);
        contact = findViewById(R.id.tv_profile_delivery_contact);
        postalCode = findViewById(R.id.tv_profile_delivery_postal_code);
        city = findViewById(R.id.tv_profile_delivery_city);

        backButton = findViewById(R.id.btn_back_profile);
        editProfile = findViewById(R.id.btn_profile_edit_profile);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                startActivity(intent);
                finish();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null){
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("edit_profile_user_object",user);
                    Intent intent = new Intent(UserProfile.this, EditProfile.class);
                    intent.putExtras(bundle2);
                    startActivity(intent );
                }
            }
        });

        userReference = FirebaseDatabase.getInstance().getReference("users");
        userReference.child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    editProfile.setVisibility(View.VISIBLE);
                    setUserDetails(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfile.this,databaseError.getMessage(),Toast.LENGTH_SHORT);
                Intent intent = new Intent(UserProfile.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);    //clear stack
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUserDetails(User user) {

        if(user != null){
            fullName.setText(user.getFullName());
            email.setText(user.getEmail());
            contact.setText(user.getContactNo());
            address.setText("Address:   "+user.getAddress());
            postalCode.setText("Postal Code:  "+ user.getPostalCode());
            city.setText("City:  "+user.getCity());
        }
    }
}
