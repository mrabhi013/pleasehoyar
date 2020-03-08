package com.example.pleasehoyar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private ProgressBar mProgressCircle;
    ImageView mImageView;
    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;
    Bitmap bitmap;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
private FirebaseStorage mStorage;
ImageView image_view_upload;
private ValueEventListener mDBListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        mRecyclerView = findViewById(R.id.recycler_view);
       mImageView=(ImageView)findViewById(R.id.image_view_upload);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(ImagesActivity.this);
mStorage=FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
      mDBListner=  mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapShot.getValue(Upload.class);
                    upload.setKey(postSnapShot.getKey());
                    mUploads.add(upload);

                }
                mAdapter.notifyDataSetChanged();

mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


    }



    public void onItemClick(int position) {
        //Toast.makeText(this, "Normal Click at position"+position, Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onWhatEverClick(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
            } else {
                saveImage();
            }
        }
        else
        {
            saveImage();
        }
    }

    private void saveImage() {
        bitmap=((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) ;
        File path= Environment.getExternalStorageDirectory();
        File dir=new File(path+"/Firebase/");
        dir.mkdir();
        String imageName=timeStamp+".PNG";
        File file=new File(dir,imageName);
        OutputStream out;
        try{
            out =new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,10,out);
            out.flush();
            out.close();
            Toast.makeText(this, imageName+"Saved into"+dir, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }}

    @Override
    public void onDeleteClick(int position) {
        Upload selectedItem=mUploads.get(position);
        final String selectedKey=selectedItem.getKey();
        StorageReference imageRef=mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
           mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImagesActivity.this, "bye!", Toast.LENGTH_SHORT).show();
            }
        });

        //Toast.makeText(this, "delete image Bro", Toast.LENGTH_SHORT).show();
    }
    public void onDestroy(){
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListner);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:
            {
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    saveImage();

                }
                else
                {
                    Toast.makeText(this, "Enable Permission to save image!", Toast.LENGTH_SHORT).show();
                }
            }



        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

