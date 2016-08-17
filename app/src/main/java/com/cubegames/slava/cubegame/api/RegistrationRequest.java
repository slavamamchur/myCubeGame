package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.UserEntity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import static com.cubegames.slava.cubegame.api.RestConst.URL_REGISTER;

public class RegistrationRequest extends AbstractHttpRequest<ErrorEntity>{

    private UserEntity user;

    public RegistrationRequest(UserEntity user, Context ctx) {
        super(URL_REGISTER, ErrorEntity.class, HttpMethod.POST, ctx);

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
