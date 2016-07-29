package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public final class SettingsManager {
    public static final String PARAM_AUTH_TOKEN = "authToken";
    public static final String PARAM_WEB_SERVICE_URL = "baseUrl";
    private static final String DEFAULT_WEB_SERVICE_URL = "http://10.0.2.2:8080/engine";

    private static final Object lockObject = new Object();
    private static SettingsManager instance = null;
    private SharedPreferences settings = null;

    public SettingsManager(Context ctx) {
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
    public void setAuthToken(String authToken){
        safeWriteProperty(PARAM_AUTH_TOKEN, authToken);
    }
    public String getWebServiceUrl(){
        return safeReadProperty(PARAM_WEB_SERVICE_URL, DEFAULT_WEB_SERVICE_URL);
    }

}
