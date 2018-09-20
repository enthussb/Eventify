package com.app.eventify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.eventify.Utils.DatabaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterFragment extends Fragment {

    private TextInputEditText editTextName,editTextPassword,editTextEmail,editTextRollNo,editTextMobileNo;
    private Spinner classSpinner;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    private boolean checkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    private void hideKeyboard(Activity activity)
    {
        if (activity.getCurrentFocus() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void registerUser()
    {
        final String name = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String className = classSpinner.getSelectedItem().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String rollNo = editTextRollNo.getText().toString().trim();
        final String mobileNo = editTextMobileNo.getText().toString().trim();
        final String img = "notSet";

        if(name.isEmpty())
        {
            editTextName.setError("name required");
            editTextName.requestFocus();
            return;
        }
        if(email.isEmpty())
        {
            editTextEmail.setError("email required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            editTextPassword.setError("password required");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6)
        {
            editTextPassword.setError("password should be atleast 6 characters long");
            editTextPassword.requestFocus();
            return;
        }
        if(rollNo.isEmpty())
        {
            editTextRollNo.setError("rollNo required");
            editTextRollNo.requestFocus();
            return;
        }
        if(rollNo.length() != 4)
        {
            editTextRollNo.setError("enter valid rollNo");
            editTextRollNo.requestFocus();
            return;
        }
        if(mobileNo.isEmpty())
        {
            editTextMobileNo.setError("mobile number required");
            editTextMobileNo.requestFocus();
            return;
        }
        if(mobileNo.length() != 10)
        {
            editTextMobileNo.setError("enter a valid mobile number");
            editTextMobileNo.requestFocus();
            return;
        }
        if(!checkConnection())
        {
            Toast.makeText(getActivity(),"Please Check your Internet Connection...",Toast.LENGTH_SHORT).show();
            return;
        }
        if(className.equals("Class"))
        {
            TextView errorText = (TextView)classSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select one option");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    UserInformation user = new UserInformation(name,email,className,rollNo,mobileNo,img);
                    DatabaseUtil.getDatabase().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getActivity(),"Data Registered Successfully",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {

                            }
                        }
                    });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Already Registered!.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        editTextName = view.findViewById(R.id.reg_name);
        editTextEmail = view.findViewById(R.id.reg_email);
        editTextPassword = view.findViewById(R.id.reg_password);
        editTextRollNo = view.findViewById(R.id.reg_rollNo);
        editTextMobileNo = view.findViewById(R.id.reg_phoneNo);
        classSpinner = view.findViewById(R.id.spinner_class);
        progressBar = view.findViewById(R.id.progressBar_register);
        Button alreadyRegistered = view.findViewById(R.id.btn_already_registered);
        Button register = view.findViewById(R.id.btn_register);
        setSpinner(view);
        mAuth = FirebaseAuth.getInstance();
        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getFragmentManager().popBackStack();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // hideKeyboard(getActivity());
                registerUser();
            }
        });
        return view;
    }
    public void setSpinner(View view)
    {
        classSpinner = (Spinner)view.findViewById(R.id.spinner_class);

        String[] classes = new String[]{
                "  Class",
                "  TE1",
                "  TE2",
                "  TE3",
                "  TE4"
        };
        final List<String> classList = new ArrayList<>(Arrays.asList(classes));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,classList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        classSpinner.setAdapter(spinnerArrayAdapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    ((TextView) view).setTextColor(Color.BLACK);
                    Toast.makeText
                            (getActivity().getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
