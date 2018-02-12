package com.sadgames.gl3d_engine;

import com.sadgames.gl3d_engine.gl_render.BitmapWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.GLES20APIWrapperInterface;

import java.io.IOException;

public interface SysUtilsWrapperInterface {

    String          iReadTextFromFile(String fileName);

    BitmapWrapperInterface iGetBitmapFromFile(String file);
    BitmapWrapperInterface iGetReliefFromFile(String file);
    BitmapWrapperInterface iCreateColorBitmap(int color);
    boolean         iIsBitmapCached(String map_id, Long updatedDate);
    void            iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException;

    void            iPlaySound(String file);
    void            iStopSound();

    SettingsManagerInterface iGetSettingsManager();
    GLES20APIWrapperInterface iGetGLES20WrapperInterface();

    //TODO: use java SE common lib for pictures ???
}
