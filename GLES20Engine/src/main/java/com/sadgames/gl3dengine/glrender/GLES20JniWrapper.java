package com.sadgames.gl3dengine.glrender;

import com.badlogic.gdx.graphics.GL20;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;

public class GLES20JniWrapper {

    private static final String GLES_20_JNI_WRAPPER_LIB = "gleswrapper";

    static {
        System.loadLibrary(GLES_20_JNI_WRAPPER_LIB); //TODO: remove
    }

    private static GL20 glEngine;

    public static void setGlEngine(GL20 gl) {
        glEngine = gl;
    }

    public static void glClear() {
        glEngine.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void glClearColor(float red, float green, float blue, float alpha) {
        glEngine.glClearColor(red, green, blue, alpha);
    }

    public static void glViewport(int width, int height) {
        glEngine.glViewport(0, 0, width, height);
    }

    public static native void glBlendFunc(int sfactor, int dfactor);
    public static native void glEnable(int cap);
    public static native void glDisable(int cap);

    public static native String glExtensions();

    public static native void glUseProgram(int id);
    public static native void glAttachShader(int program, int shader);
    public static native void glCompileShader(int shader);
    public static native  int glCreateProgram();
    public static native  int glCreateShader(int type);
    public static native void glDeleteProgram(int program);
    public static native void glDeleteShader(int shader);
    public static native void glDetachShader(int program, int shader);
    public static native void glGetProgramiv(int program, int pname, int[] params);
    public static native void glGetShaderiv(int shader, int pname, int[] params);
    public static native void glLinkProgram(int program);
    public static native void glShaderSource(int shader, String string);

    public static native void glEnableFrontFacesCulling();
    public static native void glEnableBackFacesCulling();
    public static native void glEnableFacesCulling();
    public static native void glEnableDepthTest();
    public static native void glEnableVertexAttribArray(int index);
    public static native void glVertexAttribPointer(
            int indx,
            int size,
            int type,
            boolean normalized,
            int stride,
            int offset
    );
    private static native void glVertexAttribPointerBounds(
            int indx,
            int size,
            int type,
            boolean normalized,
            int stride,
            java.nio.Buffer ptr,
            int remaining
    );

    public static void glVertexAttribPointer(
            int indx,
            int size,
            int type,
            boolean normalized,
            int stride,
            java.nio.Buffer ptr
    ) {
        glVertexAttribPointerBounds(
                indx,
                size,
                type,
                normalized,
                stride,
                ptr,
                ptr.remaining()
        );
    }
    public static native int glGetAttribLocation(
            int program,
            String name
    );
    public static native int glGetUniformLocation(
            int program,
            String name
    );
    public static native void glUniform3fv(
            int location,
            int count,
            float[] v);
    public static native void glUniformMatrix4fv(
            int location,
            int count,
            boolean transpose,
            float[] value);
    public static native void glUniform1i(
            int location,
            int x
    );
    public static native void glUniform1f(
            int location,
            float x
    );

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
            java.nio.Buffer data
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
    public static native int get_GL_UNSIGNED_INT_value();
    public static native int get_GL_UNSIGNED_SHORT_value();
    public static native int get_GL_FLOAT_value();

    public static native int get_GL_SRC_ALPHA_value();
    public static native int get_GL_ONE_MINUS_SRC_ALPHA_value();

    public static native int get_GL_ELEMENT_ARRAY_BUFFER_value();
    public static native int get_GL_ARRAY_BUFFER_value();
    public static native int get_GL_TRIANGLES_value();
    public static native int get_GL_TRIANGLE_STRIP_value();
    public static native int get_GL_CULL_FACE_value();
    public static native int get_GL_STATIC_DRAW_value();

    public static native int get_ETC1_RGB8_OES_value();

    public static native int get_GL_COMPILE_STATUS_value();
    public static native int get_GL_LINK_STATUS_value();
    public static native int get_GL_VERTEX_SHADER_value();
    public static native int get_GL_FRAGMENT_SHADER_value();


}
