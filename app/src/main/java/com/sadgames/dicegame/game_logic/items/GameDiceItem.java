package com.sadgames.dicegame.game_logic.items;

import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.scene.objects.CubePrimitiveObject;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;

import javax.vecmath.Vector3f;

import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;


public class GameDiceItem extends CubePrimitiveObject {

    public static final float GAME_DICE_HALF_SIZE = 0.1f;
    private short[] DICE_FACE_VALUES = {2, 1, 5, 6, 3, 4};

    public GameDiceItem(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, String textureName) {
        super(sysUtilsWrapper, textureName, program, 10f, MOVING_OBJECT, GAME_DICE_HALF_SIZE);
    }

    @Override
    protected void initItem() {
        setItemName(DICE_MESH_OBJECT_1);
    }

    public int getTopFaceDiceValue() {
        int result = 0;
        float max_y = 0f;

        for (int i = 0; i < 6; i++) {
            int idx = i * 12;
            Vector3f normal_vector =
                    getSysUtilsWrapper().mulMV(getModelMatrix(), new float[]{normal[idx], normal[idx + 1], normal[idx + 2], 1.0f});

            if (normal_vector.y > max_y) {
                max_y = normal_vector.y;
                result = i;
            }
        }

        return DICE_FACE_VALUES[result];
    }
}
