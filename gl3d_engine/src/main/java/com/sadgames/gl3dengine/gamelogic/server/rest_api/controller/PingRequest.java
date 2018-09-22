package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.PingResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_PING;

public class PingRequest extends BaseController<PingResponse, GenericCollectionResponse> {

    public PingRequest(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_PING, PingResponse.class, GenericCollectionResponse.class, null, sysUtilsWrapper);
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
