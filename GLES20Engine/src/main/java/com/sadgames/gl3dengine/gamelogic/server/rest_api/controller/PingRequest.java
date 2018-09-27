package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.PingResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_PING;

public class PingRequest extends AbstractController {

    public PingRequest(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_PING, PingResponse.class, GenericCollectionResponse.class, HTTP_METHOD_GET, sysUtilsWrapper);
    }

    public boolean doPing(){
        boolean result;

        try{
            PingResponse response = (PingResponse) controller.iGetResponse("");
            result = !(response == null || response.getName() == null || response.getName().isEmpty());
        }
        catch (Exception e){
            result = false;
        }

        return result;
    }
}
