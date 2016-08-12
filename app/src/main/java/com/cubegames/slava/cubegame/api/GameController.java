package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;

public class GameController extends AbstractHttpRequest<Game>{

    protected GameController(Context ctx) {
        super(Game.getActionName(), Game.class, HttpMethod.GET, ctx);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    public void removePoint(Game game, int index){
        removeChild(game.getId(), AbstractGamePoint.getActionName(), index);
    }
}
