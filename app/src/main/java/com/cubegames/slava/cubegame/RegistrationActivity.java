package com.cubegames.slava.cubegame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.UserEntity;

public class RegistrationActivity extends BaseActivityWithMenu {
    private BroadcastReceiver mRegisterBroadcastReceiver = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mUserNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        setupActionBar();

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mUserNameView = (EditText) findViewById(R.id.user_name);
        Button mRegisterButton = (Button) findViewById(R.id.register_new_user_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });

        registerRestApiResponseReceivers();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_logout).setVisible(false);
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mRegisterBroadcastReceiver);
        super.onDestroy();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void registerRestApiResponseReceivers() {
        mRegisterBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgress();

                String error = intent.getStringExtra(RestApiService.EXTRA_REGISTRATION_RESPONSE_TEXT);
                if(!TextUtils.isEmpty(error)) {
                    Toast.makeText(RegistrationActivity.this, error, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(RegistrationActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }

            }
        };
        IntentFilter updateIntentFilter = new IntentFilter(
                RestApiService.ACTION_REGISTRATION_RESPONSE);
        updateIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mRegisterBroadcastReceiver, updateIntentFilter);
    }

    private void registerNewUser(){
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String userName = mUserNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress();

            UserEntity params = new UserEntity();
            params.setEmail(email);
            params.setUserName(userName);
            params.setUserPass(password);
            params.setLanguage("RU");

            RestApiService.startActionRegistration(getApplicationContext(), params);
        }
    }

    private boolean isEmailValid(@SuppressWarnings("UnusedParameters") String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }
}
