package com.sadgames.dicegame.logic.server.rest_api.controller;

import com.sadgames.dicegame.logic.server.rest_api.WebServiceException;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.UserEntity;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_REGISTER;

public class RegistrationRequest extends AbstractHttpRequest<ErrorEntity>{

    private UserEntity user;

    public RegistrationRequest(UserEntity user, SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_REGISTER, ErrorEntity.class, HttpMethod.POST, sysUtilsWrapper);

        this.user = user;
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        return getHeaderAndObjectParamsHttpEntity(null, entity);
    }

    public void doRegister() throws WebServiceException {
        sendRequestWithParams("", mHttpMethod, user);
    }

}
