package com.sadgames.sysutils;

import android.graphics.Point;

import java.nio.Buffer;
import java.util.List;

import javax.vecmath.Vector2f;

public interface IBitmapWrapper {

    Buffer  getRawData();
    int     getPixelColor(Point position);

    int     getWidth();
    int     getHeight();

    void    drawPath(List<Vector2f> path, int pathColor, int wayPointColor);

    void    release();

}
