package com.sadgames.dicegame.logic.server.rest_api.controller;

import com.sadgames.dicegame.logic.server.rest_api.WebServiceException;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.NewPointRequest;
import com.sadgames.dicegame.logic.server.rest_api.model.responses.GameCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_LIST;

public class GameController extends AbstractHttpRequest<GameEntity>{

    public GameController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(GameEntity.ACTION_NAME, GameEntity.class, HttpMethod.GET, sysUtilsWrapper);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    @Override
    public Collection getResponseList() throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<GameCollectionResponse> responseEntity =
                restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), GameCollectionResponse.class);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public void removePoint(GameEntity game, int index){
        removeChild(game.getId(), AbstractGamePoint.urlForActionName(), index);
    }

    public void addPoint(GameEntity game, NewPointRequest point){
        addChild(game.getId(), AbstractGamePoint.urlForActionName(), point);
    }
}
