package com.sadgames.dicegame.desktop;

import com.cubegames.engine.domain.entities.GameMap;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;

import static com.sadgames.sysutils.common.DBUtils.isBitmapCached;
import static com.sadgames.sysutils.common.DBUtils.saveBitmap2DB;

/**
 * Created by Slava Mamchur on 31.10.2018.
 */
public class DesktopRestApiWrapper implements RestApiInterface {

    private static final Object lockObject = new Object();
    @SuppressWarnings("all")
    private static DesktopRestApiWrapper instance = null;

    public static DesktopRestApiWrapper getInstance() {
        synchronized (lockObject) {
            return instance != null ? instance : new DesktopRestApiWrapper();
        }
    }

    public static void releaseInstance() {
        synchronized (lockObject) {
            instance = null;
        }
    }

    @Override
    public void moveGameInstance(GameInstanceEntity gameInstanceEntity) {

    }

    @Override
    public void finishGameInstance(GameInstanceEntity gameInstanceEntity) {

    }

    @Override
    public void restartGameInstance(GameInstanceEntity gameInstanceEntity) {

    }

    @Override
    public void showTurnInfo(GameInstanceEntity gameInstanceEntity) {

    }

    @Override
    public void removeLoadingSplash() {

    }

    @Override
    public EntityControllerInterface iGetEntityController(String action, Class<? extends BasicEntity> entityType, Class<? extends GenericCollectionResponse> listType, int method) {
        return null;
    }

    @Override
    public void iDownloadBitmapIfNotCached(String textureResName, boolean isRelief) {
        GameMap map = null; //TODO: map = getMap(textureResName)

        if (!(map == null || map.getId() == null || map.getId().isEmpty()))
            try {
                if (isRelief)
                    saveMapRelief(map);
                else
                    saveMapImage(map);
            } catch (Exception ignored) {}
    }

    public void saveMapImage(GameMap map) {
        internalSavePicture(map, RestConst.URL_GAME_MAP_IMAGE, "", "GameEntity map is empty.");
    }

    public void saveMapRelief(GameMap map) {
        internalSavePicture(map, RestConst.URL_GAME_MAP_RELIEF, "rel_", "Relief map is empty.");
    }

    private void internalSavePicture(GameMap map, String url, String namePrefix, String errorMessage) {
        if (isBitmapCached(namePrefix + map.getId(), map.getLastUsedDate()))
            return;

        byte[] mapArray = "rel_".equals(namePrefix) ? map.getBinaryDataRelief() : map.getBinaryData();
        if (mapArray == null)
            throw new RuntimeException(errorMessage);
        else try {
            saveBitmap2DB(mapArray, namePrefix + map.getId(), map.getLastUsedDate());
        } catch (Exception e) {
            throw new RuntimeException(errorMessage);
        }

        /*byte[] mapArray = controller.iGetBinaryData(map, url, MEDIA_TYPE_IMAGE_JPEG);

        if (mapArray == null)
            controller.iThrowWebServiceException(HTTP_STATUS_NOT_FOUND, errorMessage);
        else try {
            saveBitmap2DB(mapArray, namePrefix + map.getId(), map.getLastUsedDate());
        } catch (Exception e) {
            controller.iThrowWebServiceException(HTTP_STATUS_NOT_FOUND, errorMessage);
        }*/
    }
}
