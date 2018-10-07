package com.sadgames.sysutils.common;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;

import org.luaj.vm2.LuaTable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.sql.Connection;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public interface SysUtilsWrapperInterface {

    String                 iReadTextFromFile(String fileName);
    InputStream            iGetResourceStream(String fileName);

    BitmapWrapperInterface iCreateColorBitmap(int color);
    BitmapWrapperInterface iCreateBitmap(InputStream source);
    BitmapWrapperInterface iCompressTexture(Buffer input, int width, int height, int pixelSize, int stride);
    BitmapWrapperInterface iCreateETC1Texture(InputStream input) throws IOException;

    Connection             iGetDBConnection(String dbName);
    void                   iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException;
    BitmapWrapperInterface iLoadBitmapFromDB(String textureResName, boolean isRelief);

    void                   iPlaySound(String file);
    void                   iStopSound();

    Matrix4f               createTransform();
    Vector3f               createVector3f(float vx, float vy, float vz);
    Vector3f               mulMV(float[] matrix, float[] vector);
    Vector3f               mulMV(Matrix4f matrix, LuaTable vector);
    Vector3f               mulMV(float[] matrix, LuaTable vector);
    void                   mulMM(float[] result, int resultOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset);
    void                   rotateM(float[] m, int mOffset, float a, float x, float y, float z);


    SettingsManagerInterface iGetSettingsManager();
    EntityControllerInterface iGetEntityController(String action,
                                                   Class<? extends BasicEntity> entityType,
                                                   Class<? extends GenericCollectionResponse> listType,
                                                   int method);

    //TODO: use java SE common lib for pictures  - > PNGDecoder
    //TODO: getJDBCConnection()
}
