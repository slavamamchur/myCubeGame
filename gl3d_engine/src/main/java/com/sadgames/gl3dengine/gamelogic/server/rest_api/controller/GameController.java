package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME;

public class GameController extends BaseController<GameEntity, GameCollectionResponse>{

    public GameController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_GAME, GameEntity.class, GameCollectionResponse.class, null, sysUtilsWrapper);
    }

    /*public void removePoint(GameEntity game, int index){
        removeChild(game.getId(), AbstractGamePoint.urlForActionName(), index);
    }

    public void addPoint(GameEntity game, NewPointRequest point){
        addChild(game.getId(), AbstractGamePoint.urlForActionName(), point);
    }*/
}
