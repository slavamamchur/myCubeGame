package com.cubegames.slava.cubegame.platforms.android.sysutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.gl3d_engine.utils.ISysUtilsWrapper;

import java.io.IOException;

public class AndroidSysUtilsWrapper implements ISysUtilsWrapper {

    private Context context;

    public AndroidSysUtilsWrapper(Context context) {
        this.context = context;
    }

    @Override
    public String readTextFromResource(int id) {
        return AndroidSysUtils.readTextFromRawResource(context, id);
    }

    @Override
    public String readTextFromFile(String fileName) {
        return AndroidSysUtils.readTextFromAssets(context, fileName);
    }

    @Override
    public Bitmap getBitmapFromResource(int id) {
        return AndroidSysUtils.getBitmapFromResource(context, id);
    }

    @Override
    public Bitmap getBitmapFromFile(String file) {
        return AndroidSysUtils.getBitmapFromFile(context, file);
    }

    @Override
    public Bitmap createColorBitmap(int color) {
        return AndroidSysUtils.createColorBitmap(color);
    }

    @Override
    public Bitmap loadBitmapFromDB(String textureResName) {
        return AndroidSysUtils.loadBitmapFromDB(this, context, textureResName, false);
    }

    @Override
    public Bitmap loadReliefFromDB(String textureResName) {
        return AndroidSysUtils.loadBitmapFromDB(this, context, textureResName, true);
    }

    @Override
    public boolean isBitmapCached(String map_id, Long updatedDate) {
        return AndroidSysUtils.isBitmapCached(context, map_id, updatedDate);
    }

    @Override
    public void saveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException {
        AndroidSysUtils.saveBitmap2DB(context, bitmapArray, map_id, updatedDate);
    }

    @Override
    public void playSound(String file) {
        AndroidSysUtils.playSound(context, file);
    }

    @Override
    public void stopSound() {
        AndroidSysUtils.stopSound();
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return AndroidSysUtils.getDefaultSharedPrefs(context);
    }
}
