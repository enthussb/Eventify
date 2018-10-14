package com.app.eventify.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.eventify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordFragment extends Fragment
{
    private TextInputEditText editTextResetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    public ResetPasswordFragment()
    {
        // Required empty public constructor
    }
    private boolean checkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    private void resetPassword()
    {
        final String email = editTextResetEmail.getText().toString().trim();
        if(email.isEmpty())
        {
            editTextResetEmail.setError("Enter your registered email Id");
            editTextResetEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextResetEmail.setError("enter a valid email");
            editTextResetEmail.requestFocus();
            return;
        }
        if(!checkConnection())
        {
            Toast.makeText(getActivity(),"Please Check your Internet Connection...",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(getContext(), "We have sent you the instructions to reset your password!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        editTextResetEmail = view.findViewById(R.id.reset_email);
        Button btnReset = view.findViewById(R.id.btn_reset_password);
        Button btngoBack = view.findViewById(R.id.btn_go_back);
        progressBar = view.findViewById(R.id.progressBar_resetPassword);
        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                resetPassword();
            }
        });

        btngoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getFragmentManager().popBackStack();
            }
        });


        return view;
    }

}
