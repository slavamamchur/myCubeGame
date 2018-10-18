package com.sadgames.sysutils.common;

import org.luaj.vm2.LuaTable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public interface SysUtilsWrapperInterface {

    BitmapWrapperInterface iCreateColorBitmap(int color);
    BitmapWrapperInterface iDecodeImage(byte[] bitmapArray, boolean isRelief);
    BitmapWrapperInterface iCompressTexture(Buffer input, int width, int height, int pixelSize, int stride);
    BitmapWrapperInterface iCreateETC1Texture(InputStream input) throws IOException;

    Vector3f               mulMV(float[] matrix, float[] vector);
    Vector3f               mulMV(Matrix4f matrix, LuaTable vector);
    Vector3f               mulMV(float[] matrix, LuaTable vector);
    void                   mulMM(float[] result, int resultOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset);
    void rotateM(float[] m, float a, float x, float y, float z);

}
