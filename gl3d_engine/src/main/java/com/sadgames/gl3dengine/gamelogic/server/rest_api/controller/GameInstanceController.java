package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.WebServiceException;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.params.StartNewGameRequestParam;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceCollectionResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceStartedResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.IdResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpMethod;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_FINISH;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_MOVE;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_RESTART;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE_START;

public class GameInstanceController extends BaseController<GameInstanceEntity, GameInstanceCollectionResponse>{

    public GameInstanceController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_GAME_INSTANCE, GameInstanceEntity.class, GameInstanceCollectionResponse.class, null, sysUtilsWrapper);
    }

    //TODO: getResponseWithPostParams, getResponseWithGetParams
    public GameInstanceResponse finishInstance(GameInstanceEntity instance) throws WebServiceException {

        return (GameInstanceResponse) getResponseWithParams(URL_GAME_INSTANCE_FINISH,
                HttpMethod.GET, null, GameInstanceResponse.class, instance.getId());
    }

    public IdResponse restartInstance(GameInstanceEntity instance) throws WebServiceException {

        return (IdResponse) getResponseWithParams(URL_GAME_INSTANCE_RESTART,
                HttpMethod.GET, null, IdResponse.class, instance.getId());
    }

    public GameInstanceStartedResponse startNewInstance(StartNewGameRequestParam request) throws WebServiceException {

        return (GameInstanceStartedResponse) getResponseWithParams(URL_GAME_INSTANCE_START,
                HttpMethod.POST, request, GameInstanceStartedResponse.class);
    }

    public GameInstanceEntity makeTurn(GameInstanceEntity instance) throws WebServiceException {

        return (GameInstanceEntity) getResponseWithParams(URL_GAME_INSTANCE_MOVE,
                HttpMethod.GET, null, GameInstanceEntity.class, instance.getId(), instance.getStepsToGo());
    }

}
