package com.sadgames.sysutils.common;

import com.badlogic.gdx.Preferences;
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GraphicsQuality;

public final class GDXSettingsManager implements SettingsManagerInterface {

    public static final String PARAM_AUTH_TOKEN = "authToken";
    public static final String PARAM_USER_NAME = "userName";
    public static final String PARAM_USER_PASS = "userPass";
    public static final String BASE_URL = "baseUrl";
    public static final String PARAM_STAY_LOGGED_IN = "stayLoggedIn";
    public static final String PARAM_IN_2D_MODE = "in2DMode";
    public static final String PARAM_GRAPHICS_QUALITY_LEVEL = "graphicsQualityLevel";

    private static final String DEFAULT_GRAPHICS_QUALITY_LEVEL = GraphicsQuality.HIGH.name();

    private static final Object lockObject = new Object();
    private static GDXSettingsManager instance = null;
    private Preferences settings;

    public GDXSettingsManager(SysUtilsWrapperInterface sysUtils) {
        settings = sysUtils.iGetDefaultSharedPrefs();
    }

    public static GDXSettingsManager getInstance(SysUtilsWrapperInterface sysUtils){
        synchronized (lockObject) {
            instance = instance != null ? instance : new GDXSettingsManager(sysUtils);
            return instance;
        }
    }

    @SuppressWarnings("unused")
    public Preferences getSettings(){
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
            settings.putString(name, value);
            settings.flush();
        }
    }

    public void safeWriteProperty(String name, boolean value){
        synchronized (lockObject) {
            settings.putBoolean(name, value);
            settings.flush();
        }
    }

    @Override
    public String getAuthToken(){
        return safeReadProperty(PARAM_AUTH_TOKEN, "");
    }

    @Override
    public boolean isLoggedIn() {
        String str = safeReadProperty(PARAM_AUTH_TOKEN, "");
        return !(str == null || str.length() == 0);
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
