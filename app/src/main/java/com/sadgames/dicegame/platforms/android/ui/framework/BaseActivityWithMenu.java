package com.sadgames.dicegame.platforms.android.ui.framework;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.platforms.android.sysutils.AndroidSysUtilsWrapper;
import com.sadgames.dicegame.platforms.android.ui.LoginActivity;
import com.sadgames.dicegame.platforms.android.ui.SettingsActivity;
import com.sadgames.dicegame.rest_api.RestApiService;
import com.sadgames.dicegame.rest_api.model.AuthToken;
import com.sadgames.dicegame.rest_api.model.ErrorEntity;
import com.sadgames.gl3d_engine.utils.ISysUtilsWrapper;

public abstract class BaseActivityWithMenu extends AppCompatActivity {

    private BroadcastReceiver mBaseBroadcastReceiver = null;
    private ProgressDialog progressDialog = null;
    private ISysUtilsWrapper sysUtilsWrapper;
    private final Handler mAlertHandler = new Handler();
    private boolean isFirstResume;

    public ISysUtilsWrapper getSysUtilsWrapper() {
        return sysUtilsWrapper;
    }

    protected void showError(ErrorEntity error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error.getError())
                .setTitle(R.string.error_title);
        builder.setPositiveButton(R.string.pos_btn_caption, null);

        AlertDialog dialog = builder.create();
        showAlert(dialog);
    }

    protected void showError(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(R.string.error_title);
        builder.setPositiveButton(R.string.pos_btn_caption, null);

        AlertDialog dialog = builder.create();
        showAlert(dialog);
    }

    protected void showAlert(final AlertDialog dialog){
        mAlertHandler.post(new Runnable() {
            @Override
            public void run() {
                 dialog.show();
            }
        });
    }

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
        sysUtilsWrapper.getSettingsManager().setAuthToken("");
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    protected void registerRestApiResponseReceivers() {
        mBaseBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgress();

                handleWebServiceResponseAction(context, intent);
            }
        };

        registerReceiver(mBaseBroadcastReceiver, getIntentFilter());
    }

    protected void unregisterRestApiResponseReceivers() {
        unregisterReceiver(mBaseBroadcastReceiver);
    }

    protected IntentFilter getIntentFilter(){
        IntentFilter intentFilter = new IntentFilter(RestApiService.ACTION_PING_RESPONSE);
        intentFilter.addAction(RestApiService.ACTION_RELOGIN_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        return intentFilter;
    }

    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(RestApiService.ACTION_PING_RESPONSE)) {
            if (!intent.getBooleanExtra(RestApiService.EXTRA_BOOLEAN_RESULT, false))
                if (!sysUtilsWrapper.getSettingsManager().isStayLoggedIn()) {
                    doLogout();
                } else
                    doRelogin();

            return true;
        }
        else if (intent.getAction().equals(RestApiService.ACTION_RELOGIN_RESPONSE)) {
            AuthToken response = intent.getParcelableExtra(RestApiService.EXTRA_LOGIN_RESPONSE_OBJECT);
            if (response.getId() != null)
                sysUtilsWrapper.getSettingsManager().setAuthToken(response.getId());
            else
                doLogout();

            return true;
        }
        else
            return false;
    }

    private void doRelogin(){
        showProgress();

        RestApiService.startActionRelogin(getApplicationContext(), sysUtilsWrapper.getSettingsManager().getUserName(),
                sysUtilsWrapper.getSettingsManager().getUserPass());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sysUtilsWrapper = new AndroidSysUtilsWrapper(getApplicationContext());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setBackgroundDrawable(new ColorDrawable(0));
            //actionBar.setStackedBackgroundDrawable(VectorDrawableCompat.create(this.getResources(), R.drawable.list_ico_cl, this.getTheme()));
            //actionBar.setSplitBackgroundDrawable(null);

            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        isFirstResume = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerRestApiResponseReceivers();
    }

    @Override
    protected void onPause() {
        unregisterRestApiResponseReceivers();

        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if (!isFirstResume) {
            checkAuthentication();
            performUserResumeAction();
        }
        else {
            isFirstResume = false;
        }
    }

    protected void performUserResumeAction() {}

    protected void showProgress(){
        showProgress(R.string.progress_text);
    }

    protected void showProgress(int messageID){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.progress_title);
        progressDialog.setMessage(getResources().getString(messageID));
        progressDialog.show();
    }

    protected void hideProgress(){
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected void toggleActionBarProgress(boolean isVisible) {
        setProgressBarIndeterminateVisibility(isVisible);
        setProgressBarVisibility(isVisible);
    }

    private void checkAuthentication(){
        if (sysUtilsWrapper.getSettingsManager().isLoggedIn()) {
            showProgress();

            RestApiService.startActionPing(this);
        }
    }
}
