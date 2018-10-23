package com.sadgames.sysutils.common;

import java.nio.Buffer;

public interface BitmapWrapperInterface {

    Buffer  getRawData();
    Buffer  getDecodedRawData();

    int     getWidth();
    int     getHeight();
    int     getImageSizeBytes();
    String  getTextureName();

    boolean isEmpty();
    boolean isCompressed();

    void    release();

}
