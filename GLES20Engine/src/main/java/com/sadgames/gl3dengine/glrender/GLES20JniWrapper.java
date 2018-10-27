package com.sadgames.gl3dengine.glrender;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;

import static com.badlogic.gdx.graphics.GL20.GL_BACK;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_CULL_FACE;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST;
import static com.badlogic.gdx.graphics.GL20.GL_EXTENSIONS;
import static com.badlogic.gdx.graphics.GL20.GL_FRONT;

public class GLES20JniWrapper {

    private static final String GLES_20_JNI_WRAPPER_LIB = "gleswrapper";

    static {
        System.loadLibrary(GLES_20_JNI_WRAPPER_LIB); //TODO: remove
    }

    private static GL20 glEngine;

    public static void setGlEngine(GL20 gl) {
        glEngine = gl;
    }

    public static IntBuffer intArray2Buffer(int[] array) {
        IntBuffer buffer = BufferUtils.newIntBuffer(array.length);
        BufferUtils.copy(array, 0, array.length, buffer);
        buffer.rewind();

        return buffer;
    }

    public static void intBuffer2Array(IntBuffer buffer, int[] array) {
        buffer.rewind();

        for (int i = 0; i < buffer.remaining(); i++)
            array[i] = buffer.get();

        buffer.limit(0);
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
    public static void glBlendFunc(int sfactor, int dfactor) {
        glEngine.glBlendFunc(sfactor, dfactor);
    }

    public static void glEnable(int cap) {
        glEngine.glEnable(cap);
    }
    public static void glDisable(int cap) {
        glEngine.glDisable(cap);
    }

    public static String glExtensions() {
        return glEngine.glGetString(GL_EXTENSIONS);
    }

    public static void glUseProgram(int id) {
        glEngine.glUseProgram(id);
    }
    public static void glAttachShader(int program, int shader) {
        glEngine.glAttachShader(program, shader);
    }
    public static void glCompileShader(int shader) {
        glEngine.glCompileShader(shader);
    }
    public static  int glCreateProgram() {
        return glEngine.glCreateProgram();
    }
    public static  int glCreateShader(int type) {
        return glEngine.glCreateShader(type);
    }
    public static void glDeleteProgram(int program) {
        glEngine.glDeleteProgram(program);
    }
    public static void glDeleteShader(int shader) {
        glEngine.glDeleteShader(shader);
    }
    public static void glDetachShader(int program, int shader) {
        glEngine.glDetachShader(program, shader);
    }
    public static void glGetProgramiv(int program, int pname, int[] params) {
        IntBuffer buffer = intArray2Buffer(params);
        glEngine.glGetProgramiv(program, pname, buffer);
        intBuffer2Array(buffer, params);
    }
    public static void glGetShaderiv(int shader, int pname, int[] params) {
        IntBuffer buffer = intArray2Buffer(params);
        glEngine.glGetShaderiv(shader, pname, buffer);
        intBuffer2Array(buffer, params);
    }
    public static void glLinkProgram(int program) {
        glEngine.glLinkProgram(program);
    }
    public static void glShaderSource(int shader, String string) {
        glEngine.glShaderSource(shader, string);
    }

    public static void glEnableFrontFacesCulling() {
        glEngine.glCullFace(GL_FRONT);
    }
    public static void glEnableBackFacesCulling() {
        glEngine.glCullFace(GL_BACK);
    }
    public static void glEnableFacesCulling() {
        glEnable(GL_CULL_FACE);
    }
    public static void glEnableDepthTest() {
        glEnable(GL_DEPTH_TEST);
    }
    public static void glEnableVertexAttribArray(int index) {
        glEngine.glEnableVertexAttribArray(index);
    }

    public static void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int offset) {
        glEngine.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
    }
    private static void glVertexAttribPointerBounds(int indx, int size, int type, boolean normalized, int stride, java.nio.Buffer ptr) {
        glEngine.glVertexAttribPointer(indx,
                size,
                type,
                normalized,
                stride,
                ptr);
    }
    public static void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, java.nio.Buffer ptr) {
        glVertexAttribPointerBounds(
                indx,
                size,
                type,
                normalized,
                stride,
                ptr
        );
    }

    public static int glGetAttribLocation(int program, String name) {
        return glEngine.glGetAttribLocation(program, name);
    }
    public static int glGetUniformLocation(int program, String name) {
        return glEngine.glGetUniformLocation(program, name);
    }

    public static void glUniform1i(int location, int value) {
        glEngine.glUniform1i(location, value);
    }
    public static void glUniform1f(int location, float value) {
        glEngine.glUniform1f(location, value);
    }
    public static void glUniform3fv(int location, int count, float[] value) {
        glEngine.glUniform3fv(location, count, value, 0);
    }
    public static void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value) {
        glEngine.glUniformMatrix4fv(location, count, transpose, value, 0);
    }

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
