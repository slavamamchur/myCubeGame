package com.sadgames.gl3dengine.gamelogic.server.rest_api;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;

import java.util.Collection;


public interface EntityControllerInterface {

    EntityControllerInterface iGetController(String action);

    BasicEntity iGetEntity(String id);
    void sendPOSTRequest(String action, Object entity);
    BasicEntity getResponse(String action, Object ... args);
    BasicEntity iUpdateEntity(BasicNamedDbEntity entity);
    void iDeleteEntity(BasicNamedDbEntity entity);
    Collection iGetEntityList();
    byte[] iGetBinaryData(BasicNamedDbEntity entity, String dataUrl);
    String iUploadFile(BasicNamedDbEntity entity, String keyParamName, String uploadActionNAme, String fileName);
    void iAddChild(String id, String childName, Object child);
    void iRemoveChild(String id, String childName, int index);
}
