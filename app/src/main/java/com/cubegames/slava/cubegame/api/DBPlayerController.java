package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.DbPlayer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_PLAYER;

public class DBPlayerController extends AbstractHttpRequest<DbPlayer> {

    protected DBPlayerController(Context ctx) {
        super(URL_PLAYER, DbPlayer.class, HttpMethod.GET, ctx);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }
}
