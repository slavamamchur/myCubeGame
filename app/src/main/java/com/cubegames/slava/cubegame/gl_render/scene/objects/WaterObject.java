package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.Utils;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.WATER_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.SEA_SIZE_IN_WORLD_SPACE;

public class WaterObject extends ProceduralMeshObject {

    public WaterObject(Context context, GLShaderProgram program) {
        super(context, WATER_OBJECT, -1, SEA_SIZE_IN_WORLD_SPACE, program);

        setCubeMap(true);
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

    @Override
    protected int loadTexture() {
        return Utils.loadGLCubeMapTexture(context, new int[]{R.drawable.skybox_right,
                                                             R.drawable.skybox_left,
                                                             R.drawable.skybox_top,
                                                             R.drawable.skybox_bottom,
                                                             R.drawable.skybox_back,
                                                             R.drawable.skybox_front});
    }
}
