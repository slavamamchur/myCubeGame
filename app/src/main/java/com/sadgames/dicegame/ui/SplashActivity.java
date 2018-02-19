package com.sadgames.dicegame.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;
import com.sadgames.sysutils.platforms.android.AndroidDiceGameUtilsWrapper;

import static com.sadgames.dicegame.logic.client.GameConst.ACTION_PING_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_BOOLEAN_RESULT;

public class SplashActivity extends AppCompatActivity {

    public static  final int HIDE_DELAY = 3000;

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private BroadcastReceiver mPingBroadcastReceiver = null;
    private SysUtilsWrapperInterface sysUtilsWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        registerRestApiResponceReceivers();

        sysUtilsWrapper = AndroidDiceGameUtilsWrapper.getInstance(getApplicationContext());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        checkAuthentication();
    }

    private void checkAuthentication(){
        if(sysUtilsWrapper.iGetSettingsManager().isLoggedIn())
            RestApiService.startActionPing(this);
        else {
            cls = LoginActivity.class;
            delayedHide(HIDE_DELAY);
        }
    }

    private Class<?> cls = LoginActivity.class;
    private void registerRestApiResponceReceivers() {
        mPingBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cls = !intent.getBooleanExtra(EXTRA_BOOLEAN_RESULT, false) ? LoginActivity.class : /*GameListActivity*/MainActivity.class;
                if(!intent.getBooleanExtra(EXTRA_BOOLEAN_RESULT, false)){
                    sysUtilsWrapper.iGetSettingsManager().setAuthToken("");
                }
                delayedHide(HIDE_DELAY);
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_PING_RESPONSE);
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
