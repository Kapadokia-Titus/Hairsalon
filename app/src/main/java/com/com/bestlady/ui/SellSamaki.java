package com.com.bestlady.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.com.bestlady.R;
import com.com.bestlady.model.Products;
import com.com.bestlady.utility.PreferenceKeys;
import com.com.bestlady.viewmodel.SellProductViewModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SellSamaki extends AppCompatActivity {
    private static final String TAG = "SellSamaki";
    //firebase auth
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final String ANONYMOUS = "anonymous";
    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String mUsername;
    String imageUrl;
    // Uri indicates, where the image will be picked from
    private Uri filePath;
    private static final int RC_SIGN_IN = 123;
    // initialize view model 
    SellProductViewModel sellSamakiViewModel;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    private TextView fish_name, fish_price, fish_quantity;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Base);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_product);
        intToolbar();
        mUsername = ANONYMOUS;
        mAuth = FirebaseAuth.getInstance();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //init views
        fish_name = findViewById(R.id.ref_fish_name);
        fish_price = findViewById(R.id.ref_fish_price);
        fish_quantity = findViewById(R.id.ref_fish_quantity);
        imageView = findViewById(R.id.upload_image);
        // init view model
        sellSamakiViewModel = ViewModelProviders.of(this).get(SellProductViewModel.class);
        //perform login
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    //user is signed in
                    onSignedInInitialised(user.getDisplayName());
                    // check the loggedin status in the shared preference
                    //Change Activity
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SellSamaki.this);
                    String status= preferences.getString(PreferenceKeys.logged_in_value, "");
                    if(status == null){
                        Random random = new Random();
                        String creatorId = String.format("%04d", random.nextInt(10000));
                        //store id in a shared preference
                        SharedPreferences preference= PreferenceManager.getDefaultSharedPreferences(SellSamaki.this);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString(PreferenceKeys.logged_in_value, creatorId);
                        editor.apply();
                        Log.d(TAG, "onAuthStateChanged: "+creatorId);
                    }
                }else{
                    //user is signed out
                    onSignedOutCleanup();
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void intToolbar() {
        Toolbar toolbar = findViewById(R.id.sell_samaki_toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Fish Details");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode ==RESULT_OK){
            Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();

        }else if (resultCode ==RESULT_CANCELED){
            Toast.makeText(this, "SignIn cancelled", Toast.LENGTH_SHORT).show();
            finish();
        }

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignedInInitialised(String username){

        mUsername = username;
    }
    private void onSignedOutCleanup(){

        mUsername =ANONYMOUS;
    }
    public void selectImage(View view) {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    public void submitButton(View view) {

        
        // step 1 submitting the file path to firebase storage
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            // first we check if the filepath is empty
            if(filePath == null || filePath.equals("")){
                showSnackbar("We require an Image of Your samaki");
            }else{
                ref.putFile(filePath)
                        .addOnSuccessListener(
                                new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(
                                            UploadTask.TaskSnapshot taskSnapshot)
                                    {

                                        // Image uploaded successfully
                                        // Dismiss dialog
                                        progressDialog.dismiss();
                                        showSnackbar("Image Uploaded!!");


                                        // get the image reference
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageUrl = uri.toString();

                                                // pass data to the api
                                                Products products = new Products();
                                                products.setName(fish_name.getText().toString().trim());
                                                products.setCreatorId(String.valueOf(PreferenceKeys.LOGGED_IN_VALUE));
                                                Log.d(TAG, "submitButton: "+PreferenceKeys.LOGGED_IN_VALUE);
                                                products.setImageUrl(imageUrl);
                                                Log.d(TAG, "onSuccess: "+imageUrl); // i want to know the url passed
                                                Double price = Double.valueOf( fish_price.getText().toString());
                                                products.setPrice(price);
                                                int quantity = Integer.parseInt(fish_quantity.getText().toString());
                                                products.setQuantity(quantity);

                                                //step 3, lets submit the data to the data layer
                                                String status = sellSamakiViewModel.uploadFishData(products);
                                                showSnackbar(status);
                                            }
                                        });

                                    }
                                })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {

                                // Error, Image not uploaded
                                progressDialog.dismiss();
                                showSnackbar("Failed " + e.getMessage());

                            }
                        })
                        .addOnProgressListener(
                                new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    // Progress Listener for loading
                                    // percentage on the dialog box
                                    @Override
                                    public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot)
                                    {
                                        double progress
                                                = (100.0
                                                * taskSnapshot.getBytesTransferred()
                                                / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage(
                                                "Uploaded "
                                                        + (int)progress + "%");
                                    }
                                });
            }

        }
    }

    private void showSnackbar(String status) {
        View contextView = findViewById(R.id.main_layout);
        Snackbar.make(contextView, status, Snackbar.LENGTH_LONG).show();
    }


    public void checkMyProducts(View view) {
        startActivity(new Intent(SellSamaki.this, SellersProductsActivity.class));
    }
}