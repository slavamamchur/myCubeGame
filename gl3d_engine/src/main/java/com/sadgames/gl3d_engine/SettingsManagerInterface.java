package com.sadgames.gl3d_engine;

import com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GraphicsQuality;

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
