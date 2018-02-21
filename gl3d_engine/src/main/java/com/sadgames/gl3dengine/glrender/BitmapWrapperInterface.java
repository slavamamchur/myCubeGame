package com.sadgames.gl3dengine.glrender;

import java.nio.Buffer;
import java.util.List;

import javax.vecmath.Vector2f;

public interface BitmapWrapperInterface {

    Buffer  getRawData();
    int     getPixelColor(int x, int y);

    int     getWidth();
    int     getHeight();
    int     getImageSizeBytes();

    boolean isEmpty();
    boolean isCompressed();

    void    drawPath(List<Vector2f> path, int pathColor, int wayPointColor, float scaleFactor);

    void    release();

}
