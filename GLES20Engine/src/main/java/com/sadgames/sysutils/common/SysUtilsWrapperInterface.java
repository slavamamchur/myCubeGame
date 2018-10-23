package com.sadgames.sysutils.common;

import java.nio.Buffer;

public interface SysUtilsWrapperInterface {

    BitmapWrapperInterface iCreateColorBitmap(int color);
    BitmapWrapperInterface iDecodeImage(byte[] bitmapArray, boolean isRelief);
    BitmapWrapperInterface iCompressTexture(Buffer input, int width, int height, int pixelSize, int stride);

}
