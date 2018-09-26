package com.app.eventify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eventify.Utils.DatabaseUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileFragment extends Fragment {

    private static final String TAG = "MainActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef ,imgRef;
    private FloatingActionButton addImg;
    private static final int CAPTURE_IMAGE_REQUEST = 1, GALLERY = 2;
    private StorageReference mStorageRef;
    private Uri selectedImageUri;
    private Bitmap selectedBitmap;
    private TextView mName, mClass, mRollno, mEmail, mMobileNo;
    private ImageView profilePic;
    private String user_id;
    private ProgressBar progressBar;
    private  ScrollView scrollView;

    File photoFile = null;
    String mCurrentPhotoPath = "";
    Uri photoURI = null;
    private byte[] mUploadBytes;

    public ProfileFragment() {

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

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }


    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void captureImage()
    {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            try {

                photoFile = createImageFile();
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(getContext(),
                            "com.app.eventify.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                }
            } catch (Exception ex) {
                displayMessage(getContext(),ex.getMessage());
            }
        }else
        {
            displayMessage(getContext(),"Null");
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 0) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                captureImage();
//            }
//        }
//    }

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
                    captureImage();
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

    private void loadImg(String img)
    {
        Bitmap bm=((BitmapDrawable)profilePic.getDrawable()).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), bm);
        RequestOptions options = new RequestOptions()
                .placeholder(d)
                .diskCacheStrategy(DiskCacheStrategy.DATA);
        if(progressBar.getVisibility() == View.GONE)
            progressBar.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(img)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(profilePic);
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

                if(!userImg.equals("notSet"))
                    loadImg(userImg);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void uploadProfilePhoto(Uri uri)
    {
        BackgroundImageTask resize = new  BackgroundImageTask(null);
        resize.execute(uri);
    }
    private void uploadProfilePhoto(Bitmap bitmap)
    {
        Uri uri = null;
        BackgroundImageTask resize = new  BackgroundImageTask(bitmap);
        resize.execute(uri);
    }
    private void executeUploadTask()
    {

            mStorageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference imagePath = mStorageRef.child("Users").child(user_id).child("profile.jpg");
            imagePath.putBytes(mUploadBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Log.d(TAG, "onSuccess: uri= "+ uri.toString());
                        String downloadUri = uri.toString();
                        imgRef.setValue(downloadUri);
                        loadImg(downloadUri);
                        displayMessage(getContext(),"Profile pic Updated!");
                    }
                });
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_REQUEST)
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                uploadProfilePhoto(myBitmap);
                }
                else if (requestCode == GALLERY) {
                    selectedImageUri = data.getData();
                    uploadProfilePhoto(selectedImageUri);
                }
            }
        }

     public class BackgroundImageTask extends AsyncTask<Uri,Integer,byte[]>
     {
         Bitmap mBitmap;
         public BackgroundImageTask(Bitmap bitmap)
         {
             if(bitmap != null)
                 this.mBitmap = bitmap;
         }
         @Override
         protected void onPreExecute()
         {
             super.onPreExecute();
             progressBar.setVisibility(View.VISIBLE);
         }

         @Override
         protected byte[] doInBackground(Uri... params)
         {
             if(mBitmap == null)
             {
                 try
                 {
                     mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),params[0]);
                 }catch (IOException e)
                 {
                     Log.e(TAG, "doInBackground: IOException" + e.getMessage() );
                 }
             }
             byte[] bytes = null;
             try
             {
                 bytes = getBytesFromBitmap(mBitmap);
             }
             catch (FileNotFoundException e)
             {
                 e.printStackTrace();
             }
             return bytes;
         }

         @Override
         protected void onPostExecute(byte[] bytes) {
             super.onPostExecute(bytes);
             mUploadBytes = bytes;
             executeUploadTask();
         }
     }

     public byte[] getBytesFromBitmap(Bitmap bitmap) throws FileNotFoundException {
         int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
         Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         scaled.compress(Bitmap.CompressFormat.JPEG , 25, stream);
         return stream.toByteArray();
     }
}

