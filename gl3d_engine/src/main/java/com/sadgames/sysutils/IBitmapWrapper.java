package com.sadgames.sysutils;

import android.graphics.Point;

import java.nio.Buffer;

public interface IBitmapWrapper {

    Buffer  getRawData();
    int     getPixelColor(Point position);

    int     getWidth();
    int     getHeight();

    void    release();

}
