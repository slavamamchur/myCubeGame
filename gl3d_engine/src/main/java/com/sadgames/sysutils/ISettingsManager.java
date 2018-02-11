package com.sadgames.sysutils;

public interface ISettingsManager {

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
