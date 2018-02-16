package com.sadgames.dicegame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sadgames.dicegame.logic.server.rest_api.RestApiInterface;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;

import static com.sadgames.dicegame.RestApiService.startActionMoveGameInstance;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_SHOW_TURN_INFO;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_DICE_VALUE;

public class AndroidRestApiWrapper implements RestApiInterface {
    private static final Object lockObject = new Object();
    @SuppressWarnings("all")
    private static AndroidRestApiWrapper instance = null;

    public static AndroidRestApiWrapper getInstance(Context context) {
        synchronized (lockObject) {
            return instance != null ? instance : new AndroidRestApiWrapper(context);
        }
    }

    public static void releaseInstance() {
        synchronized (lockObject) {
            instance = null;
        }
    }

    private Context context;

    public AndroidRestApiWrapper(Context context) {
        this.context = context;
    }

    private void sendResponseIntent(String action, Bundle params){
        Intent responseIntent = new Intent();
        responseIntent.setAction(action);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtras(params);
        context.sendBroadcast(responseIntent);
    }

    @Override
    public void moveGameInstance(GameInstanceEntity gameInstanceEntity) {
        startActionMoveGameInstance(context, gameInstanceEntity);
    }

    @Override
    public void showTurnInfo(GameInstanceEntity gameInstanceEntity) {
        Bundle params = new Bundle();
        params.putInt(EXTRA_DICE_VALUE, gameInstanceEntity.getStepsToGo());
        sendResponseIntent(ACTION_SHOW_TURN_INFO, params);
    }


}
