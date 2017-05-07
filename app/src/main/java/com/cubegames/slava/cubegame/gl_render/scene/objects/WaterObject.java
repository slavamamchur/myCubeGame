package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;

import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralMeshObject {

    public WaterObject(Context context, GLShaderProgram program, String mapID) {
        //TODO: Water texture and shader (same texture and changed shader -> not blue color 2 blue)
        super(context, WATER_OBJECT, mapID, LAND_SIZE_IN_WORLD_SPACE/*SEA_SIZE_IN_WORLD_SPACE*/, program);
    }

    @Override
    protected float getYValue(float valX, float valZ, int[] rowPixels, float tu, float tv) {
        return 0;
    }

}
