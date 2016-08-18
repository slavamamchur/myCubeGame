package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.CollectionResponseGame;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;
import com.cubegames.slava.cubegame.model.points.NewPointRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_LIST;

public class GameController extends AbstractHttpRequest<Game>{

    public GameController(Context ctx) {
        super(Game.ACTION_NAME, Game.class, HttpMethod.GET, ctx);
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
        removeChild(game.getId(), AbstractGamePoint.getActionName(), index);
    }

    public void addPoint(Game game, NewPointRequest point){
        addChild(game.getId(), AbstractGamePoint.getActionName(), point);
    }
}
