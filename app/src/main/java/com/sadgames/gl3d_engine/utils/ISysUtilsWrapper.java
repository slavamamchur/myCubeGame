package com.sadgames.gl3d_engine.utils;

import android.graphics.Bitmap;

import java.io.IOException;

public interface ISysUtilsWrapper {

    String  readTextFromResource(int id);
    String  readTextFromFile(String fileName);

    Bitmap  getBitmapFromResource(int id);
    Bitmap  getBitmapFromFile(String file);
    Bitmap  createColorBitmap(int color);
    Bitmap  loadBitmapFromDB(String textureResName);
    Bitmap  loadReliefFromDB(String textureResName);
    boolean isBitmapCached(String map_id, Long updatedDate);
    void    saveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException;

    void    playSound(String file);
    void    stopSound();

    ISettingsManager getSettingsManager();
}
