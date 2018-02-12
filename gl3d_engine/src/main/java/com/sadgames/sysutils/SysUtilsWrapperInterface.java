package com.sadgames.sysutils;

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
    GLES20WrapperInterface iGetGLES20WrapperInterface();

    //TODO: use java SE common lib for pictures ???
}
