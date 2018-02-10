package com.sadgames.dicegame.rest_api;

import com.sadgames.dicegame.rest_api.model.ErrorEntity;
import com.sadgames.dicegame.rest_api.model.UserEntity;
import com.sadgames.gl3d_engine.utils.ISysUtilsWrapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import static com.sadgames.dicegame.rest_api.RestConst.URL_REGISTER;

public class RegistrationRequest extends AbstractHttpRequest<ErrorEntity>{

    private UserEntity user;

    public RegistrationRequest(UserEntity user, ISysUtilsWrapper sysUtilsWrapper) {
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
