package com.sadgames.dicegame.platforms.android.sysutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sadgames.dicegame.gl3d_engine.utils.ISettingsManager;

public final class AndroidSettingsManager implements ISettingsManager {

    public static final String PARAM_AUTH_TOKEN = "authToken";
    public static final String PARAM_USER_NAME = "userName";
    public static final String PARAM_USER_PASS = "userPass";
    public static final String BASE_URL = "baseUrl";
    public static final String DEFAULT_BASE_URL_VALUE = "http://10.0.2.2:8080/engine";
    public static final String PARAM_STAY_LOGGED_IN = "stayLoggedIn";

    private static final Object lockObject = new Object();
    private static AndroidSettingsManager instance = null;
    private SharedPreferences settings = null;

    public AndroidSettingsManager(Context context) {
        settings = AndroidSysUtils.getDefaultSharedPrefs(context);
    }

    public static AndroidSettingsManager getInstance(Context context){
        synchronized (lockObject) {
            return instance != null ? instance : new AndroidSettingsManager(context);
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

    @Override
    public String getAuthToken(){
        return safeReadProperty(PARAM_AUTH_TOKEN, "");
    }

    @Override
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(safeReadProperty(PARAM_AUTH_TOKEN, ""));
    }

    @Override
    public void setAuthToken(String authToken){
        safeWriteProperty(PARAM_AUTH_TOKEN, authToken);
    }

    @Override
    public String getWebServiceUrl(){
        return safeReadProperty(BASE_URL, DEFAULT_BASE_URL_VALUE);
    }

    @Override
    public boolean isStayLoggedIn(){
        return safeReadProperty(PARAM_STAY_LOGGED_IN, true);
    }

    @Override
    public String getUserName(){
        return safeReadProperty(PARAM_USER_NAME, "");
    }

    @Override
    public void setUserName(String userName){
        safeWriteProperty(PARAM_USER_NAME, userName);
    }

    @Override
    public String getUserPass(){
        return safeReadProperty(PARAM_USER_PASS, "");
    }

    @Override
    public void setUserPass(String userPass){
        safeWriteProperty(PARAM_USER_PASS, userPass);
    }
}
