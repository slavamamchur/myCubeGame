package com.sadgames.dicegame.rest_api;

import com.sadgames.dicegame.gl3d_engine.utils.ISysUtilsWrapper;
import com.sadgames.dicegame.rest_api.model.CollectionResponseGame;
import com.sadgames.dicegame.rest_api.model.Game;
import com.sadgames.dicegame.rest_api.model.points.AbstractGamePoint;
import com.sadgames.dicegame.rest_api.model.points.NewPointRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.dicegame.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.dicegame.rest_api.RestConst.URL_LIST;

public class GameController extends AbstractHttpRequest<Game>{

    public GameController(ISysUtilsWrapper sysUtilsWrapper) {
        super(Game.ACTION_NAME, Game.class, HttpMethod.GET, sysUtilsWrapper);
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

        ResponseEntity<CollectionResponseGame> responseEntity =
                restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), CollectionResponseGame.class);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public void removePoint(Game game, int index){
        removeChild(game.getId(), AbstractGamePoint.urlForActionName(), index);
    }

    public void addPoint(Game game, NewPointRequest point){
        addChild(game.getId(), AbstractGamePoint.urlForActionName(), point);
    }
}
