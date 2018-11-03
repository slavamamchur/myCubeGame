package com.sadgames.gl3dengine.gamelogic.server.rest_api;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;

public interface RestApiInterface {

    void moveGameInstance(GameInstanceEntity gameInstanceEntity);
    void finishGameInstance(GameInstanceEntity gameInstanceEntity);
    void restartGameInstance(GameInstanceEntity gameInstanceEntity);

    void showTurnInfo(GameInstanceEntity gameInstanceEntity);
    void removeLoadingSplash();

    EntityControllerInterface iGetEntityController(String action,
                                                   Class<? extends BasicEntity> entityType,
                                                   Class<? extends GenericCollectionResponse> listType,
                                                   int method);

    //TODO: remove fake
    void iDownloadBitmapIfNotCached(String textureResName,
                                   boolean isRelief);
}
