package com.sadgames.gl3dengine.glrender.scene.objects;

import com.badlogic.gdx.graphics.Pixmap;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralSurfaceObject {

    public WaterObject(GLShaderProgram program) {
        super(WATER_OBJECT, null, SEA_SIZE_IN_WORLD_SPACE, program);

        //setCubeMap(true);
    }

    @Override
    protected float calculateLandScale(float landSize) {
        return 1.0f;
    }

    @Override
    protected float getYValue(float valX, float valZ, Pixmap map, float tu, float tv) {
        return 0.0f;
    }

    @Override
    protected Pixmap getReliefMap() {
        return null;
    }

    @Override
    protected int getDimension(Pixmap bmp) {
        return FLAT_MAP_DEFAULT_DIMENSION;//250
    }

}
