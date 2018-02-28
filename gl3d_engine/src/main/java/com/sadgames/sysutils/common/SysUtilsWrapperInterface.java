package com.sadgames.sysutils.common;

import java.io.IOException;
import java.io.InputStream;

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

    Vector3f               mulMV(float[] matrix, float[] vector);

    SettingsManagerInterface iGetSettingsManager();

    //TODO: use java SE common lib for pictures  - > PNGDecoder
}
