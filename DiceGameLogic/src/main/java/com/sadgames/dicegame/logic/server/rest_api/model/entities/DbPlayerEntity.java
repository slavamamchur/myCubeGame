package com.sadgames.dicegame.logic.server.rest_api.model.entities;

import com.sadgames.dicegame.logic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.DBPlayerController;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.Serializable;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_PLAYER;

public class DbPlayerEntity extends BasicNamedDbEntity implements Serializable {

    private static final long serialVersionUID = 1709751985689672630L;

    public int color;
    public static String ACTION_NAME =  URL_PLAYER;

    public DbPlayerEntity() {}

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper) {
        return new DBPlayerController(sysUtilsWrapper);
    }
}
