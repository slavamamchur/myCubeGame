package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.Serializable;

public class BasicNamedDbEntity extends BasicDbEntity implements Serializable {

    @JsonProperty(required = true)
    public String name;

    public BasicNamedDbEntity(){}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static String ACTION_NAME = "";
    public String urlForActionName(){
        return ACTION_NAME;
    }

    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper){
        return null;
    }
}
