package com.app.eventify.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.eventify.R;
import com.app.eventify.utils.DatabaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;


public class AddNewsFragment extends Fragment
{
    private ImageView newsPostimage;
    private Button postButton;
    private MultiAutoCompleteTextView newsPostdesc;
    private EditText newsPosttitle;
    private Uri postImageUri = null;
    private ProgressBar progressBar;

    private FirebaseDatabase firebaseDatabase;
    private StorageReference filePath, thumbfilePath;

    private Bitmap compressedImgFile;
    private byte[] mUploadBytes;
    private String desc;

    public AddNewsFragment()
    {
        // Required empty public constructor
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) throws FileNotFoundException
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100, stream);
        return stream.toByteArray();
    }

    private void getImage()
    {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
                .setOutputCompressQuality(25)
                .start(getContext(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK)
            {
                postImageUri = result.getUri();
                newsPostimage.setImageURI(postImageUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_news, container, false);
        newsPostimage = view.findViewById(R.id.news_post_img);
        newsPostdesc = view.findViewById(R.id.news_post_desc);
        newsPosttitle = view.findViewById(R.id.news_post_title);
        postButton = view.findViewById(R.id.btn_post);
        progressBar = view.findViewById(R.id.progressBar_post);

        newsPostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String postTitle = newsPosttitle.getText().toString();
                desc = newsPostdesc.getText().toString();
                final Map<String, Object> postMap = new HashMap<>();

                if (TextUtils.isEmpty(desc))
                    desc = "null";

                if (!TextUtils.isEmpty(postTitle) && postImageUri != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseDatabase = DatabaseUtil.getDatabase();

                    final String key = firebaseDatabase.getReference("News").push().getKey();
                    filePath = FirebaseStorage.getInstance().getReference().child("News").child("post_images").child(key).child("img.jpg");
                    thumbfilePath = FirebaseStorage.getInstance().getReference().child("News").child("post_images").child(key).child("thumbnail.jpg");

                    filePath.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();
                                    File imgFile = new File(postImageUri.getPath());
                                    try {
                                        compressedImgFile = new Compressor(getContext())
                                                .setMaxHeight(200)
                                                .setMaxWidth(200)
                                                .setQuality(15)
                                                .compressToBitmap(imgFile);
                                        mUploadBytes = getBytesFromBitmap(compressedImgFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    thumbfilePath.putBytes(mUploadBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            thumbfilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String downloadThumbUri = uri.toString();
                                                    postMap.put("image_url", downloadUri);
                                                    postMap.put("thumbnail_url", downloadThumbUri);
                                                    postMap.put("title", postTitle);
                                                    postMap.put("description", desc);
                                                    postMap.put("timestamp", System.currentTimeMillis() * -1);
                                                    firebaseDatabase.getReference().child("News").child(key).setValue(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressBar.setVisibility(View.GONE);
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getContext(), "Post Successful", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });

                }
            }
        });
        return view;
    }

}
