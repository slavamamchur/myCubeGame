package com.sadgames.sysutils.platforms.android;

import android.graphics.Bitmap;
import android.graphics.Point;

import com.sadgames.sysutils.IBitmapWrapper;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AndroidBitmapWrapper implements IBitmapWrapper {

    private Bitmap picture;

    public AndroidBitmapWrapper(Bitmap picture) {
        this.picture = picture;
    }

    @Override
    public Buffer getRawData() {
        ByteBuffer result = ByteBuffer.allocateDirect(picture.getByteCount()).order(ByteOrder.nativeOrder());
        picture.copyPixelsToBuffer(result);
        result.position(0);

        return result;
    }

    @Override
    public int getPixelColor(Point position) {
        return picture.getPixel(position.x, position.y);
    }

    @Override
    public int getWidth() {
        return picture.getWidth();
    }

    @Override
    public int getHeight() {
        return picture.getHeight();
    }

    @Override
    public void release() {
        picture.recycle();
    }
}
