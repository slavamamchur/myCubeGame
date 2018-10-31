package com.sadgames.dicegame.desktop;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;

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
}
