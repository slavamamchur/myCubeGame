package com.sadgames.sysutils.platforms.android;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.BaseController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.BasicResponse;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GameInstanceCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpMethod;

import java.util.Collection;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE;

public class AndroidRESTControllerFabric implements EntityControllerInterface {

    private SysUtilsWrapperInterface sysUtilsWrapper;
    private BaseController<?, ?> controller;

    public AndroidRESTControllerFabric(SysUtilsWrapperInterface sysUtilsWrapper, String action) {
        this.sysUtilsWrapper = sysUtilsWrapper;

        if (action.equals(RestConst.URL_GAME_INSTANCE))
            controller = new BaseController<>
                    (URL_GAME_INSTANCE, GameInstanceEntity.class, GameInstanceCollectionResponse.class, null, sysUtilsWrapper);
    }

    public static AndroidRESTControllerFabric createInstance(SysUtilsWrapperInterface sysUtilsWrapper, String action) {
        return new AndroidRESTControllerFabric(sysUtilsWrapper, action);
    }

    @Override
    public BasicEntity iGetEntity(String id) {
        return controller.find(id);
    }

    @Override
    public void sendPOSTRequest(String action, Object entity) {

    }

    @Override
    public BasicEntity getResponse(String action, Object... args) {
        return null;
    }

    @Override
    public BasicEntity iUpdateEntity(BasicNamedDbEntity entity) {
        return null;
    }

    @Override
    public void iDeleteEntity(BasicNamedDbEntity entity) {

    }

    @Override
    public Collection iGetEntityList() {
        return controller.getResponseList();
    }

    @Override
    public byte[] iGetBinaryData(BasicNamedDbEntity entity, String dataUrl) {
        return new byte[0];
    }

    @Override
    public String iUploadFile(BasicNamedDbEntity entity, String keyParamName, String uploadActionNAme, String fileName) {
        return null;
    }

    @Override
    public void iAddChild(String id, String childName, Object child) {

    }

    @Override
    public void iRemoveChild(String id, String childName, int index) {

    }

    @Override
    public BasicResponse getResponseWithParams(String action, HttpMethod method, Object entity, Class<?> responseType, Object... args) {
        return null;
    }
}
