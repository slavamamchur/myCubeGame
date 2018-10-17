package com.sadgames.gl3dengine.glrender;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.sysutils.common.GdxDbInterface;

public class GdxExt extends Gdx {

    public static GdxDbInterface dataBase;
    public static Preferences preferences;
    public static RestApiInterface restAPI;
}
