package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.DBPlayerController;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.Serializable;

public class DbPlayerEntity extends BasicNamedDbEntity implements Serializable {

    private static final long serialVersionUID = 1709751985689672630L;

    public int color;

    public DbPlayerEntity() {}

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String getActionURL() {
        return RestConst.URL_PLAYER;
    }

    @Override
    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper) {
        return new DBPlayerController(sysUtilsWrapper);
    }
}
