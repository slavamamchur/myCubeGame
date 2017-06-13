package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.gl_render.GLRenderConsts;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

public class ColorShapeObject extends ProceduralMeshObject {

    public ColorShapeObject(Context context, GLShaderProgram program, int color) {
        super(context, GLRenderConsts.GLObjectType.CHIP_OBJECT, 0.1f, program, color);
    }


    @Override
    protected int getDimension(Bitmap bmp) {
        return 2;
    }

    @Override
    protected float getYValue(float valX, float valZ, Bitmap map, float tu, float tv) {
        return (valX == valZ) && (valX == 0) ? 0.2f: 0.1f;
    }
    @Override
    protected Bitmap getReliefMap() {
        return null;
    }
}
