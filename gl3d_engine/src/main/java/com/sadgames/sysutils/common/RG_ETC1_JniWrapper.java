package com.sadgames.sysutils.common;

public class RG_ETC1_JniWrapper {

    private static final String RG_ETC1_JNI_WRAPPER_LIB = "rgetc1wrapper";

    static {
        System.loadLibrary(RG_ETC1_JNI_WRAPPER_LIB);
    }

    public static native void packETC1BlockInit();
    public static native int packETC1Block(int[] etc1_block, final int[] src_pixels_rgba);
    public static native boolean unpackETC1Block(final int[] etc1_block, int[] dst_pixels_rgba/*, boolean preserve_alpha = false*/);
}
