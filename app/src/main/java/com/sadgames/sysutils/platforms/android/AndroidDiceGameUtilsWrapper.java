package com.sadgames.sysutils.platforms.android;

import android.content.Context;

import com.sadgames.dicegame.rest_api.GameMapController;
import com.sadgames.dicegame.rest_api.model.GameMap;

public class AndroidDiceGameUtilsWrapper extends AndroidSysUtilsWrapper {

    public AndroidDiceGameUtilsWrapper(Context context) {
        super(context);
    }

    @Override
    protected void downloadBitmapIfNotCached(String textureResName, boolean isRelief) {
        GameMapController gmc = new GameMapController(this);
        GameMap map = gmc.find(textureResName);
        if (isRelief)
            gmc.saveMapRelief(map);
        else
            gmc.saveMapImage(map);
    }
}
