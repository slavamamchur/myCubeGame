package com.sadgames.dicegame.game.server.rest_api;

import android.text.TextUtils;

import com.sadgames.dicegame.game.server.rest_api.model.responses.PingResponse;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.sadgames.dicegame.game.server.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.dicegame.game.server.rest_api.RestConst.URL_PING;

public class PingRequest extends AbstractHttpRequest<PingResponse> {

    public PingRequest(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_PING, PingResponse.class, HttpMethod.GET, sysUtilsWrapper);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        return getHeaderAndObjectParamsHttpEntity(params);
    }

    public boolean doPing(){
        boolean result;

        try{
            result = !TextUtils.isEmpty(getResponse().getName());
        }
        catch (Exception e){
            result = false;
        }

        return result;
    }
}
