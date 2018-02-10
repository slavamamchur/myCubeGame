package com.sadgames.dicegame;

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
