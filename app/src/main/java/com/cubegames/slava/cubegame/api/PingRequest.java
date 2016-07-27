package com.cubegames.slava.cubegame.api;

import android.text.TextUtils;

import com.cubegames.slava.cubegame.model.PingResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_PING;

public class PingRequest extends AbstractHttpRequest<PingResponse> {

    private String authToken;

    protected PingRequest(String authToken) {
        super(URL_PING, PingResponse.class, HttpMethod.GET);
        this.authToken = authToken;
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, authToken);

        return getHeaderParamsHttpEntity(params);
    }

    public boolean doPing(){
        boolean result;

        try{
            result = !TextUtils.isEmpty(getResponse().getName());
        }
        catch (WebServiceException e){
            result = false;
        }

        return result;
    }
}
