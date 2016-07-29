package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.params.RegisterRequestParams;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import static com.cubegames.slava.cubegame.api.RestConst.URL_REGISTER;

public class RegistrationRequest extends AbstractHttpRequest<ErrorEntity>{

    private RegisterRequestParams params;

    public RegistrationRequest(RegisterRequestParams params, Context ctx) {
        super(URL_REGISTER, ErrorEntity.class, HttpMethod.POST, ctx);

        this.params = params;
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        return getJsonObjectParamsHttpEntity(params);
    }

    public void doRegister() throws WebServiceException {
        sendRequestWithParams("");
    }

}
