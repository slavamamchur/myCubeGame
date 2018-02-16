package com.sadgames.gl3dengine;

import com.sadgames.gl3dengine.glrender.BitmapWrapperInterface;
import com.sadgames.gl3dengine.glrender.GLES20APIWrapperInterface;

import java.io.IOException;

import javax.vecmath.Vector3f;

public interface SysUtilsWrapperInterface {

    String                 iReadTextFromFile(String fileName);

    BitmapWrapperInterface iGetBitmapFromFile(String file);
    BitmapWrapperInterface iGetReliefFromFile(String file);
    BitmapWrapperInterface iCreateColorBitmap(int color);
    boolean                iIsBitmapCached(String map_id, Long updatedDate);
    void                   iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException;

    void                   iPlaySound(String file);
    void                   iStopSound();

    Vector3f               mulMV(float[] matrix, float[] vector);

    SettingsManagerInterface iGetSettingsManager();
    GLES20APIWrapperInterface iGetGLES20WrapperInterface();

    //TODO: use java SE common lib for pictures ???
}
