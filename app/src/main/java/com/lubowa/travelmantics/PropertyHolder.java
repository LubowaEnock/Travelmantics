package com.lubowa.travelmantics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class PropertyHolder extends RecyclerView.ViewHolder {
    private View itemView;
    private AppCompatTextView mTitle, mDescription, mPrice;
    private LoaderImageView mImage;
    private Bitmap bmp;

    public PropertyHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setDetails(Context ctx, String title, String description, String price, String imageUrl){
        mTitle = (AppCompatTextView) itemView.findViewById(R.id.title);
        mPrice = (AppCompatTextView) itemView.findViewById(R.id.price);
        mDescription = (AppCompatTextView) itemView.findViewById(R.id.description);
        mImage = (LoaderImageView) itemView.findViewById(R.id.display_picture);
        mTitle.setText(title);
        mPrice.setText(price);
        mDescription.setText(description);

        if(imageUrl != null){
            Picasso.get()
                    .load(imageUrl)
                    .resize(100, 100)
                    .centerCrop()
                    .into(mImage);
        }

    }
}
