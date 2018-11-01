package com.sadgames.gl3dengine.glrender;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

import static com.badlogic.gdx.graphics.GL20.GL_BACK;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_ATTACHMENT0;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_CULL_FACE;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_ATTACHMENT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_COMPONENT;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_COMPONENT16;
import static com.badlogic.gdx.graphics.GL20.GL_DEPTH_TEST;
import static com.badlogic.gdx.graphics.GL20.GL_EXTENSIONS;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_COMPLETE;
import static com.badlogic.gdx.graphics.GL20.GL_FRONT;
import static com.badlogic.gdx.graphics.GL20.GL_RENDERBUFFER;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE0;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_2D;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE_CUBE_MAP;
import static com.badlogic.gdx.graphics.GL20.GL_UNSIGNED_INT;

public class GLES20JniWrapper {

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

    public static void glBindBuffer(int target, int buffer) {
        glEngine.glBindBuffer(target, buffer);
    }
    public static void glBufferData(int target, int size, java.nio.Buffer data, int usage) {
        glEngine.glBufferData(target, size, data, usage);
    }
    public static void glDeleteBuffers(int[] buffers) {
        glEngine.glDeleteBuffer(buffers[0]);
    }
    public static void glGenBuffers(int n, int[] buffers) {
        buffers[0] = glEngine.glGenBuffer();
    }

    public static void glDrawArrays(int mode, int first, int count) {
        glEngine.glDrawArrays(mode, first, count);
    }

    public static void glDrawElements(int mode, int count, int type) {
        if (glEngine.getClass().getSimpleName().contains("LwjglGL20"))
            GL11.glDrawElements(mode, count, type, 0);
        else
            glEngine.glDrawElements(mode, count, type, null);
    }

    public static void glBindFramebuffer(int id) {
        glEngine.glBindFramebuffer(GL_FRAMEBUFFER, id);
    }
    public static boolean glCheckFramebufferStatus() {
        return glEngine.glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    }
    public static void glFramebufferAttachDepthTexture(int textureId) {
        glEngine.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, textureId, 0);
    }
    public static void glFramebufferAttachColorTexture(int textureId) {
        glEngine.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);
    }
    public static void glGenFrameBuffers(int[] framebuffers) {
        framebuffers[0] = glEngine.glGenFramebuffer();
    }
    public static void glDeleteFrameBuffers(int[] framebuffers) {
        glEngine.glDeleteFramebuffer(framebuffers[0]);
    }

    public static void glGenRenderBuffers(int[] renderbuffers) {
        renderbuffers[0] = glEngine.glGenRenderbuffer();
    }
    public static void glDeleteRenderBuffers(int[] renderbuffers) {
        glEngine.glDeleteRenderbuffer(renderbuffers[0]);
    }
    public static void glBindRenderBuffer(int id) {
        glEngine.glBindRenderbuffer(GL_RENDERBUFFER, id);
    }
    public static void glRenderBufferStorage(int width, int height) {
        glEngine.glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);
    }
    public static void glFramebufferAttachDepthBuffer(int id) {
        glEngine.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, id);
    }

    public static void glGenTextures(int[] textures) {
        textures[0] = glEngine.glGenTexture();
    }
    public static void glDeleteTextures(int[] textures) {
        glEngine.glDeleteTexture(textures[0]);
    }
    public static void glActiveTexture(int slot) {
        glEngine.glActiveTexture(GL_TEXTURE0 + slot);
    }
    public static void glBindTexture2D(int id) {
        glEngine.glBindTexture(GL_TEXTURE_2D, id);
    }
    public static void glBindTextureCube(int id) {
        glEngine.glBindTexture(GL_TEXTURE_CUBE_MAP, id);
    }
    public static void glTexParameteri(int target, int pname, int param) {
        glEngine.glTexParameteri(target, pname, param);
    }
    public static void glTexImageDepth(int width, int height) {
        glEngine.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, null);
    }
    public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, java.nio.Buffer data) {
        glEngine.glTexImage2D(target, level, internalformat, width, height, border, format, type, data);
    }
    public static void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, java.nio.Buffer data) {
        glEngine.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
    }

}
