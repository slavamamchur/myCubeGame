package com.cubegames.slava.cubegame.api;

import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.params.RegisterRequestParams;
import com.cubegames.slava.cubegame.model.AuthToken;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import static com.cubegames.slava.cubegame.api.RestConst.URL_REGISTER;

public class RegistrationRequest extends AbstractHttpRequest<ErrorEntity>{

    private RegisterRequestParams params;

    public RegistrationRequest(RegisterRequestParams params) {
        super(URL_REGISTER, ErrorEntity.class, HttpMethod.POST);

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
