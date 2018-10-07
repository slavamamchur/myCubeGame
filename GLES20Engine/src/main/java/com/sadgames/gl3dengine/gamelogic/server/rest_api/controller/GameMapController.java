package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameMapCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_MAP;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_MAP_IMAGE_SIMPLE;
import static com.sadgames.sysutils.common.DBUtils.isBitmapCached;
import static com.sadgames.sysutils.common.DBUtils.saveBitmap2DB;

public class GameMapController extends AbstractController {

    private static final int HTTP_STATUS_NOT_FOUND = 27;
    public final static String MEDIA_TYPE_IMAGE_JPEG = "image/jpeg";

    public GameMapController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(URL_GAME_MAP, GameMapEntity.class, GameMapCollectionResponse.class, HTTP_METHOD_GET, sysUtilsWrapper);
    }

    public void saveMapImage(GameMapEntity map) {
        internalSavePicture(map, RestConst.URL_GAME_MAP_IMAGE, "", "GameEntity map is empty.");
    }

    public void saveMapRelief(GameMapEntity map) {
        internalSavePicture(map, RestConst.URL_GAME_MAP_RELIEF, "rel_", "Relief map is empty.");
    }

    private void internalSavePicture(GameMapEntity map, String url, String namePrefix, String errorMessage) {
        if (isBitmapCached(sysUtilsWrapper, namePrefix + map.getId(), map.getLastUsedDate()))
            return;

        byte[] mapArray = controller.iGetBinaryData(map, url, MEDIA_TYPE_IMAGE_JPEG);

        if (mapArray == null)
            controller.iThrowWebServiceException(HTTP_STATUS_NOT_FOUND, errorMessage);
        else try {
            saveBitmap2DB(sysUtilsWrapper, mapArray, namePrefix + map.getId(), map.getLastUsedDate());
        } catch (Exception e) {
            controller.iThrowWebServiceException(HTTP_STATUS_NOT_FOUND, errorMessage);
        }
    }

    public String uploadMapImage(GameMapEntity map, String fileName) {
        return controller.iUploadFile(map, "mapid", URL_GAME_MAP_IMAGE_SIMPLE, fileName);
    }
}
