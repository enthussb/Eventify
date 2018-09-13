package com.app.eventify;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class LoginFragment extends Fragment {



    public LoginFragment() {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final TextInputLayout emailWrapper = (TextInputLayout)view.findViewById(R.id.emailWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout)view.findViewById(R.id.passwordWrapper);
        emailWrapper.setHint("Email");
        passwordWrapper.setHint("Password");

        Button linkToRegister = (Button)view.findViewById(R.id.btn_link_register);
        linkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.OpeningContainer,registerFragment,"REGISTER_FRAGMENT")
                                .addToBackStack(null)
                                .commit();
            }
        });


        Button btn = (Button)view.findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
            }
        });
        return view;
    }

}
