package com.sadgames.dicegame.logic.server.rest_api;

import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;

public interface RestApiInterface {

    void moveGameInstance(GameInstanceEntity gameInstanceEntity);
    void finishGameInstance(GameInstanceEntity gameInstanceEntity);
    void restartGameInstance(GameInstanceEntity gameInstanceEntity);

    void showTurnInfo(GameInstanceEntity gameInstanceEntity);

}
