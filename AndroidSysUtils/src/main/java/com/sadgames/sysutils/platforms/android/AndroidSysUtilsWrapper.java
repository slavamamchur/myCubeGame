package com.sadgames.sysutils.platforms.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.CommonUtils;
import com.sadgames.sysutils.common.ETC1Utils;
import com.sadgames.sysutils.common.LuaUtils;
import com.sadgames.sysutils.common.MathUtils;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.luaj.vm2.LuaTable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public class AndroidSysUtilsWrapper implements SysUtilsWrapperInterface {

    private static final Object lockObject = new Object();
    protected static SysUtilsWrapperInterface instance = null;

    protected Context context;

    protected AndroidSysUtilsWrapper(Context context) {
        this.context = context;
    }

    public static SysUtilsWrapperInterface getInstance(Context context) {
        synchronized (lockObject) {
            instance = instance != null ? instance : new AndroidSysUtilsWrapper(context);
            return instance;
        }
    }

    /** Math    sysutils ---------------------------------------------------------------------------*/
    @Override
    public Vector3f mulMV(Matrix4f matrix, LuaTable vector) {
        return mulMV(MathUtils.getOpenGlMatrix(matrix), LuaUtils.luaTable2FloatArray(vector));
    }

    @Override
    public Vector3f mulMV(float[] matrix, LuaTable vector) {
        return mulMV(matrix, LuaUtils.luaTable2FloatArray(vector));
    }

    @Override
    public Vector3f mulMV(float[] matrix, float[] vector) {
        float[] result = new float[4];
        Matrix.multiplyMV(result, 0, matrix, 0, vector, 0);

        return new Vector3f(result[0], result[1], result[2]);
    }

    @Override
    public void mulMM(float[] result, int resultOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset) {
        Matrix.multiplyMM(result, resultOffset, lhs, lhsOffset, rhs, rhsOffset);
    }

    @Override
    public void rotateM(float[] m, int mOffset, float a, float x, float y, float z) {
        Matrix.rotateM(m, mOffset, a, x, y, z);
    }

    /** ------------------------------------------------------------------------------------------*/

    /** Bitmap sysutils ----------------------------------------------------------------------------*/
    @NonNull
    private static BitmapFactory.Options getiBitmapOptions() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        return options;
    }

    //TODO: save compressed to DB (draw path by OGL)
    private static BitmapWrapperInterface createETC1Texture(InputStream input) throws IOException {
        int width;
        int height;
        byte[] ioBuffer = new byte[32768];
        {
            if (input.read(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE) != ETC1.ETC_PKM_HEADER_SIZE) {
                throw new IOException("Unable to read PKM file header.");
            }
            ByteBuffer headerBuffer = ByteBuffer.allocateDirect(ETC1.ETC_PKM_HEADER_SIZE)
                    .order(ByteOrder.nativeOrder());
            headerBuffer.put(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE).position(0);
            if (!ETC1.isValid(headerBuffer)) {
                throw new IOException("Not a PKM file.");
            }
            width = ETC1.getWidth(headerBuffer);
            height = ETC1.getHeight(headerBuffer);
        }
        int encodedSize = ETC1.getEncodedDataSize(width, height);
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(ByteOrder.nativeOrder());
        for (int i = 0; i < encodedSize; ) {
            int chunkSize = Math.min(ioBuffer.length, encodedSize - i);
            if (input.read(ioBuffer, 0, chunkSize) != chunkSize) {
                throw new IOException("Unable to read PKM file data.");
            }
            dataBuffer.put(ioBuffer, 0, chunkSize);
            i += chunkSize;
        }
        dataBuffer.position(0);

        return new AndroidBitmapWrapper(new ETC1Utils.ETC1Texture(width, height, dataBuffer));
    }

    private static BitmapWrapperInterface compressTexture(Buffer input, int width, int height, int pixelSize, int stride){
        int encodedImageSize = ETC1.getEncodedDataSize(width, height);
        ByteBuffer compressedImage = ByteBuffer.allocateDirect(encodedImageSize).
                order(ByteOrder.nativeOrder());
        ETC1.encodeImage(input, width, height, pixelSize, stride, compressedImage);

        return new AndroidBitmapWrapper(new ETC1Utils.ETC1Texture(width, height, compressedImage));
    }

    private BitmapWrapperInterface decodeImage(byte[] bitmapArray, boolean isRelief) {
        if (bitmapArray != null) {
            int scaleFactor = TEXTURE_RESOLUTION_SCALE[getSettingsManager().getGraphicsQualityLevel().ordinal()];
            final BitmapFactory.Options options = getiBitmapOptions();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);

            options.inSampleSize = CommonUtils.calculateInSampleSize(
                    options.outWidth,
                    options.outHeight,
                    options.outWidth / scaleFactor,
                    options.outHeight / scaleFactor);
            options.inJustDecodeBounds = false;

            return new AndroidBitmapWrapper(BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options));
        }
        else
            return isRelief ? new AndroidBitmapWrapper((Bitmap) null) : null;
    }
    /** ------------------------------------------------------------------------------------------*/

    @Override
    public BitmapWrapperInterface iCreateColorBitmap(int color) {
        return new AndroidBitmapWrapper(color);
    }

    @Override
    public BitmapWrapperInterface iDecodeImage(byte[] bitmapArray, boolean isRelief) {
        return decodeImage(bitmapArray, isRelief);
    }

    @Override
    public BitmapWrapperInterface iCompressTexture(Buffer input, int width, int height, int pixelSize, int stride) {
        return compressTexture(input, width, height, pixelSize, stride);
    }

    @Override
    public BitmapWrapperInterface iCreateETC1Texture(InputStream input) throws IOException {
        return createETC1Texture(input);
    }

}
