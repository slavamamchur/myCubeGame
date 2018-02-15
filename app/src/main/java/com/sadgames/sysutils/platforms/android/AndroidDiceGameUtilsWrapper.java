package com.sadgames.sysutils.platforms.android;

import android.content.Context;

import com.sadgames.dicegame.game.server.rest_api.GameMapController;
import com.sadgames.dicegame.game.server.rest_api.WebServiceException;
import com.sadgames.dicegame.game.server.rest_api.model.entities.GameMapEntity;

public class AndroidDiceGameUtilsWrapper extends AndroidSysUtilsWrapper {

    public AndroidDiceGameUtilsWrapper(Context context) {
        super(context);
    }

    @Override
    protected void downloadBitmapIfNotCached(String textureResName, boolean isRelief) {
        GameMapController gmc = new GameMapController(this);
        GameMapEntity map = gmc.find(textureResName);
        if (isRelief)
            try { gmc.saveMapRelief(map); } catch (WebServiceException e) {}
        else
            gmc.saveMapImage(map);
    }
}
