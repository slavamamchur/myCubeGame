package com.cubegames.slava.cubegame.rest_api;

import com.cubegames.slava.cubegame.gl3d_engine.utils.ISysUtilsWrapper;
import com.cubegames.slava.cubegame.rest_api.model.AuthToken;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.rest_api.RestConst.PARAM_LOGIN_USER_NAME;
import static com.cubegames.slava.cubegame.rest_api.RestConst.PARAM_LOGIN_USER_PASS;
import static com.cubegames.slava.cubegame.rest_api.RestConst.URL_LOGIN;

public class LoginRequest extends AbstractHttpRequest<AuthToken> {

    private String userName;
    private String userPass;

    public LoginRequest(String userName, String userPass, ISysUtilsWrapper sysUtilsWrapper) {
        super(URL_LOGIN, AuthToken.class, HttpMethod.GET, sysUtilsWrapper);

        this.userName = userName;
        this.userPass = userPass;
    }


    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_LOGIN_USER_NAME, userName);
        params.put(PARAM_LOGIN_USER_PASS, userPass);

        return getHeaderAndObjectParamsHttpEntity(params);
    }

    public AuthToken doLogin() throws WebServiceException {
        return getResponse();
    }
}
