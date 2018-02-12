package com.sadgames.gl3d_engine;

public interface SettingsManagerInterface {

    String getAuthToken();
    boolean isLoggedIn();
    void setAuthToken(String authToken);
    String getWebServiceUrl(String defaultValue);
    boolean isStayLoggedIn();
    String getUserName();
    void setUserName(String userName);
    String getUserPass();
    void setUserPass(String userPass);

}
