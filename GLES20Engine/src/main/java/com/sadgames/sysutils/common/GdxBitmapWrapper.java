package com.sadgames.sysutils.common;

import com.badlogic.gdx.graphics.Pixmap;

public class GdxBitmapWrapper extends BitmapWrapper {

    protected GdxBitmapWrapper(Pixmap image) {
        super(image.getPixels(), image.getWidth(), image.getHeight(), false);
    }

    @Override
    public int[] asIntArray() {
        return new int[0];
    }

}
