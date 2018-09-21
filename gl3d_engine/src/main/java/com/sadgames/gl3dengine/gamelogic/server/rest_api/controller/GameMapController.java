package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.WebServiceException;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameMapCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpStatus;

import java.io.IOException;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_MAP;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_MAP_IMAGE_SIMPLE;

public class GameMapController extends BaseController<GameMapEntity, GameMapCollectionResponse> {

    public GameMapController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_GAME_MAP, GameMapEntity.class, GameMapCollectionResponse.class, null, sysUtilsWrapper);
    }

    public void saveMapImage(GameMapEntity map) throws WebServiceException {
        internalSavePicture(map, RestConst.URL_GAME_MAP_IMAGE, "", "GameEntity map is empty.");
    }

    public void saveMapRelief(GameMapEntity map) throws WebServiceException {
        internalSavePicture(map, RestConst.URL_GAME_MAP_RELIEF, "rel_", "Relief map is empty.");
    }

    private void internalSavePicture(GameMapEntity map, String url, String namePrefix, String errorMessage) {
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
