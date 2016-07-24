package com.cubegames.slava.cubegame.api;

import com.cubegames.slava.cubegame.model.LoginResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.*;

public class LoginRequest extends AbstractHttpRequest<LoginResponse> {

    private String userName;
    private String userPass;

    public LoginRequest(String userName, String userPass) {
        super(URL_LOGIN, LoginResponse.class, HttpMethod.GET);

        this.userName = userName;
        this.userPass = userPass;
    }


    @Override
    protected HttpEntity<?> getHttpEntity() {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_LOGIN_USER_NAME, userName);
        params.put(PARAM_LOGIN_USER_PASS, userPass);

        return getHeaderParamsHttpEntity(params);
    }
}
