package com.sadgames.gl3d_engine.utils;

import android.graphics.Bitmap;

import java.io.IOException;

public interface ISysUtilsWrapper {

    String  iReadTextFromFile(String fileName);

    Bitmap  iGetBitmapFromFile(String file);
    Bitmap  iCreateColorBitmap(int color);
    Bitmap  iLoadBitmapFromDB(String textureResName);
    Bitmap  iLoadReliefFromDB(String textureResName);
    boolean iIsBitmapCached(String map_id, Long updatedDate);
    void    iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException;

    void    iPlaySound(String file);
    void    iStopSound();

    ISettingsManager iGetSettingsManager();
    //TODO: Create Bitmap interface and wrapper or use java SE common lib
}
