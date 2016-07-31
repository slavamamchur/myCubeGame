package com.cubegames.slava.cubegame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.widget.Toast;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.UserEntity;

public class RegistrationActivity extends BaseActivityWithMenu {
    private BroadcastReceiver mRegisterBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        setupActionBar();
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

                //TODO: Implement registration response processing
                String error = intent.getStringExtra(RestApiService.EXTRA_REGISTRATION_RESPONSE_TEXT);
                Toast.makeText(RegistrationActivity.this, error, Toast.LENGTH_LONG).show();

            }
        };
        IntentFilter updateIntentFilter = new IntentFilter(
                RestApiService.ACTION_REGISTRATION_RESPONSE);
        updateIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mRegisterBroadcastReceiver, updateIntentFilter);
    }

    //TODO: implement registration request
    private void registerNewUser(){
        showProgress();

        UserEntity params = new UserEntity();

        RestApiService.startActionRegistration(getApplicationContext(), params);
    }
}
