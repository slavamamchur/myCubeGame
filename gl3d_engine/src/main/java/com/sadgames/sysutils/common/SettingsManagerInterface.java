package com.sadgames.sysutils.common;

import com.sadgames.gl3dengine.glrender.GLRenderConsts.GraphicsQuality;

public interface SettingsManagerInterface {

    String getAuthToken();
    boolean isLoggedIn();
    void setAuthToken(String authToken);
    boolean isStayLoggedIn();

    String getWebServiceUrl(String defaultValue);

    String getUserName();
    void setUserName(String userName);
    String getUserPass();
    void setUserPass(String userPass);

    GraphicsQuality getGraphicsQualityLevel();
}
