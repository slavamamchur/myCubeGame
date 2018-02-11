package com.sadgames.gl3d_engine.utils;

public interface ISettingsManager {

    String getAuthToken();
    boolean isLoggedIn();
    void setAuthToken(String authToken);
    String getWebServiceUrl();
    boolean isStayLoggedIn();
    String getUserName();
    void setUserName(String userName);
    String getUserPass();
    void setUserPass(String userPass);

}
