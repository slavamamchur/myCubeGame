package com.sadgames.gl3dengine.glrender.scene.objects.materials.textures;

import com.sadgames.gl3dengine.glrender.GLES20JniWrapper;
import com.sadgames.sysutils.common.BitmapWrapperInterface;

public class DepthTexture extends RGBATexture {

    public DepthTexture(int width, int height) {
        super(width, height);
    }

    @Override
    protected void loadTexture(BitmapWrapperInterface bitmap) throws UnsupportedOperationException {
        GLES20JniWrapper.glTexImageDepth(getWidth(), getHeight());
    }
}
