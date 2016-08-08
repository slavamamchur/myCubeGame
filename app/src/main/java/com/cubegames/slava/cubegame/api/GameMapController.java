package com.cubegames.slava.cubegame.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.media.Image;

import com.cubegames.slava.cubegame.model.GameMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME_MAP;

public class GameMapController extends AbstractHttpRequest<GameMap> {
    private String authToken;

    public GameMapController(String authToken, Context ctx) {
        super(URL_GAME_MAP, GameMap.class, HttpMethod.GET, ctx);

        this.authToken = authToken;
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, authToken);

        if(entity == null)
            return getHeaderParamsHttpEntity(params);
        else
            return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    //TODO: download pic from stream
    public Picture getMapImage(GameMap map){
        Picture pic = new Picture(null);
        return null;
    }
}
