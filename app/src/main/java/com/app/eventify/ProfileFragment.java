package com.app.eventify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.eventify.Utils.DatabaseUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class ProfileFragment extends Fragment {

    private static final String TAG = "MainActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef ,imgRef;
    private FloatingActionButton addImg;
    private static final int REQUEST_CAMERA = 1, GALLERY = 2;
    private StorageReference mStorageRef;
    private Uri selectedImageUri;
    private TextView mName, mClass, mRollno, mEmail, mMobileNo;
    private ImageView profilePic;
    private String user_id;
    private ProgressBar progressBar;
    private  ScrollView scrollView;


    private String pictureFilePath;
    private FirebaseStorage firebaseStorage;
    private String deviceIdentifier;


    public ProfileFragment() {

    }
    private void selectImage()
    {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i)
            {
                if(items[i] == "Camera")
                {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent,REQUEST_CAMERA);
                }
                else if(items[i] == "Gallery")
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, GALLERY);
                }
                else
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void getUserProfile()
    {

        mFirebaseDatabase = DatabaseUtil.getDatabase();
        myRef = mFirebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        imgRef = myRef.child("profilePic");
        imgRef.keepSynced(true);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name, email, classname, rollNo, userImg, mobileNo;

                name = dataSnapshot.child("userName").getValue(String.class);
                email = dataSnapshot.child("emailId").getValue(String.class);
                classname = dataSnapshot.child("className").getValue(String.class);
                rollNo = dataSnapshot.child("rollNo").getValue(String.class);
                mobileNo = dataSnapshot.child("mobileNo").getValue(String.class);
                userImg = dataSnapshot.child("profilePic").getValue(String.class);

                mName.setText(name);
                mEmail.setText(email);
                mClass.setText(classname);
                mRollno.setText(rollNo);
                mMobileNo.setText(mobileNo);

                Glide.with(getContext())
                        .load(userImg)
                        .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                        .into(profilePic);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void uploadFirebase(Uri uri)
    {
        if(uri != null)
        {
            mStorageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference imagePath = mStorageRef.child("Users").child(user_id).child("profile.jpg");
            UploadTask uploadTask = imagePath.putFile(uri);
            progressBar.setVisibility(View.VISIBLE);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imagePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        profilePic.setImageURI(selectedImageUri);
                        String imgFirebaseURI = task.getResult().toString();
                        imgRef.setValue(imgFirebaseURI);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
//                Bundle bundle = data.getExtras();
//                final Bitmap bmp = (Bitmap) bundle.get("data");
                //selectedImageUri = getImageUri(getContext(),bmp);
                File imgFile = new File(pictureFilePath);
                if (imgFile.exists()) {
                    profilePic.setImageURI(Uri.fromFile(imgFile));
                    Log.d(TAG, "onActivityResult: " + selectedImageUri);
                    //profilePic.setImageBitmap(bmp);
                } else if (requestCode == GALLERY) {
                    selectedImageUri = data.getData();
                    uploadFirebase(selectedImageUri);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        scrollView = (ScrollView)view.findViewById(R.id.profileView);
        progressBar = view.findViewById(R.id.progressBar_profile_pic);
        profilePic = view.findViewById(R.id.profile_pic);
        mName = view.findViewById(R.id.profile_name);
        mClass = view.findViewById(R.id.profile_class);
        mRollno = view.findViewById(R.id.profile_rollNo);
        mMobileNo = view.findViewById(R.id.profile_mobileNo);
        mEmail = view.findViewById(R.id.profile_email);
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserProfile();
        addImg = view.findViewById(R.id.btn_profile_pic_add);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        return view;
    }
}

