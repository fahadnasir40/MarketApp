package com.devfn.seller.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.devfn.common.model.PostItem;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.devfn.seller.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreatePost extends AppCompatActivity {


    TextInputLayout nameLayout;
    TextInputLayout priceLayout;
    TextInputLayout itemQuantityLayout;
    TextInputLayout descriptionLayout;
    TextInputLayout deliveryDaysLayout;


    TextInputEditText name;
    TextInputEditText price;
    TextInputEditText itemQuantity;
    TextInputEditText description;
    TextInputEditText deliveryDays;

    ProgressDialog progressDialog;

    Button postButton,backButton;
    Button uploadImageButton;


    ImageView imageId;

    DatabaseReference databaseReference;
    private Uri imgUri;
    PostItem postItem;

    StorageReference storageReference;
    StorageTask storageTask;
    private static final int PICK_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_post);

        nameLayout = findViewById(R.id.textInputLayoutName);
        priceLayout = findViewById(R.id.textInputLayoutPrice);
        itemQuantityLayout = findViewById(R.id.textInputLayoutTotalItems);
        descriptionLayout = findViewById(R.id.textInputLayoutDescription);
        deliveryDaysLayout = findViewById(R.id.textInputLayoutDeliveryDays);


        name = findViewById(R.id.et_post_name);
        price = findViewById(R.id.et_post_price);
        itemQuantity = findViewById(R.id.et_post_total_items);
        description = findViewById(R.id.et_post_description);
        deliveryDays = findViewById(R.id.et_post_delivery_days);
        backButton = findViewById(R.id.btn_back_create_post);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePost.this,MainActivitySeller.class);
                startActivity(intent);
                finish();
            }
        });

        imageId = findViewById(R.id.image_id);

        progressDialog = new ProgressDialog(this);
        postButton = findViewById(R.id.btn_post_now);

        uploadImageButton = findViewById(R.id.btn_upload_image);

        checkValidations(); //Initializing text fields to detect errors

        progressDialog.setMessage("Creating post. Please Wait");
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                createPost();

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    openFileChooser();
            }
        });

    }

    void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
            imgUri = data.getData();
            Picasso.with(this).load(imgUri).into(imageId);
        }
    }

    String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    void uploadFile(){
        if(imgUri != null){


            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));

            storageTask = fileReference.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    postItem = new PostItem();

                                    postItem.setPhoto(uri.toString());
                                    createPostInDb();

                                    Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    void createPostInDb(){

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");


        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        String userId = FirebaseAuth.getInstance().getUid();
        String key = databaseReference.push().getKey();

        postItem.setPostId(key);
        postItem.setAuthorId(userId);
        postItem.setDatePosted(formattedDate);
        postItem.setName(name.getText().toString());
        postItem.setDescription(description.getText().toString());
        postItem.setDeliveryTime(deliveryDays.getText().toString());
        postItem.setQuantity(Integer.parseInt(itemQuantity.getText().toString()));
        postItem.setPrice(Integer.parseInt(price.getText().toString()));


        databaseReference.child(key).setValue(postItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Intent intent = new Intent(CreatePost.this,MainActivitySeller.class);
                    finish();
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplication(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    void createPost(){


        if(!name.getText().toString().equals("") && !price.getText().toString().equals("")
                && !itemQuantity.getText().toString().equals("")
                && !deliveryDays.getText().toString().equals("")
                &&
                checkValidations()) {
                uploadFile();

        }
        else {

            if(name.getText().toString().equals(""))nameLayout.setError("Item name is required");
            if(price.getText().toString().equals(""))priceLayout.setError("Item price is required");
            if(deliveryDays.getText().toString().equals(""))deliveryDays.setError("You need to maximum days to deliver the item");
            if(itemQuantity.getText().toString().equals(""))itemQuantityLayout.setError("You need to enter total items quantity");
        }
    }


    boolean checkValidations()
    {
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(name.getText().toString().equals("")){
                        nameLayout.setError("Item name is required");
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


        price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(price.getText().toString().equals("")){
                        priceLayout.setError("Item price is required");
                    }
                }else {
                    priceLayout.setError(null);
                }
            }
        });

        itemQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(itemQuantity.getText().toString().equals("")){
                        itemQuantityLayout.setError("You need to enter total items quantity");
                    }
                    else {
                        itemQuantityLayout.setError(null);
                    }
                }else {
                    itemQuantityLayout.setError(null);
                }
            }
        });

        deliveryDays.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(deliveryDays.getText().toString().equals("")){
                        deliveryDaysLayout.setError("You need to maximum days to deliver the item");
                    }
                    else {
                        deliveryDaysLayout.setError(null);
                    }
                }else {
                    deliveryDaysLayout.setError(null);
                }
            }
        });


        if(nameLayout.getError() == null && priceLayout.getError() == null
                && itemQuantityLayout.getError() == null && deliveryDaysLayout.getError() == null){
            return true;
        }else {
            return false;
        }
    }




}