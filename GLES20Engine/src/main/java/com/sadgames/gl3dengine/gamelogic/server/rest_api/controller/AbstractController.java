package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.gl3dengine.glrender.GdxExt;

import java.util.Collection;

public abstract class AbstractController {

    protected static final int HTTP_METHOD_GET = 0;
    protected static final int HTTP_METHOD_POST = 1;

    protected EntityControllerInterface controller;

    protected AbstractController(String action,
                                 Class<? extends BasicEntity> responseType,
                                 Class<? extends GenericCollectionResponse> listType,
                                 int method) {
        controller = GdxExt.restAPI.iGetEntityController(action, responseType, listType, method);
    }

    public Collection getResponseList() {
        return  controller.iGetEntityList();
    }

    public void delete(BasicNamedDbEntity entity) {
        controller.iDeleteEntity(entity);
    }

    public BasicEntity update(BasicNamedDbEntity entity) {
        return controller.iUpdateEntity(entity);
    }

    public BasicEntity find(String id) {
        return controller.iGetEntity(id);
    }
}
