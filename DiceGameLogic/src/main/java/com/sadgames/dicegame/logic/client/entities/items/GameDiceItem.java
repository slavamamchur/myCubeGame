package com.sadgames.dicegame.logic.client.entities.items;

import com.bulletphysics.collision.shapes.BoxShape;
import com.sadgames.gl3dengine.glrender.scene.objects.CubePrimitiveObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.DICE_MESH_OBJECT_1;

public class GameDiceItem extends CubePrimitiveObject { //TODO: Blender3DObject: import blender model -> "Dice" and recalculate top face value.

    public static final float GAME_DICE_HALF_SIZE = 0.15f;
    private static final float DICE_DEFAULT_WEIGHT = 10f;
    private static Map<Integer, Integer> DICE_FACES_VALUES = new HashMap<Integer, Integer>() {
        {
            put(12, 1);
            put(0,  2);
            put(48, 3);
            put(60, 4);
            put(24, 5);
            put(36, 6);
        }
    };

    public GameDiceItem(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, String textureName) {
        super(sysUtilsWrapper, textureName, program, DICE_DEFAULT_WEIGHT, MOVING_OBJECT, GAME_DICE_HALF_SIZE);

        //this.initialScale = GAME_DICE_HALF_SIZE;
        //this.initialTranslation = new Vector3f(0f, 0.08f, 0f);

        //setTwoSidedSurface(false);
        setItemName(DICE_MESH_OBJECT_1);
    }

    @Override
    protected void createCollisionShape(float[] vertexes) {
        _shape = new BoxShape(new Vector3f(GAME_DICE_HALF_SIZE, GAME_DICE_HALF_SIZE, GAME_DICE_HALF_SIZE));
    }

    public int getTopFaceDiceValue() {
        int result = 0;
        float max_y = 0f;

        int idx;
        for (int i = 0; i < 6; i++) {
            idx = i * 12;
            Vector3f normal_vector =
                    getSysUtilsWrapper().mulMV(getModelMatrix(), new float[]{normal[idx], normal[idx + 1], normal[idx + 2], 1.0f});

            if (normal_vector.y > max_y) {
                max_y = normal_vector.y;
                result = idx;
            }
        }

        return DICE_FACES_VALUES.get(result);
    }
}
