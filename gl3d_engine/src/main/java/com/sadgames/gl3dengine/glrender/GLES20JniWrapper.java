package com.sadgames.gl3dengine.glrender;

public class GLES20JniWrapper {

    private static final String GLES_20_JNI_WRAPPER_LIB = "gleswrapper";

    static {
        System.loadLibrary(GLES_20_JNI_WRAPPER_LIB);
    }

    public static native void glClear();
    public static native void glClearColor(float red, float green, float blue, float alpha);
    public static native void glViewport(int width, int height);
    public static native void glBlendFunc(int sfactor, int dfactor);
    public static native void glEnable(int cap);
    public static native void glDisable(int cap);

    public static native String glExtensions();

    public static native void glUseProgram(int id);

    public static native void glEnableFrontFacesCulling();
    public static native void glEnableBackFacesCulling();
    public static native void glEnableFacesCulling();
    public static native void glEnableDepthTest();

    public static native void glBindBuffer(int target, int buffer);
    public static native void glDeleteBuffers(int[] buffers);
    public static native void glGenBuffers(
            int n,
            int[] buffers
    );
    public static native void glBufferData(
            int target,
            int size,
            java.nio.Buffer data,
            int usage
    );

    public static native void glDrawArrays(
            int mode,
            int first,
            int count
    );

    public static native void glDrawElements(
            int mode,
            int count,
            int type
    );

    public static native void glBindFramebuffer(int id);
    public static native boolean glCheckFramebufferStatus();
    public static native void glFramebufferAttachDepthTexture(int textureId);
    public static native void glFramebufferAttachColorTexture(int textureId);
    public static native void glGenFrameBuffers(int[] framebuffers);
    public static native void glDeleteFrameBuffers(int[] framebuffers);

    public static native void glGenRenderBuffers(int[] renderbuffers);
    public static native void glDeleteRenderBuffers(int[] renderbuffers);
    public static native void glBindRenderBuffer(int id);
    public static native void glRenderBufferStorage(int width, int height);
    public static native void glFramebufferAttachDepthBuffer(int id);

    public static native void glGenTextures(int[] textures);
    public static native void glDeleteTextures(int[] textures);
    public static native void glActiveTexture(int slot);
    public static native void glBindTexture2D(int id);
    public static native void glBindTextureCube(int id);
    public static native void glTexImageDepth(int width, int height);
    public static native void glTexParameteri(int target, int pname, int param);
    public static native void glTexImage2D(
            int target,
            int level,
            int internalformat,
            int width,
            int height,
            int border,
            int format,
            int type,
            byte[] pixels
    );
    public static native void glCompressedTexImage2D(
            int target,
            int level,
            int internalformat,
            int width,
            int height,
            int border,
            int imageSize,
            java.nio.Buffer data
    );

    public static native int get_GL_TEXTURE_2D_value();
    public static native int get_GL_TEXTURE_CUBE_MAP_value();
    public static native int get_GL_TEXTURE_CUBE_MAP_POSITIVE_X_value();
    public static native int get_GL_TEXTURE_MIN_FILTER_value();
    public static native int get_GL_TEXTURE_MAG_FILTER_value();
    public static native int get_GL_TEXTURE_WRAP_S_value();
    public static native int get_GL_TEXTURE_WRAP_T_value();
    public static native int get_GL_NEAREST_value();
    public static native int get_GL_LINEAR_value();
    public static native int get_GL_BLEND_value();
    public static native int get_GL_REPEAT_value();
    public static native int get_GL_CLAMP_TO_EDGE_value();
    public static native int get_GL_RGBA_value();
    public static native int get_GL_UNSIGNED_BYTE_value();
    public static native int get_GL_UNSIGNED_SHORT_value();
    public static native int get_GL_SRC_ALPHA_value();
    public static native int get_GL_ONE_MINUS_SRC_ALPHA_value();
    public static native int get_GL_ELEMENT_ARRAY_BUFFER_value();
    public static native int get_GL_TRIANGLES_value();
    public static native int get_GL_TRIANGLE_STRIP_value();
    public static native int get_GL_CULL_FACE_value();
    public static native int get_GL_STATIC_DRAW_value();

    public static native int get_ETC1_RGB8_OES_value();

}
