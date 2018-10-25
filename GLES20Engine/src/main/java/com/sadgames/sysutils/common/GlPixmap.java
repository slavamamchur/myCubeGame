package com.sadgames.sysutils.common;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ByteBuffer;

public class GlPixmap extends Pixmap {

    public GlPixmap(int width, int height, Format format) {
        super(width, height, format);
    }

    public GlPixmap(byte[] pixels, int width, int height, Format format) {
        this(width, height, format);

        BufferUtils.copy(pixels, 0, getPixels(), pixels.length);
    }

    public GlPixmap(ByteBuffer input, int width, int height, Format format) {
        this(input.array(), width, height, format);
    }

    @SuppressWarnings("unused") public GlPixmap(byte[] encodedData, int offset, int length) {
        super(encodedData, offset, length);
    }

    @Override
    public void setColor(int color) {
        super.setColor(ColorUtils.convert2libGDX(color));
    }

    @Override
    public int getPixel(int x, int y) {
        return ColorUtils.convert2libGDX(super.getPixel(x, y));
    }

    public static Pixmap createScaledTexture(Gdx2DPixmap src, int scale) {
        Gdx2DPixmap dst = src;

        if (scale != 1) {
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            int dstWidth = srcWidth / scale;
            int dstHeight = srcHeight / scale;
            dst = new Gdx2DPixmap(dstWidth, dstHeight, src.getFormat());

            dst.drawPixmap(src, 0, 0, srcWidth, srcHeight, 0, 0, dstWidth, dstHeight);
            src.dispose();
        }

        return new Pixmap(dst);
    }
}
