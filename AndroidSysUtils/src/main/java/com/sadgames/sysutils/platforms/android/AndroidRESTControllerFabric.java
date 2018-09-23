package com.sadgames.sysutils.platforms.android;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;
import com.sadgames.sysutils.platforms.android.restapi.BaseController;
import com.sadgames.sysutils.platforms.android.restapi.WebServiceException;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.Map;

public class AndroidRESTControllerFabric implements EntityControllerInterface {

    private BaseController<?, ?> controller;

    private AndroidRESTControllerFabric(SysUtilsWrapperInterface sysUtilsWrapper, String action,
                                        Class<? extends BasicEntity> entityType,
                                        Class<? extends GenericCollectionResponse> listType,
                                        int method) {
        controller = new BaseController<>(action, entityType, listType, null, method, sysUtilsWrapper);
    }

    public static EntityControllerInterface createInstance(SysUtilsWrapperInterface sysUtilsWrapper, String action,
                                                             Class<? extends BasicEntity> entityType,
                                                             Class<? extends GenericCollectionResponse> listType,
                                                             int method) {
        return new AndroidRESTControllerFabric(sysUtilsWrapper, action, entityType, listType, method);
    }

    @Override
    public BasicEntity iGetEntity(String id) {
        return controller.find(id);
    }

    @Override
    public void iSendPOSTRequest(String action, Object entity) {
        controller.sendPostRequest(action, entity);
    }

    @Override
    public BasicEntity iGetResponse(String action, Object... args) {
        return controller.getResponse(action, args);
    }

    @Override
    public BasicEntity iUpdateEntity(BasicNamedDbEntity entity) {
        return controller.update(entity);
    }

    @Override
    public void iDeleteEntity(BasicNamedDbEntity entity) {
        controller.delete(entity);
    }

    @Override
    public Collection iGetEntityList() {
        return controller.getResponseList();
    }

    @Override
    public byte[] iGetBinaryData(BasicNamedDbEntity entity, String dataUrl, String mediaType) {
        return controller.getBinaryData(entity, dataUrl, mediaType);
    }

    @Override
    public String iUploadFile(BasicNamedDbEntity entity, String keyParamName, String uploadActionNAme, String fileName) {
        return controller.uploadFile(entity, keyParamName, uploadActionNAme, fileName);
    }

    @Override
    public void iAddChild(String id, String childName, Object child) {
        controller.addChild(id, childName, child);
    }

    @Override
    public void iRemoveChild(String id, String childName, int index) {
        controller.removeChild(id, childName, index);
    }

    @Override
    public BasicEntity iGetResponseWithParams(String action, int method, Object entity, Class<?> responseType, Object... args) {
        return controller.getResponseWithParams(action, method, entity, responseType, args);
    }

    @Override
    public BasicEntity iGetResponseWithGetParams(String action, Object entity, Class<?> responseType, Object... args) {
        return iGetResponseWithParams(action, HttpMethod.GET.ordinal(), entity, responseType, args);
    }

    @Override
    public BasicEntity iGetResponseWithPostParams(String action, Object entity, Class<?> responseType, Object... args) {
        return iGetResponseWithParams(action, HttpMethod.POST.ordinal(), entity, responseType, args);
    }

    @Override
    public void iThrowWebServiceException(int HTTPStatus, String errorMessage) {
        throw new WebServiceException(HttpStatus.values()[HTTPStatus], errorMessage);
    }

    @Override
    public void iSetParams(Map<String, String> params) {
        controller.setParams(params);
    }
}
