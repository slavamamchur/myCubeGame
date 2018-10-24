package com.sadgames.sysutils.common;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.glutils.ETC1;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static com.badlogic.gdx.graphics.g2d.Gdx2DPixmap.GDX2D_FORMAT_RGBA8888;

public class BitmapWrapper implements BitmapWrapperInterface {

    protected ByteBuffer data;
    protected int sizeInBytes;
    protected int mWidth;
    protected int mHeight;
    protected boolean mCompressed;

    private String name = "";

    protected BitmapWrapper(ByteBuffer data, int width, int height, boolean compressed) {
        this.data = data;
        this.sizeInBytes = data != null ? data.capacity() : 0;
        this.mWidth = width;
        this.mHeight = height;
        this.mCompressed = compressed;
    }

    public BitmapWrapper(ETC1Utils.ETC1Texture compressedPicture) {
        this(compressedPicture.getData(),
             compressedPicture.getWidth(),
             compressedPicture.getHeight(),
             true);
    }

    public BitmapWrapper(Pixmap image) {
        this(image != null ? image.getPixels() : null,
             image != null ? image.getWidth() : 0,
             image != null ? image.getHeight() : 0,
             false);
    }

    public BitmapWrapper(int color) {
        this(createColourBitmap(color));
    }

    public BitmapWrapper(byte[] encodedImage) throws IOException {
        this(new Pixmap(new Gdx2DPixmap(encodedImage, 0, encodedImage.length, GDX2D_FORMAT_RGBA8888))); //TODO: RGB888 and scale image by settings
    }

    protected static Pixmap createColourBitmap(int color) {
        return CommonUtils.createPixmap(2, 2, color, Pixmap.Format.RGBA8888);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTextureName() {
        return getName();
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

    protected Buffer decodeImage(ByteBuffer in) {
        return ETC1.decodeImage(new ETC1.ETC1Data(mWidth, mHeight, in, 0), Pixmap.Format.RGB888).getPixels();
    }

}
