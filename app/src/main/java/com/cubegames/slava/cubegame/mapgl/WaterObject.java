package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;
import android.graphics.Bitmap;

import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType.WATER_OBJECT;

public class WaterObject extends ProceduralMeshObject {

    public WaterObject(Context context, int dimension, GLShaderProgram program) {
        super(context, WATER_OBJECT, 10.0f, 10.0f, dimension, program);
    }

    @Override
    protected float getValueY(float valX, float valZ, float tu, float tv) {
        return 0;
    }

    @Override
    protected Bitmap getTextureBitmap() {
        return null; //TODO: load from resource
    }
}
