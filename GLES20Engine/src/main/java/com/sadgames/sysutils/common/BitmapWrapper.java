package com.sadgames.sysutils.common;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.glutils.ETC1;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static com.badlogic.gdx.graphics.g2d.Gdx2DPixmap.GDX2D_FORMAT_RGBA8888;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public class BitmapWrapper {

    protected ByteBuffer data;
    protected int sizeInBytes;
    protected int mWidth;
    protected int mHeight;
    protected boolean mCompressed;
    private Pixmap pixmap = null;

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

        pixmap = image;
    }

    public BitmapWrapper(int color) {
        this(createColourBitmap(color));
    }

    public BitmapWrapper(byte[] encodedImage) throws IOException { //TODO: RGB888
        this(GlPixmap.createScaledTexture(new Gdx2DPixmap(encodedImage, 0, encodedImage.length, GDX2D_FORMAT_RGBA8888),
                                          TEXTURE_RESOLUTION_SCALE[getSettingsManager().getGraphicsQualityLevel().ordinal()]));
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

    public String getTextureName() {
        return getName();
    }

    public Buffer getRawData() {
        if (data != null)
            data.rewind();

        return data;
    }

    @SuppressWarnings("unused") public Buffer getDecodedRawData() {
        if (mCompressed)
            return decodeImage(data);
        else
            return getRawData();
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getImageSizeBytes() {
        return sizeInBytes;
    }

    public boolean isEmpty() {
        return data == null;
    }

    public boolean isCompressed() {
        return mCompressed;
    }

    public void release() {
        try {
            if (pixmap != null)
                pixmap.dispose();
        }
        catch (GdxRuntimeException e) { e.printStackTrace(); }
        finally { pixmap = null; }

        if (data != null) {
            data.limit(0);
            data = null;
        }
    }

    protected Buffer decodeImage(ByteBuffer in) {
        return ETC1.decodeImage(new ETC1.ETC1Data(mWidth, mHeight, in, 0), Pixmap.Format.RGB888).getPixels();
    }

}
