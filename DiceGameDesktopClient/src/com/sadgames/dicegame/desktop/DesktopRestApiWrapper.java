package com.sadgames.dicegame.desktop;

import com.cubegames.engine.domain.entities.GameMap;
import com.cubegames.vaa.client.Consts;
import com.cubegames.vaa.client.RestClient;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.SettingsManagerInterface;

import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;
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
    public byte[] iDownloadBitmapIfNotCached(String textureResName, boolean isRelief) {
        RestClient restClient = getRestClient();
        GameMap map = restClient.getGameMap(textureResName);

        if (!(map == null || map.getId() == null || map.getId().isEmpty()))
            try {
                if (isRelief)
                    saveMapRelief(map);
                else
                    saveMapImage(map);
            } catch (Exception ignored) {}

        return null;
    }

    private RestClient getRestClient() {
        SettingsManagerInterface setings = getSettingsManager();
        RestClient restClient = new RestClient(setings.getWebServiceUrl(Consts.BASE_URL));
        restClient.setToken(setings.getAuthToken());

        return restClient;
    }


    public void saveMapImage(GameMap map) throws NoSuchFieldException {
        internalSavePicture(map, RestConst.URL_GAME_MAP_IMAGE, "", "GameEntity map is empty.");
    }

    public void saveMapRelief(GameMap map) throws NoSuchFieldException {
        internalSavePicture(map, RestConst.URL_GAME_MAP_RELIEF, "rel_", "Relief map is empty.");
    }

    private void internalSavePicture(GameMap map, String url, String namePrefix, String errorMessage) throws NoSuchFieldException {
        if (isBitmapCached(namePrefix + map.getId(), map.getLastUsedDate()))
            return;

        RestClient restClient = getRestClient();
        byte[] mapArray = restClient.getBinaryData(restClient.getMapImagePostfix(map.getId(), !"rel_".equals(namePrefix)));
        if (mapArray == null)
            throw new NoSuchFieldException(errorMessage);
        else try {
            saveBitmap2DB(mapArray, namePrefix + map.getId(), map.getLastUsedDate());
        } catch (Exception e) {
            throw new RuntimeException(errorMessage);
        }
    }
}
