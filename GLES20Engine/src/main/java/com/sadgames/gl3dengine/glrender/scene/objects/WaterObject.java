package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralSurfaceObject {

    public WaterObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program) {
        super(sysUtilsWrapper, WATER_OBJECT, null, SEA_SIZE_IN_WORLD_SPACE, program);

        //setCubeMap(true);
    }

    @Override
    protected float calculateLandScale(float landSize) {
        return 1.0f;
    }

    @Override
    protected float getYValue(float valX, float valZ, BitmapWrapperInterface map, float tu, float tv, int[] imgData) {
        return 0.0f;
    }

    @Override
    protected BitmapWrapperInterface getReliefMap() {
        return null;
    }

    @Override
    protected int getDimension(BitmapWrapperInterface bmp) {
        return FLAT_MAP_DEFAULT_DIMENSION;//250
    }

}
