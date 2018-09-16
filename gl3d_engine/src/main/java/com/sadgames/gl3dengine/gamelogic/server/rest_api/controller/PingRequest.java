package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.PingResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_PING;

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
            result = !(getResponse() == null || getResponse().getName() == null || getResponse().getName().isEmpty());
        }
        catch (Exception e){
            result = false;
        }

        return result;
    }
}
