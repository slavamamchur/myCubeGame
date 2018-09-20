package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.WebServiceException;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.params.StartNewGameRequestParam;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceCollectionResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceStartedResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.IdResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class GameInstanceController extends BaseController<GameInstanceEntity, GameInstanceCollectionResponse>{

    public GameInstanceController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(GameInstanceEntity.ACTION_NAME, GameInstanceEntity.class, GameInstanceCollectionResponse.class, null, sysUtilsWrapper);
    }

    //TODO: add getRequest and postRequest to the controller interface
    public GameInstanceResponse finishInstance(GameInstanceEntity instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_FINISH,
                HttpMethod.GET, getHttpEntity(null), GameInstanceResponse.class, instance.getId()).getBody();
    }

    public IdResponse restartInstance(GameInstanceEntity instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_RESTART,
                HttpMethod.GET, getHttpEntity(null), IdResponse.class, instance.getId()).getBody();
    }

    public GameInstanceStartedResponse startNewInstance(StartNewGameRequestParam request) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_START,
                HttpMethod.POST, getHttpEntity(request), GameInstanceStartedResponse.class).getBody();
    }

    public GameInstanceEntity makeTurn(GameInstanceEntity instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_MOVE,
                HttpMethod.GET, getHttpEntity(null), GameInstanceEntity.class, instance.getId(), instance.getStepsToGo()).getBody();
    }

}
