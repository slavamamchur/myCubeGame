package com.sadgames.sysutils.platforms.android;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.Collection;

public class AndroidRESTControllerFabric implements EntityControllerInterface {

    private static final Object lockObject = new Object();
    private static AndroidRESTControllerFabric instance = null;

    private SysUtilsWrapperInterface sysUtilsWrapper;

    public AndroidRESTControllerFabric(SysUtilsWrapperInterface sysUtilsWrapper) {
        this.sysUtilsWrapper = sysUtilsWrapper;
    }

    //TODO: change via cache -> iGetController
    public static AndroidRESTControllerFabric getInstance(SysUtilsWrapperInterface sysUtilsWrapper) {
        synchronized (lockObject) {
            instance = instance != null ? instance : new AndroidRESTControllerFabric(sysUtilsWrapper);
            return instance;
        }
    }

    @Override
    public EntityControllerInterface iGetController(String action) {
        return null;
    }

    @Override
    public BasicEntity iGetEntity(String id) {
        return null;
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
        return null;
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
}
