package com.app.eventify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void hideKeyboard(Activity activity)
    {
        if (activity.getCurrentFocus() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        final TextInputLayout regUsername  = (TextInputLayout)view.findViewById(R.id.reg_username);
        final TextInputLayout regEmailWrapper = (TextInputLayout)view.findViewById(R.id.reg_emailWrapper);
        final TextInputLayout regPasswordWrapper = (TextInputLayout)view.findViewById(R.id.reg_passwordWrapper);
        final TextInputLayout regRollWrapper = (TextInputLayout)view.findViewById(R.id.reg_rollWrapper);
        final TextInputLayout regPhoneWrapper = (TextInputLayout)view.findViewById(R.id.reg_phoneWrapper);
        Button alreadyRegistered = (Button)view.findViewById(R.id.btn_already_registered);
        Button register =  (Button)view.findViewById(R.id.btn_register);

        regUsername.setHint("Name");
        regEmailWrapper.setHint("Email");
        regPasswordWrapper.setHint("Password");
        regRollWrapper.setHint("Roll Number");
        regPhoneWrapper.setHint("Phone Number");
        setSpinner(view);

        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getFragmentManager().popBackStack();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
            }
        });

        return view;
    }
    public void setSpinner(View view)
    {
        final Spinner spinner = (Spinner)view.findViewById(R.id.spinner_class);

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
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
