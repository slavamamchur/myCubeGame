package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.AuthTokenEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;

import java.util.HashMap;
import java.util.Map;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PARAM_LOGIN_USER_NAME;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PARAM_LOGIN_USER_PASS;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_LOGIN;

public class LoginRequest extends AbstractController {

    public LoginRequest(String userName, String userPass) {
        super(URL_LOGIN, AuthTokenEntity.class, GenericCollectionResponse.class, HTTP_METHOD_GET);

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_LOGIN_USER_NAME, userName);
        params.put(PARAM_LOGIN_USER_PASS, userPass);
        controller.iSetParams(params);
    }

    public AuthTokenEntity doLogin() {
        return (AuthTokenEntity) controller.iGetResponse("");
    }
}
