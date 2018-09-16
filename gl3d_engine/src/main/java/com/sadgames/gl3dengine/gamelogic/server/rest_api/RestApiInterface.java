package com.sadgames.gl3dengine.gamelogic.server.rest_api;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;

public interface RestApiInterface {

    void moveGameInstance(GameInstanceEntity gameInstanceEntity);
    void finishGameInstance(GameInstanceEntity gameInstanceEntity);
    void restartGameInstance(GameInstanceEntity gameInstanceEntity);

    void showTurnInfo(GameInstanceEntity gameInstanceEntity);
    void removeLoadingSplash();

}
