package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.UserEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;

import java.util.HashMap;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_REGISTER;

public class RegistrationRequest extends AbstractController {

    private UserEntity user;

    public RegistrationRequest(UserEntity user) {
        super(URL_REGISTER,
                ErrorEntity.class,
                GenericCollectionResponse.class,
                HTTP_METHOD_POST);

        this.user = user;
        controller.iSetParams(new HashMap<>());
    }

    public void doRegister() {
        controller.iSendPOSTRequest("", user);
    }

}
