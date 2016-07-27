package com.cubegames.slava.cubegame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.cubegames.slava.cubegame.api.RestApiService;

public class SplashActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private BroadcastReceiver mPingBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        registerRestApiResponceReceivers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        checkAuthentication();
    }

    private void checkAuthentication(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String authToken = settings.getString("authToken", "");

        if(!TextUtils.isEmpty(authToken))
            RestApiService.startActionPing(this, authToken);
        else {
            cls = LoginActivity.class;
            delayedHide(3000);
        }
    }

    private Class<?> cls = LoginActivity.class;
    private void registerRestApiResponceReceivers() {
        mPingBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cls = !intent.getBooleanExtra(RestApiService.EXTRA_BOOLEAN_RESULT, false) ? LoginActivity.class : SettingsActivity.class;
                if(!intent.getBooleanExtra(RestApiService.EXTRA_BOOLEAN_RESULT, false)){
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("authToken", "");
                    editor.commit();
                }
                delayedHide(3000);
            }
        };

        IntentFilter intentFilter = new IntentFilter(RestApiService.ACTION_PING_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mPingBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mPingBroadcastReceiver);

        super.onDestroy();
    }

    private void hide() {
        startActivity(new Intent(getApplicationContext(), cls));
        finish();
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
