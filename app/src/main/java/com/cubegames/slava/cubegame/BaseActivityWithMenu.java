package com.cubegames.slava.cubegame;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.AuthToken;

public class BaseActivityWithMenu extends AppCompatActivity {

    private BroadcastReceiver mPingBroadcastReceiver = null;
    private BroadcastReceiver mLoginBroadcastReceiver = null;
    private ProgressDialog progressDialog = null;

    private boolean isFirstResume;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.menu_exit:
                finish();
                return true;
            case R.id.menu_logout:
                doLogout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLogout(){
        SettingsManager.getInstance(getApplicationContext()).setAuthToken("");
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    protected void registerRestApiResponseReceivers() {
        mPingBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!intent.getBooleanExtra(RestApiService.EXTRA_BOOLEAN_RESULT, false))
                    if (!SettingsManager.getInstance(getApplicationContext()).isStayLoggedIn()) {
                        hideProgress();
                        doLogout();
                    }
                    else
                        doRelogin();
            }
        };

        IntentFilter intentFilter = new IntentFilter(RestApiService.ACTION_PING_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mPingBroadcastReceiver, intentFilter);


        mLoginBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgress();

                AuthToken response = intent.getParcelableExtra(RestApiService.EXTRA_LOGIN_RESPONSE_OBJECT);
                if (response.getId() != null)
                    SettingsManager.getInstance(getApplicationContext()).setAuthToken(response.getId());
                else
                    doLogout();
            }
        };
        intentFilter = new IntentFilter(RestApiService.ACTION_LOGIN_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mLoginBroadcastReceiver, intentFilter);
    }

    private void doRelogin(){
        RestApiService.startActionLogin(getApplicationContext(), SettingsManager.getInstance(getApplicationContext()).getUserName(),
                SettingsManager.getInstance(getApplicationContext()).getUserPass());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        isFirstResume = true;

        registerRestApiResponseReceivers();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (!isFirstResume) {
            checkAuthentication();
        }
        else {
            isFirstResume = false;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mPingBroadcastReceiver);
        unregisterReceiver(mLoginBroadcastReceiver);

        super.onDestroy();
    }

    protected void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
    }

    protected void hideProgress(){
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void checkAuthentication(){
        if(SettingsManager.getInstance(getApplicationContext()).isLoggedIn()) {
            showProgress();

            RestApiService.startActionPing(this);
        }
    }
}
