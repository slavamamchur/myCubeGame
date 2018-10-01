package com.sadgames.sysutils.common;

import org.luaj.vm2.LuaTable;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class BitmapWrapper implements BitmapWrapperInterface {

    protected ByteBuffer pixels;
    protected int width;
    protected int height;
    protected boolean compressed;

    protected BitmapWrapper(ByteBuffer pixels, int width, int height, boolean compressed) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
        this.compressed = compressed;
    }

    @Override
    public Buffer getRawData() {
        return pixels;
    }

    @Override
    public Buffer getDecodedRawData() {
        if (compressed) {
            ByteBuffer decodedData = ByteBuffer.allocateDirect(3 * width * height).order(ByteOrder.nativeOrder());
            decodeImage(pixels, decodedData);

            return decodedData;
        }
        else
            return getRawData();
    }

    @Override
    public int[] asIntArray() {
        ByteBuffer data = compressed ? (ByteBuffer) getDecodedRawData() : pixels;

        return data.asIntBuffer().array();
    }

    @Override
    public int getPixelColor(int x, int y) {
        return asIntArray()[y * width + x];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getImageSizeBytes() {
        return compressed ? getEncodedDataSize(width, height) : pixels.capacity();
    }

    @Override
    public boolean isEmpty() {
        return pixels == null;
    }

    @Override
    public boolean isCompressed() {
        return compressed;
    }

    @Override
    public abstract void drawPath(LuaTable path, int pathColor, int wayPointColor, float scaleFactor);

    @Override
    public void release() {
        if (pixels != null) {
            pixels.limit(0);
            pixels = null;
        }
    }

    protected abstract void decodeImage(Buffer in, Buffer out);
    protected abstract int getEncodedDataSize(int width, int height);

}
