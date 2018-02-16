package com.sadgames.dicegame;

import android.content.Context;

import com.sadgames.dicegame.logic.server.rest_api.RestApiInterface;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;

import static com.sadgames.dicegame.RestApiService.startActionMooveGameInstance;

public class AndroidRestApiWrapper implements RestApiInterface {
    private static final Object lockObject = new Object();
    private static AndroidRestApiWrapper instance = null;

    public static AndroidRestApiWrapper getInstance(Context context) {
        synchronized (lockObject) {
            return instance != null ? instance : new AndroidRestApiWrapper(context);
        }
    }

    private Context context;

    public AndroidRestApiWrapper(Context context) {
        this.context = context;
    }

    @Override
    public void moveGameInstance(GameInstanceEntity gameInstanceEntity) {
        startActionMooveGameInstance(context, gameInstanceEntity);
    }


}
