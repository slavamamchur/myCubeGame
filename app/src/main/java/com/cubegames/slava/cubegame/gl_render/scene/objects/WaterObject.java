package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralMeshObject {

    public WaterObject(Context context, GLShaderProgram program, int res_id) {
        super(context, WATER_OBJECT, res_id, SEA_SIZE_IN_WORLD_SPACE, program);
    }

    @Override
    protected float getYValue(float valX, float valZ, Bitmap map, float tu, float tv) {
        return 0;
    }

    @Override
    protected Bitmap getReliefMap() {
        return null;
    }

    @Override
    protected int getDimension(Bitmap bmp) {
        return 250;
    }

}
