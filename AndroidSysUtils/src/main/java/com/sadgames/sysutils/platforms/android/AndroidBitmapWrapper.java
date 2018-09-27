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

    public AndroidBitmapWrapper(Bitmap picture) {
        this.picture = picture;
    }

    public AndroidBitmapWrapper(ETC1Utils.ETC1Texture compressedPicture) {
        this.compressedPicture = compressedPicture;
    }

    @Override
    public int getImageSizeBytes() {
        return isCompressed() ?
                              ETC1.getEncodedDataSize(compressedPicture.getWidth(), compressedPicture.getHeight())
                              :
                              picture.getByteCount();
    }

    @Override
    public Buffer getRawData() {
        ByteBuffer imageBuffer;

        if (isCompressed())
            imageBuffer = compressedPicture.getData();
        else {
            imageBuffer = ByteBuffer.allocateDirect(picture.getByteCount()).order(ByteOrder.nativeOrder());
            picture.copyPixelsToBuffer(imageBuffer);
            imageBuffer.position(0);
        }

        return imageBuffer;
    }

    @Override
    public Buffer getDecodedRawData() {
        if (isCompressed()) {
            int width = compressedPicture.getWidth();
            int height = compressedPicture.getHeight();
            int stride = 3 * width;
            ByteBuffer decodedData = ByteBuffer.allocateDirect(stride * height).order(ByteOrder.nativeOrder());
            ETC1.decodeImage(compressedPicture.getData(), decodedData, width, height, 3, stride);

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
        return isCompressed() ? compressedPicture.getWidth() : picture != null ? picture.getWidth() : 0;
    }

    @Override
    public int getHeight() {
        return isCompressed() ? compressedPicture.getHeight() : picture != null ? picture.getHeight() : 0;
    }

    @Override
    public boolean isEmpty() {
        return picture == null && compressedPicture == null;
    }

    @Override
    public boolean isCompressed() {
        return compressedPicture != null;
    }

    @Override
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

        compressedPicture = null;
    }
}
