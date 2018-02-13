package com.sadgames.sysutils.platforms.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sadgames.gl3d_engine.SettingsManagerInterface;
import com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GraphicsQuality;

public final class AndroidSettingsManager implements SettingsManagerInterface {

    public static final String PARAM_AUTH_TOKEN = "authToken";
    public static final String PARAM_USER_NAME = "userName";
    public static final String PARAM_USER_PASS = "userPass";
    public static final String BASE_URL = "baseUrl";
    public static final String PARAM_STAY_LOGGED_IN = "stayLoggedIn";
    public static final String PARAM_GRAPHICS_QUALITY_LEVEL = "graphicsQualityLevel";

    private static final int DEFAULT_GRAPHICS_QUALITY_LEVEL = GraphicsQuality.HIGH.ordinal();

    private static final Object lockObject = new Object();
    private static AndroidSettingsManager instance = null;
    private SharedPreferences settings = null;

    public AndroidSettingsManager(Context context) {
        settings = AndroidSysUtilsWrapper.getDefaultSharedPrefs(context);
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

    public int safeReadProperty(String name, int defValue){
        synchronized (lockObject) {
            return settings.getInt(name, defValue);
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
    public String getWebServiceUrl(String defaultValue){
        return safeReadProperty(BASE_URL, defaultValue);
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

    @Override
    public GraphicsQuality getGraphicsQualityLevel() {
        return GraphicsQuality.values()[safeReadProperty(PARAM_GRAPHICS_QUALITY_LEVEL, DEFAULT_GRAPHICS_QUALITY_LEVEL)];
    }
}
