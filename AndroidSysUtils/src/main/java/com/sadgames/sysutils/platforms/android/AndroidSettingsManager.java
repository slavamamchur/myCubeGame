package com.sadgames.sysutils.platforms.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GraphicsQuality;
import com.sadgames.sysutils.common.SettingsManagerInterface;

public final class AndroidSettingsManager implements SettingsManagerInterface {

    public static final String PARAM_AUTH_TOKEN = "authToken";
    public static final String PARAM_USER_NAME = "userName";
    public static final String PARAM_USER_PASS = "userPass";
    public static final String BASE_URL = "baseUrl";
    public static final String PARAM_STAY_LOGGED_IN = "stayLoggedIn";
    public static final String PARAM_IN_2D_MODE = "in2DMode";
    public static final String PARAM_GRAPHICS_QUALITY_LEVEL = "graphicsQualityLevel";

    private static final String DEFAULT_GRAPHICS_QUALITY_LEVEL = GraphicsQuality.HIGH.name();

    private static final Object lockObject = new Object();
    private static AndroidSettingsManager instance = null;
    private SharedPreferences settings = null;
    private Context context;

    public AndroidSettingsManager(Context context) {
        this.context = context;
        settings = AndroidSysUtilsWrapper.getDefaultSharedPrefs(context);
    }

    public static AndroidSettingsManager getInstance(Context context){
        synchronized (lockObject) {
            instance = instance != null ? instance : new AndroidSettingsManager(context);
            return instance;
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

    public void safeWriteProperty(String name, boolean value){
        synchronized (lockObject) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(name, value);
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
        return GraphicsQuality.valueOf(safeReadProperty(PARAM_GRAPHICS_QUALITY_LEVEL, DEFAULT_GRAPHICS_QUALITY_LEVEL));
    }

    @Override
    public boolean isIn_2D_Mode() {
        return safeReadProperty(PARAM_IN_2D_MODE, false);
    }

    @Override
    public void setIn_2D_Mode(boolean value) {
        safeWriteProperty(PARAM_IN_2D_MODE, value);
    }
}
