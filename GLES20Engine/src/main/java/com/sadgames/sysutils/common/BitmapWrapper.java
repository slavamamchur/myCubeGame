package com.sadgames.sysutils.common;

import org.luaj.vm2.LuaTable;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class BitmapWrapper implements BitmapWrapperInterface {

    protected Buffer data;
    protected int sizeInBytes;
    protected int mWidth;
    protected int mHeight;
    protected boolean mCompressed;

    protected BitmapWrapper(ByteBuffer data, int width, int height, boolean compressed) {
        this.data = data;
        this.sizeInBytes = data.capacity();
        this.mWidth = width;
        this.mHeight = height;
        this.mCompressed = compressed;
    }

    @Override
    public Buffer getRawData() {
        return data;
    }

    @Override
    public Buffer getDecodedRawData() {
        if (mCompressed) {
            ByteBuffer decodedData = ByteBuffer.allocateDirect(3 * mWidth * mHeight).order(ByteOrder.nativeOrder());
            decodeImage(data, decodedData);

            return decodedData;
        }
        else
            return getRawData();
    }

    @Override
    public abstract int[] asIntArray();

    @Override
    public int getPixelColor(int x, int y) {
        return asIntArray()[y * mWidth + x];
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getImageSizeBytes() {
        return sizeInBytes;
    }

    @Override
    public boolean isEmpty() {
        return data == null;
    }

    @Override
    public boolean isCompressed() {
        return mCompressed;
    }

    @Override
    public abstract void drawPath(LuaTable path, int pathColor, int wayPointColor, float scaleFactor);

    @Override
    public void release() {
        if (data != null) {
            data.limit(0);
            data = null;
        }
    }

    protected abstract void decodeImage(Buffer in, Buffer out);

}
