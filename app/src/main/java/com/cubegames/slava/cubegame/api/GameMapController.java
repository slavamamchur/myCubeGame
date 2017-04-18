package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.CollectionResponseGameMap;
import com.cubegames.slava.cubegame.model.GameMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.Utils.isBitmapCached;
import static com.cubegames.slava.cubegame.Utils.saveBitmap2DB;
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
        //params.put(PAGE_OFFSET_HEADER, "1");
        //params.put(PAGE_LIMIT_HEADER, "2");
        //params.put(FILTER_BY_NAME, "test_");
        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    @Override
    public Collection getResponseList() throws WebServiceException {

            RestTemplate restTemplate = getRestTemplate();

            ResponseEntity<CollectionResponseGameMap> responseEntity =
                    restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), CollectionResponseGameMap.class);

            return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public void saveMapImage(GameMap map) throws WebServiceException {

        if (isBitmapCached(ctx, map.getId(), map.getLastUsedDate()))
            return;

        byte[] mapArray = getBinaryData(map, RestConst.URL_GAME_MAP_IMAGE);

        if (mapArray == null)
            throw new WebServiceException(HttpStatus.NOT_FOUND, "Game map is empty.");
        else
            try {
                // create bitmap and save into map
                saveBitmap2DB(ctx, mapArray, map.getId(), map.getLastUsedDate());
            } catch (IOException e) {
                throw new WebServiceException(HttpStatus.NOT_FOUND, "Game map is empty.");
            }
    }

    public String uploadMapImage(GameMap map, String fileName) throws WebServiceException {
        return uploadFile(map, "mapid", URL_GAME_MAP_IMAGE_SIMPLE, fileName);
    }
}
