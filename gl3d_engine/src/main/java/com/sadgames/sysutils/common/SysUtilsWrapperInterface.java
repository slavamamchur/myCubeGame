package com.sadgames.sysutils.common;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;

import org.luaj.vm2.LuaTable;

import java.io.IOException;
import java.io.InputStream;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public interface SysUtilsWrapperInterface {

    String                 iReadTextFromFile(String fileName);
    InputStream            getResourceStream(String fileName);

    BitmapWrapperInterface iGetBitmapFromFile(String file);
    BitmapWrapperInterface iGetReliefFromFile(String file);
    BitmapWrapperInterface iCreateColorBitmap(int color);
    BitmapWrapperInterface packToETC1(BitmapWrapperInterface bitmap);

    boolean                iIsBitmapCached(String map_id, Long updatedDate);
    void                   iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException;

    void                   iPlaySound(String file);
    void                   iStopSound();

    Matrix4f               createTransform();
    Vector3f               createVector3f(float vx, float vy, float vz);
    Vector3f               mulMV(float[] matrix, float[] vector);
    Vector3f               mulMV(Matrix4f matrix, LuaTable vector);
    Vector3f               mulMV(float[] matrix, LuaTable vector);

    SettingsManagerInterface iGetSettingsManager();
    EntityControllerInterface iGetEntityController();

    //TODO: use java SE common lib for pictures  - > PNGDecoder
}
