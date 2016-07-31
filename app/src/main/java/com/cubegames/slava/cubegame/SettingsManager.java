package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public final class SettingsManager {
    public static final String PARAM_AUTH_TOKEN = "authToken";
    public static final String PARAM_USER_NAME = "userName";
    public static final String PARAM_USER_PASS = "userPass";

    private static final Object lockObject = new Object();
    private static SettingsManager instance = null;
    private SharedPreferences settings = null;
    private Context mCtx;

    public SettingsManager(Context ctx) {
        mCtx = ctx;
        settings = getDefaultSharedPreferences(ctx);
    }

    public static SettingsManager getInstance(Context ctx){
        synchronized (lockObject) {
            return instance != null ? instance : new SettingsManager(ctx);
        }
    }

    @SuppressWarnings("unused")
    public SharedPreferences getSettings(){
        synchronized (lockObject) {
            return settings;
        }
    }

    public String safeReadProperty(String name, String defValue){
        synchronized (lockObject) {
            return settings.getString(name, defValue);
        }
    }

    public boolean safeReadProperty(String name, boolean defValue){
        synchronized (lockObject) {
            return settings.getBoolean(name, defValue);
        }
    }

    public void safeWriteProperty(String name, String value){
        synchronized (lockObject) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(name, value);
            editor.apply();
        }
    }

    public String getAuthToken(){
        return safeReadProperty(PARAM_AUTH_TOKEN, "");
    }
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(safeReadProperty(PARAM_AUTH_TOKEN, ""));
    }
    public void setAuthToken(String authToken){
        safeWriteProperty(PARAM_AUTH_TOKEN, authToken);
    }
    public String getWebServiceUrl(){
        return safeReadProperty(mCtx.getString(R.string.pref_key_web_service_url), mCtx.getString(R.string.pref_default_web_service_url));
    }

    public boolean isStayLoggedIn(){
        return safeReadProperty(mCtx.getString(R.string.pref_key_stay_logged), true);
    }

    public String getUserName(){
        return safeReadProperty(PARAM_USER_NAME, "");
    }
    public void setUserName(String userName){
        safeWriteProperty(PARAM_USER_NAME, userName);
    }
    public String getUserPass(){
        return safeReadProperty(PARAM_USER_PASS, "");
    }
    public void setUserPass(String userPass){
        safeWriteProperty(PARAM_USER_PASS, userPass);
    }
}
