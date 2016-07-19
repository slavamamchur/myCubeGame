package com.cubegames.slava.cubegame.api;

/**
 * Created by Slava Mamchur on 19.07.2016.
 */
public class LoginRequest extends AbstractHttpGetRequest<LoginResponse> {

    public LoginRequest(String userName, String userPass) {
        super(RestConst.getBaseUrl() + RestConst.URL_LOGIN, LoginResponse.class);

        getmParams().put("user-name", userName);
        getmParams().put("user-pass", userPass);
    }
}
