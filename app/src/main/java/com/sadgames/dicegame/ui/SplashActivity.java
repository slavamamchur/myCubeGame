package com.sadgames.dicegame.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.backends.android.AndroidPreferences;
import com.sadgames.dicegame.GdxDbAndroid;
import com.sadgames.dicegame.R;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.gl3dengine.glrender.GdxExt;

import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_PING_RESPONSE;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.EXTRA_BOOLEAN_RESULT;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        initGDXLib();
        registerRestApiResponceReceivers();
    }

    private void initGDXLib() {
        GdxExt.preferences = new AndroidPreferences(getSharedPreferences(getPackageName() + "_preferences",
                                                                          Context.MODE_PRIVATE));
        GdxExt.files = new AndroidFiles(this.getAssets(), this.getFilesDir().getAbsolutePath());
        GdxExt.dataBase = new GdxDbAndroid(this);
        GdxExt.audio = new AndroidAudio(this.getApplicationContext(), new AndroidApplicationConfiguration());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        checkAuthentication();
    }

    private void checkAuthentication(){
        if(getSettingsManager().isLoggedIn())
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
                    getSettingsManager().setAuthToken("");
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
