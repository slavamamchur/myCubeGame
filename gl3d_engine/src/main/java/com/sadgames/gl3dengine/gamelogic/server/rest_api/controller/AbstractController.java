package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.Collection;

public abstract class AbstractController {

    protected EntityControllerInterface controller;

    protected AbstractController(String action,
                                 Class<? extends BasicEntity> responseType,
                                 Class<? extends GenericCollectionResponse> listType,
                                 int method,
                                 SysUtilsWrapperInterface sysUtilsWrapper) {
        controller = sysUtilsWrapper.iGetEntityController(action, responseType, listType, method);
    }

    public Collection getResponseList() {
        return  controller.iGetEntityList();
    }


}
