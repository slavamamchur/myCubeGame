package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameCollectionResponse;

import java.io.Serializable;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME;

public class GameController extends AbstractController {

    public GameController() {
        super(URL_GAME, GameEntity.class, GameCollectionResponse.class, HTTP_METHOD_GET);
    }

    public void removeChild(String parentId, String childName, int childIndex){
        controller.iRemoveChild(parentId, childName, childIndex);
    }

    public void addChild(String parentId, String childName, Serializable childEntity){
        controller.iAddChild(parentId, childName, childEntity);
    }
}
