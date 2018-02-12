package com.sadgames.dicegame.rest_api;

import com.sadgames.dicegame.rest_api.model.entities.GameMapEntity;
import com.sadgames.dicegame.rest_api.model.responses.GameMapCollectionResponse;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.dicegame.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.dicegame.rest_api.RestConst.URL_GAME_MAP_IMAGE_SIMPLE;
import static com.sadgames.dicegame.rest_api.RestConst.URL_LIST;

public class GameMapController extends AbstractHttpRequest<GameMapEntity> {

    public GameMapController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(GameMapEntity.ACTION_NAME, GameMapEntity.class, HttpMethod.GET, sysUtilsWrapper);
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

            ResponseEntity<GameMapCollectionResponse> responseEntity =
                    restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), GameMapCollectionResponse.class);

            return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public void saveMapImage(GameMapEntity map) throws WebServiceException {
        internalSavePicture(map, RestConst.URL_GAME_MAP_IMAGE, "", "GameEntity map is empty.");
    }

    public void saveMapRelief(GameMapEntity map) throws WebServiceException {
        internalSavePicture(map, RestConst.URL_GAME_MAP_RELIEF, "rel_", "Relief map is empty.");
    }

    public void internalSavePicture(GameMapEntity map, String url, String namePrefix, String errorMessage) {
        if (getSysUtilsWrapper().iIsBitmapCached(namePrefix + map.getId(), map.getLastUsedDate()))
            return;

        byte[] mapArray = getBinaryData(map, url);

        if (mapArray == null)
            throw new WebServiceException(HttpStatus.NOT_FOUND, errorMessage);
        else try {
            // create bitmap and save into map
            getSysUtilsWrapper().iSaveBitmap2DB(mapArray, namePrefix + map.getId(), map.getLastUsedDate());
        } catch (IOException e) {
            throw new WebServiceException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    public String uploadMapImage(GameMapEntity map, String fileName) throws WebServiceException {
        return uploadFile(map, "mapid", URL_GAME_MAP_IMAGE_SIMPLE, fileName);
    }
}
