package com.sadgames.sysutils.platforms.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.opengl.ETC1;

import com.sadgames.sysutils.common.BitmapWrapper;
import com.sadgames.sysutils.common.ETC1Utils;

import org.luaj.vm2.LuaTable;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.vecmath.Vector2f;

public class AndroidBitmapWrapper extends BitmapWrapper {

    private static Bitmap createColourBitmap(int color) {
        Bitmap bmp = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(color);

        return bmp;
    }

    AndroidBitmapWrapper(Bitmap picture) {
        super(getRawDataFromBitmap(picture),
              picture != null ? picture.getWidth() : 0,
              picture != null ? picture.getHeight() : 0,
             false);

        if (picture != null)
            picture.recycle();
    }

    AndroidBitmapWrapper(int color) {
        this(createColourBitmap(color));
    }

    AndroidBitmapWrapper(ETC1Utils.ETC1Texture compressedPicture) {
        super(compressedPicture);
    }

    private Bitmap getBitmap(Buffer data) {
        Bitmap result = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        data.rewind();
        result.copyPixelsFromBuffer(data);

        return result;
    }

    private static ByteBuffer getRawDataFromBitmap(Bitmap picture) {
        ByteBuffer rawData = null;

        if (picture != null) {
            rawData = ByteBuffer.allocateDirect(picture.getByteCount()).order(ByteOrder.nativeOrder());
            picture.copyPixelsToBuffer(rawData);
        }

        return rawData;
    }

    @Override
    protected void decodeImage(Buffer in, Buffer out) {
        ETC1.decodeImage(data, out, mWidth, mHeight, 3, 3 * mWidth);
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

}
