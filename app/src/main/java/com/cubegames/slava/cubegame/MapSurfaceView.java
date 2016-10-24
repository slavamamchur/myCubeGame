package com.cubegames.slava.cubegame;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawMapViewDelegate drawMapViewDelegate;

    public MapSurfaceView(Context context, DrawMapViewDelegate drawMapViewDelegate) {
        super(context);

        this.drawMapViewDelegate = drawMapViewDelegate;
        getHolder().addCallback(this);
    }

    @Override
    public void draw(Canvas canvas) {
        //super.draw(canvas);

        if (drawMapViewDelegate != null )
            drawMapViewDelegate.onDrawMap(canvas);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = null;

        try {
            canvas = holder.lockCanvas(null);
            draw(canvas);
        }
        finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public interface DrawMapViewDelegate {
        void onDrawMap(Canvas canvas);
    }
}
