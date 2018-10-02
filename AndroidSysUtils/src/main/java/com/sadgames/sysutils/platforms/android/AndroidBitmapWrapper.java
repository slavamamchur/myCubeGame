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

    private Bitmap picture = null;
    private ETC1Utils.ETC1Texture compressedPicture = null;
    private int sizeInBytes;
    private int mWidth;
    private int mHeight;

    AndroidBitmapWrapper(Bitmap picture) {
        /*ByteBuffer rawData = ByteBuffer.allocateDirect(sizeInBytes).order(ByteOrder.nativeOrder());
        picture.copyPixelsToBuffer(rawData);
        picture.recycle();*/

        this.picture = picture;
        mWidth = picture.getWidth();
        mHeight = picture.getHeight();
        sizeInBytes = picture.getByteCount();
    }

    AndroidBitmapWrapper(ETC1Utils.ETC1Texture compressedPicture) {
        this.compressedPicture = compressedPicture;
        mWidth = compressedPicture.getWidth();
        mHeight = compressedPicture.getHeight();
        sizeInBytes = ETC1.getEncodedDataSize(mWidth, mHeight);
    }

    private Bitmap getBitmap(Buffer data) {
        Bitmap result = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        data.rewind();
        result.copyPixelsFromBuffer(data);

        return result;
    }

    @Override
    public int getImageSizeBytes() {
        return sizeInBytes;
    }

    @Override
    public Buffer getRawData() {
        Buffer rawData;

        if (isCompressed())
            rawData = compressedPicture.getData();
        else {
            rawData = ByteBuffer.allocateDirect(sizeInBytes).order(ByteOrder.nativeOrder());
            picture.copyPixelsToBuffer(rawData);

            rawData.position(0);
        }

        return rawData;
    }

    @Override
    public int[] asIntArray() {
        int[] result = null;

        if (!isCompressed()) {
            result = new int[mWidth * mHeight];
            picture.getPixels(result, 0, mWidth, 0, 0, mWidth, mHeight);
        }

        return result;
    }

    @Override
    public Buffer getDecodedRawData() {
        if (isCompressed()) {
            int stride = 3 * mWidth;
            ByteBuffer decodedData = ByteBuffer.allocateDirect(stride * mHeight).order(ByteOrder.nativeOrder());
            ETC1.decodeImage(compressedPicture.getData(), decodedData, mWidth, mHeight, 3, stride);

            return decodedData;
        }
        else
            return getRawData();
    }

    @Override
    public int getPixelColor(int x, int y) {
        return isCompressed() ? 0 : picture.getPixel(x, y);
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
        return picture == null && compressedPicture == null;
    }

    @Override
    public boolean isCompressed() {
        return compressedPicture != null;
    }

    @Override //TODO: draw via blending map
    public void drawPath(LuaTable path, int pathColor, int wayPointColor, float scaleFactor) {
        if (path == null || isCompressed())
            return;

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
    }

    private Vector2f getPoint(LuaValue value) {
        return (Vector2f)(LuaUtils.getUserData(value, Vector2f.class));
    }

    @Override
    public void release() {
        if (picture != null)
            picture.recycle();

        if (compressedPicture != null)
            compressedPicture.getData().limit(0);
    }
}
