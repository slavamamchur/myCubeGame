package com.sadgames.sysutils;

import android.graphics.Bitmap;

import java.io.IOException;

public interface ISysUtilsWrapper {

    String          iReadTextFromFile(String fileName);

    Bitmap          iGetBitmapFromFile(String file);
    IBitmapWrapper  iGetReliefFromFile(String file);
    Bitmap          iCreateColorBitmap(int color);
    boolean         iIsBitmapCached(String map_id, Long updatedDate);
    void            iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException;

    void            iPlaySound(String file);
    void            iStopSound();

    ISettingsManager iGetSettingsManager();
    //TODO: use java SE common lib for pictures ???
}
