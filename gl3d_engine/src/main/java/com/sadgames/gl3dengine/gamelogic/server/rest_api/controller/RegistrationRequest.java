package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.WebServiceException;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.UserEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_REGISTER;

public class RegistrationRequest extends BaseController<ErrorEntity, GenericCollectionResponse>{

    private static final int HTTP_METHOD_POST = 1;

    private UserEntity user;

    public RegistrationRequest(UserEntity user, SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_REGISTER,
                ErrorEntity.class,
                GenericCollectionResponse.class,
                new HashMap<>(),
                HTTP_METHOD_POST,
                sysUtilsWrapper);

        this.user = user;
    }

    public void doRegister() throws WebServiceException {
        sendPostRequest("", user);
    }

}
