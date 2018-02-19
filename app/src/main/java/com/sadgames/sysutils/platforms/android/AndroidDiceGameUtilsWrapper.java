package com.sadgames.sysutils.platforms.android;

import android.content.Context;

import com.sadgames.dicegame.logic.server.rest_api.WebServiceException;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameMapController;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.SysUtilsWrapperInterface;

public class AndroidDiceGameUtilsWrapper extends AndroidSysUtilsWrapper {

    public AndroidDiceGameUtilsWrapper(Context context) {
        super(context);
    }

    public static SysUtilsWrapperInterface getInstance(Context context) {
        synchronized (lockObject) {
            return instance != null ? instance : new AndroidDiceGameUtilsWrapper(context);
        }
    }

    @Override
    protected void downloadBitmapIfNotCached(String textureResName, boolean isRelief) {
        GameMapController gmc = new GameMapController(this);
        GameMapEntity map = gmc.find(textureResName);

        try {
            if (isRelief)
                gmc.saveMapRelief(map);
            else
                gmc.saveMapImage(map);
        }
        catch (WebServiceException e) {}
    }
}
