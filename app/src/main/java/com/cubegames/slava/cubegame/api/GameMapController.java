package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.CollectionResponseGameMap;
import com.cubegames.slava.cubegame.model.GameMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME_MAP_IMAGE_SIMPLE;
import static com.cubegames.slava.cubegame.api.RestConst.URL_LIST;

public class GameMapController extends AbstractHttpRequest<GameMap> {

    public GameMapController(Context ctx) {
        super(GameMap.ACTION_NAME, GameMap.class, HttpMethod.GET, ctx);
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

            ResponseEntity<CollectionResponseGameMap> responseEntity =
                    restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), CollectionResponseGameMap.class);

            return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public byte[] getMapImage(GameMap map) throws WebServiceException {
        return  getBinaryData(map, RestConst.URL_GAME_MAP_IMAGE);
    }

    public String uploadMapImage(GameMap map, String fileName) throws WebServiceException {
        return uploadFile(map, "mapid", URL_GAME_MAP_IMAGE_SIMPLE, fileName);
    }
}
