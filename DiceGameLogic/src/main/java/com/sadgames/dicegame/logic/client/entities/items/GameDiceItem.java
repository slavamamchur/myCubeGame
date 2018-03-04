package com.sadgames.dicegame.logic.client.entities.items;

import com.sadgames.gl3dengine.glrender.scene.objects.CubePrimitiveObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.DICE_MESH_OBJECT_1;

public class GameDiceItem extends CubePrimitiveObject { //TODO: Blender3DObject: import blender model -> "Dice" and recalculate top face value.

    public static final float GAME_DICE_HALF_SIZE = 0.15f;
    private static final float DICE_DEFAULT_WEIGHT = 10f;
    private static short[] DICE_FACE_VALUES = {2, 1, 5, 6, 3, 4};

    public GameDiceItem(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, String textureName) {
        super(sysUtilsWrapper, textureName, program, DICE_DEFAULT_WEIGHT, MOVING_OBJECT, GAME_DICE_HALF_SIZE);

        //this.initialScale = 0.25f;
        //this.initialTranslation = new Vector3f(0f, 0.08f, 0f);

        //setTwoSidedSurface(false);
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
