package com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.objects;

import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.gl3d_engine.utils.ISysUtilsWrapper;

import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralSurfaceObject {

    public WaterObject(ISysUtilsWrapper sysUtilsWrapper, GLShaderProgram program) {
        super(sysUtilsWrapper, WATER_OBJECT, -1, SEA_SIZE_IN_WORLD_SPACE, program);

        //setCubeMap(true);
    }

    @Override
    protected float getYValue(float valX, float valZ, Bitmap map, float tu, float tv) {
        return 0f;
    }

    @Override
    protected Bitmap getReliefMap() {
        return null;
    }

    @Override
    protected int getDimension(Bitmap bmp) {
        return 3;
    }//250
}
