package com.cubegames.slava.cubegame.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cubegames.slava.cubegame.SettingsManager;
import com.cubegames.slava.cubegame.model.GameMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME_MAP;
import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME_MAP_IMAGE2;

public class GameMapController extends AbstractHttpRequest<GameMap> {
    private String authToken;

    public GameMapController(Context ctx) {
        super(URL_GAME_MAP, GameMap.class, HttpMethod.GET, ctx);

        this.authToken = SettingsManager.getInstance(ctx).getAuthToken();
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

    public Bitmap getMapImage(GameMap map) throws WebServiceException {
        try {
            URL url;
            url = new URL(getBaseUrl() + String.format(URL_GAME_MAP_IMAGE2, map.getId(), SettingsManager.getInstance(ctx).getAuthToken()));

            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();

            return  BitmapFactory.decodeStream(urlcon.getInputStream());

        } catch (Exception e) {
            throw new WebServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Can not download image.");
        }
    }
}
