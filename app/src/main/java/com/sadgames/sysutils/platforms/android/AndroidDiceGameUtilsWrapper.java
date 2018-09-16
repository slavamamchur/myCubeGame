package com.sadgames.sysutils.platforms.android;

import android.content.Context;

import com.sadgames.dicegame.gamelogic.server.rest_api.WebServiceException;
import com.sadgames.dicegame.gamelogic.server.rest_api.controller.GameMapController;
import com.sadgames.dicegame.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

public class AndroidDiceGameUtilsWrapper extends AndroidSysUtilsWrapper {

    protected static SysUtilsWrapperInterface instance = null;

    public AndroidDiceGameUtilsWrapper(Context context) {
        super(context);
    }

    public static SysUtilsWrapperInterface getInstance(Context context) {
        synchronized (lockObject) {
            instance = instance != null ? instance : new AndroidDiceGameUtilsWrapper(context);
            return instance;
        }
    }

    @Override
    protected void downloadBitmapIfNotCached(String textureResName, boolean isRelief) {
        GameMapController gmc = new GameMapController(this);
        GameMapEntity map = gmc.find(textureResName);

        if (map == null || map.getId() == null || map.getId().isEmpty())
            return;

        try {
            if (isRelief)
                gmc.saveMapRelief(map);
            else
                gmc.saveMapImage(map);
        } catch (WebServiceException e) {}
    }
}
