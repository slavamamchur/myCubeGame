package com.sadgames.gl3dengine.glrender;

public class GLES20JniWrapper {

    public static final String GLES_20_JNI_WRAPPER_LIB = "gleswrapper";

    static {
        System.loadLibrary(GLES_20_JNI_WRAPPER_LIB);
    }

    public static native void glClear();
    public static native void glClearColor(float red, float green, float blue, float alpha);

}
