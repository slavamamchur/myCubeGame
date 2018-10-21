package com.sadgames.sysutils.common;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ETC1;

import org.luaj.vm2.LuaValue;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.vecmath.Vector2f;

public abstract class BitmapWrapper implements BitmapWrapperInterface {

    protected Buffer data;
    protected int sizeInBytes;
    protected int mWidth;
    protected int mHeight;
    protected boolean mCompressed;

    protected BitmapWrapper(ByteBuffer data, int width, int height, boolean compressed) {
        this.data = data;
        this.sizeInBytes = data != null ? data.capacity() : 0;
        this.mWidth = width;
        this.mHeight = height;
        this.mCompressed = compressed;
    }

    protected BitmapWrapper(ETC1Utils.ETC1Texture compressedPicture) {
        this(compressedPicture.getData(),
             compressedPicture.getWidth(),
             compressedPicture.getHeight(),
             true);
    }

    @Override
    public Buffer getRawData() {
        if (data != null)
            data.rewind();

        return data;
    }

    @Override
    public Buffer getDecodedRawData() {
        if (mCompressed)
            return decodeImage(data);
        else
            return getRawData();
    }

    @Override
    public abstract int[] asIntArray();

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
    public void release() {
        if (data != null) {
            data.limit(0);
            data = null;
        }
    }

    protected Vector2f getPoint(LuaValue value) {
        return (Vector2f)(LuaUtils.getUserData(value, Vector2f.class));
    }

    protected Buffer decodeImage(Buffer in) {
        return ETC1.decodeImage(new ETC1.ETC1Data(mWidth, mHeight, (ByteBuffer) in, 0),
                                Pixmap.Format.RGB888).getPixels();
    }

}
