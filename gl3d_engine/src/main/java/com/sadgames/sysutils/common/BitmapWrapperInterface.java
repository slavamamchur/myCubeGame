package com.sadgames.sysutils.common;

import org.luaj.vm2.LuaTable;

import java.nio.Buffer;

public interface BitmapWrapperInterface {

    Buffer  getRawData();
    Buffer  getDecodedRawData();
    int     getPixelColor(int x, int y);

    int     getWidth();
    int     getHeight();
    int     getImageSizeBytes();

    boolean isEmpty();
    boolean isCompressed();

    void    drawPath(LuaTable path, int pathColor, int wayPointColor, float scaleFactor);

    void    release();

}
