package com.sadgames.sysutils.platforms.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.sadgames.gl3d_engine.gl_render.BitmapWrapperInterface;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import javax.vecmath.Vector2f;

public class AndroidBitmapWrapper implements BitmapWrapperInterface {

    private Bitmap picture;

    public AndroidBitmapWrapper(Bitmap picture) {
        this.picture = picture;
    }

    @Override
    public Buffer getRawData() {
        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(picture.getByteCount()).order(ByteOrder.nativeOrder());
        picture.copyPixelsToBuffer(imageBuffer);
        imageBuffer.position(0);

        return imageBuffer;
    }

    @Override
    public int getPixelColor(Point position) {
        return picture.getPixel(position.x, position.y);
    }

    @Override
    public int getWidth() {
        return picture.getWidth();
    }

    @Override
    public int getHeight() {
        return picture.getHeight();
    }

    @Override
    public boolean isEmpty() {
        return picture == null;
    }

    @Override
    public void drawPath(List<Vector2f> path, int pathColor, int wayPointColor) {
        if (path == null)
            return;

        Canvas canvas = new Canvas(picture);
        Path pathObject = new Path();
        final Paint paint = new Paint();

        paint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));
        pathObject.moveTo(path.get(0).x, path.get(0).y);
        for (int i = 1; i < path.size(); i++)
            pathObject.lineTo(path.get(i).x, path.get(i).y);
        paint.setColor(pathColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawPath(pathObject, paint);

        paint.setColor(wayPointColor);
        paint.setStyle(Paint.Style.FILL);
        for (Vector2f point : path)
            canvas.drawCircle(point.x, point.y, 10f, paint);
    }

    @Override
    public void release() {
        picture.recycle();
    }
}
