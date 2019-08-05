package com.lubowa.travelmantics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class CreateDealActivity extends AppCompatActivity {
    private static final int PICTURE_RESULT = 42;
    private StorageReference storageReference;
    private String imageUrl;
    private AppCompatImageView uploadedImage;
    private AppCompatEditText title, description, price;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);
        uploadedImage = (AppCompatImageView) findViewById(R.id.uploaded_image);
        title = (AppCompatEditText) findViewById(R.id.add_title);
        description = (AppCompatEditText) findViewById(R.id.add_description);
        price = (AppCompatEditText) findViewById(R.id.add_price);
        progressDialog = new ProgressDialog(this){
            @Override
            public void onBackPressed() {
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        };
        progressDialog.setCancelable(false);

    }

    public void uploadImage(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(intent.createChooser(intent, "insert Picture"), PICTURE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK ){
            imageUri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadedImage.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }



        }
    }

    public void saveDeal(View v){
        if(title.getText() == null || description.getText() == null || price.getText() == null || imageUri == null){
            Toast.makeText(this, "Please provide all the required details", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setMessage("saving deal");
            progressDialog.show();
            storageReference = FirebaseStorage.getInstance().getReference().child("dealImages").child(imageUri.getLastPathSegment());
            /*storageReference.putFile(imageUri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageUrl = storageReference.getDownloadUrl().toString();
                            progressDialog.cancel();
                            progressDialog.dismiss();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("properties");
                            databaseReference.push().setValue(new Property(title.getText().toString(),description.getText().toString(),price.getText().toString(),imageUrl));
                            Toast.makeText(CreateDealActivity.this, "Image added at "+imageUrl, Toast.LENGTH_SHORT).show();
                        }
                    }
            ).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateDealActivity.this, "Upload error :"+e+" \nPlease try again", Toast.LENGTH_SHORT).show();
                        }
                    }
            );*/


            Task<UploadTask.TaskSnapshot> uploadTask = storageReference.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressDialog.cancel();
                        Uri downloadUri = task.getResult();
                        imageUrl = downloadUri.toString();
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("properties");
                        databaseReference.push().setValue(new Property(title.getText().toString(),description.getText().toString(),price.getText().toString(),imageUrl));
                        progressDialog.cancel();
                        progressDialog.dismiss();
                        Toast.makeText(CreateDealActivity.this, "Image added at "+imageUrl, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateDealActivity.this, "Upload error \nPlease try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }
}
