package com.sadgames.gl3d_engine.utils;

import android.graphics.Point;

import java.nio.Buffer;

public interface IBitmapWrapper {

    Buffer  getRawData();
    int     getPixelColor(Point position);

    int     getWidth();
    int     getHeight();

    void    release();

}
