package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;

import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralMeshObject {

    public WaterObject(Context context, int dimension, GLShaderProgram program) {
        //TODO: Water texture and shader
        super(context, WATER_OBJECT, -1/**textureId*/, SEA_SIZE_IN_WORLD_SPACE, dimension, program);
    }

    @Override
    protected float getValueY(float valX, float valZ, int[] rowPixels, float tv) {
        return 0;
    }

}
