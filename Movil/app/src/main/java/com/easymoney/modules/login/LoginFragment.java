package com.easymoney.modules.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easymoney.R;
import com.easymoney.models.services.Login;
import com.easymoney.modules.main.MainActivity;

/**
 * Created by ulises on 30/12/17.
 */

public class LoginFragment extends LoginContract.Fragment {

    private EditText mEmailView;
    private EditText mPasswordView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        // Set up the login form.
        mEmailView = root.findViewById(R.id.email);
        mPasswordView = root.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button btnLogin = root.findViewById(R.id.email_sign_in_button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preventDoubleClick(new Runnable() {
                    @Override
                    public void run() {
                        attemptLogin();
                    }
                });
            }
        });
        this.getPresenter().subscribe();
        return root;
    }

    @Override
    public void showMain(final int userId, final String userName, final String userType) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userName", userName);
        intent.putExtra("userType", userType);
        startActivity(intent);
        this.getActivity().finishAffinity();
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            getPresenter().attemptLogin(email, password);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().unsubscribe();
    }

    @Override
    public void setPreloadedLogin(Login.Request request) {
        mEmailView.setText(request.getUser());
        mPasswordView.setText(request.getPass());
    }
}
