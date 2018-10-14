package com.app.eventify.fragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddEventFragment extends Fragment
{
    private ImageView eventPostimage;
    private Button postButton;
    private MultiAutoCompleteTextView eventPostdesc;
    private EditText eventPosttitle;
    private Uri postImageUri = null;
    private ProgressBar progressBar;
    private EditText startDateText,endDateText;

    private FirebaseDatabase firebaseDatabase;
    private StorageReference filePath;

    private Calendar calendar;

    public AddEventFragment()
    {
        // Required empty public constructor
    }
    private void updateEditText(EditText editText)
    {
        String dataFormat = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat,Locale.US);
        editText.setText(simpleDateFormat.format(calendar.getTime()));
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
               eventPostimage.setImageURI(postImageUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        eventPostimage = view.findViewById(R.id.event_post_img);
        eventPostdesc = view.findViewById(R.id.event_post_desc);
        eventPosttitle = view.findViewById(R.id.event_post_title);
        postButton = view.findViewById(R.id.btn_post);
        progressBar = view.findViewById(R.id.progressBar_event);
        startDateText = view.findViewById(R.id.editTextStartDate);
        endDateText = view.findViewById(R.id.editTextEndDate);
        calendar = Calendar.getInstance();
        eventPostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });



        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEditText(startDateText);
            }
        };

        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener()
        {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEditText(endDateText);
            }
        };



        startDateText.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getContext(), startDate, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getContext(), endDate, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final String postTitle = eventPosttitle.getText().toString();
                final String eventStartDate = startDateText.getText().toString();
                final String eventEndDate = endDateText.getText().toString();
                final String desc = eventPostdesc.getText().toString();
                final Map<String, Object> postMap = new HashMap<>();

                if (!TextUtils.isEmpty(postTitle) && postImageUri != null && !TextUtils.isEmpty(eventStartDate) && !TextUtils.isEmpty(eventEndDate) && !TextUtils.isEmpty(desc))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseDatabase = DatabaseUtil.getDatabase();

                    final String key = firebaseDatabase.getReference("Events").push().getKey();
                    filePath = FirebaseStorage.getInstance().getReference().child("Events").child("event_images").child(key).child("img.jpg");
                    filePath.putFile(postImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    String downloadUri = uri.toString();
                                    postMap.put("img_url", downloadUri);
                                    postMap.put("title", postTitle);
                                    postMap.put("description", desc);
                                    postMap.put("startDate",eventStartDate);
                                    postMap.put("endDate",eventEndDate);
                                    postMap.put("timestamp", System.currentTimeMillis() * -1);
                                    firebaseDatabase.getReference().child("Events").child(key).setValue(postMap).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(getContext(), "Post Successful", Toast.LENGTH_SHORT).show();
                                            }
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
