package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.DbPlayerEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.DBPlayerCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_PLAYER;

public class DBPlayerController extends AbstractController {

    public DBPlayerController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_PLAYER, DbPlayerEntity.class, DBPlayerCollectionResponse.class, HTTP_METHOD_GET, sysUtilsWrapper);
    }

}
