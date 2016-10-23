package com.cubegames.slava.cubegame;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {

    private DrawMapViewDelegate drawMapViewDelegate = null;

    public MapView(Context context, DrawMapViewDelegate drawMapViewDelegate) {
        super(context);

        this.drawMapViewDelegate = drawMapViewDelegate;
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode())
            return;
        else if (drawMapViewDelegate != null )
            drawMapViewDelegate.onDraw(canvas);
    }

    public interface DrawMapViewDelegate {
        void onDraw(Canvas canvas);
    }
}
