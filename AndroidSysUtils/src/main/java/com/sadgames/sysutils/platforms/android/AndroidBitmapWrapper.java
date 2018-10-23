package com.sadgames.sysutils.platforms.android;

import android.graphics.Bitmap;

import com.sadgames.sysutils.common.BitmapWrapper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AndroidBitmapWrapper extends BitmapWrapper {

    AndroidBitmapWrapper(Bitmap picture) {
        super(getRawDataFromBitmap(picture),
              picture != null ? picture.getWidth() : 0,
              picture != null ? picture.getHeight() : 0,
             false);

        if (picture != null)
            picture.recycle();
    }

    private static ByteBuffer getRawDataFromBitmap(Bitmap picture) {
        ByteBuffer rawData = null;

        if (picture != null) {
            rawData = ByteBuffer.allocateDirect(picture.getByteCount()).order(ByteOrder.nativeOrder());
            picture.copyPixelsToBuffer(rawData);
        }

        return rawData;
    }



}
