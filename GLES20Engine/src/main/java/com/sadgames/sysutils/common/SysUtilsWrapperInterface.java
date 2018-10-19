package com.sadgames.sysutils.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

public interface SysUtilsWrapperInterface {

    BitmapWrapperInterface iCreateColorBitmap(int color);
    BitmapWrapperInterface iDecodeImage(byte[] bitmapArray, boolean isRelief);
    BitmapWrapperInterface iCompressTexture(Buffer input, int width, int height, int pixelSize, int stride);
    BitmapWrapperInterface iCreateETC1Texture(InputStream input) throws IOException;

}
