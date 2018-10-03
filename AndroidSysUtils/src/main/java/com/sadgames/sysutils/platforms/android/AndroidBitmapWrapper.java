package com.sadgames.sysutils.platforms.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.opengl.ETC1;

import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.ETC1Utils;
import com.sadgames.sysutils.common.LuaUtils;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.vecmath.Vector2f;

public class AndroidBitmapWrapper implements BitmapWrapperInterface {

    protected Buffer data;
    protected int sizeInBytes;
    protected int mWidth;
    protected int mHeight;
    protected boolean mCompressed;

    AndroidBitmapWrapper(Bitmap picture) {
        data = getRawDataFromBitmap(picture);
        mWidth = picture.getWidth();
        mHeight = picture.getHeight();
        sizeInBytes = picture.getByteCount();
        mCompressed = false;
    }

    AndroidBitmapWrapper(ETC1Utils.ETC1Texture compressedPicture) {
        data = compressedPicture.getData();
        mWidth = compressedPicture.getWidth();
        mHeight = compressedPicture.getHeight();
        sizeInBytes = compressedPicture.getData().capacity();
        mCompressed = true;
    }

    private Bitmap getBitmap(Buffer data) {
        Bitmap result = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        data.rewind();
        result.copyPixelsFromBuffer(data);

        return result;
    }

    private Buffer getRawDataFromBitmap(Bitmap picture) {
        Buffer rawData = ByteBuffer.allocateDirect(picture.getByteCount()).order(ByteOrder.nativeOrder());
        picture.copyPixelsToBuffer(rawData);

        return rawData;
    }

    @Override
    public int getImageSizeBytes() {
        return sizeInBytes;
    }

    @Override
    public Buffer getRawData() {
        if (data != null)
            data.rewind();

        return data;
    }

    @Override
    public int[] asIntArray() {
        int[] result = null;

        if (!mCompressed) {
            result = new int[mWidth * mHeight];
            Bitmap picture = getBitmap(data);

            picture.getPixels(result, 0, mWidth, 0, 0, mWidth, mHeight);
            picture.recycle();
        }

        return result;
    }

    @Override
    public Buffer getDecodedRawData() {
        if (mCompressed) {
            int stride = 3 * mWidth;
            ByteBuffer decodedData = ByteBuffer.allocateDirect(stride * mHeight).order(ByteOrder.nativeOrder());
            ETC1.decodeImage(data, decodedData, mWidth, mHeight, 3, stride);

            return decodedData;
        }
        else
            return getRawData();
    }

    @Override
    public int getPixelColor(int x, int y) {
        return mCompressed ? 0 : asIntArray()[y * mWidth + x];
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public boolean isEmpty() {
        return data == null;
    }

    @Override
    public boolean isCompressed() {
        return mCompressed;
    }

    @Override //TODO: draw via blending map
    public void drawPath(LuaTable path, int pathColor, int wayPointColor, float scaleFactor) {
        if (path == null || mCompressed)
            return;

        Bitmap picture = getBitmap(data);
        Canvas canvas = new Canvas(picture);
        Path pathObject = new Path();
        final Paint paint = new Paint();
        paint.setPathEffect(new DashPathEffect(new float[]{7.5f * scaleFactor, 3.75f * scaleFactor}, 0));

        Vector2f point = getPoint(path.get(1));
        pathObject.moveTo(point.x, point.y);

        for (int i = 2; i <= path.keys().length; i++) {
            point = getPoint(path.get(i));
            pathObject.lineTo(point.x, point.y);
        }

        paint.setColor(pathColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.75f * scaleFactor);
        canvas.drawPath(pathObject, paint);

        paint.setColor(wayPointColor);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 1; i <= path.keys().length; i++) {
            point = getPoint(path.get(i));
            canvas.drawCircle(point.x, point.y, 7.5f * scaleFactor, paint);
        }

        data = getRawDataFromBitmap(picture);
        picture.recycle();
    }

    private Vector2f getPoint(LuaValue value) {
        return (Vector2f)(LuaUtils.getUserData(value, Vector2f.class));
    }

    @Override
    public void release() {
        if (data != null) {
            data.limit(0);
            data = null;
        }
    }
}
