package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;

import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralMeshObject {

    public WaterObject(Context context, int dimension, GLShaderProgram program) {
        //TODO: Water texture and shader
        super(context, WATER_OBJECT, -1/**textureId*/, SEA_SIZE_IN_WORLD_SPACE, dimension, program);
    }

    @Override
    protected float getYValue(float valX, float valZ, int[] rowPixels, float tv) {
        return 0;
    }



}
