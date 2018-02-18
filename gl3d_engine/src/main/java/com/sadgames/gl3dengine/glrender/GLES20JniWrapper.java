package com.sadgames.gl3dengine.glrender;

public class GLES20JniWrapper {

    private static final String GLES_20_JNI_WRAPPER_LIB = "gleswrapper";

    static {
        System.loadLibrary(GLES_20_JNI_WRAPPER_LIB);
    }

    public static native void glClear();
    public static native void glClearColor(float red, float green, float blue, float alpha);
    public static native void glViewport(int width, int height);

    public static native String glExtensions();

    public static native void glUseProgram(int id);

    public static native void glEnableFrontFacesCulling();
    public static native void glEnableBackFacesCulling();
    public static native void glEnableFacesCulling();
    public static native void glEnableDepthTest();

    public static native void glBindFramebuffer(int id);
    public static native boolean glCheckFramebufferStatus();
    public static native void glFramebufferAttachDepthTexture(int textureId);
    public static native void glFramebufferAttachColorTexture(int textureId);
    public static native void glGenFrameBuffers(int[] framebuffers);
    public static native void glDeleteFrameBuffers(int[] framebuffers);

    public static native void glGenTextures(int[] textures);
    public static native void glDeleteTextures(int[] textures);
    public static native void glActiveTexture(int slot);
    public static native void glBindTexture2D(int id);
    public static native void glBindTextureCube(int id);
}
