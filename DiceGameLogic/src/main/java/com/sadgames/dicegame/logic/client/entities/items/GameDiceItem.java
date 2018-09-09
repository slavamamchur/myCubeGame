package com.sadgames.dicegame.logic.client.entities.items;

import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3f;

public class GameDiceItem extends Blender3DObject {

    private static final short BOX_SHAPE_TYPE = 1;
    private static final float GAME_DICE_HALF_SIZE = 0.15f;

    private static Map<Integer, Integer> DICE_FACES_VALUES = new HashMap<Integer, Integer>() {
        {
            put(68, 1);
            put(85, 2);
            put(17, 3);
            put( 0, 4);
            put(51, 5);
            put(34, 6);
        }
    };

    public GameDiceItem(SysUtilsWrapperInterface sysUtilsWrapper, String objFileName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, objFileName, program, mass, tag);

        setInitialScale(GAME_DICE_HALF_SIZE);
        setInitialTranslation(0f, 0.08f, 0f);
        setTwoSidedSurface(false);
        setCollisionShapeType(BOX_SHAPE_TYPE);
        setItemName(objFileName);
    }

    @SuppressWarnings("unused") public int getTopFaceDiceValue() {
        int result = 0;
        float max_y = 0f;
        float[] normals = raw3DModel.getNormals();

        for (int idx = 0; idx < normals.length / 3; idx++) {
            Vector3f normalVector =
                    getSysUtilsWrapper().mulMV(getModelMatrix(), new float[]{normals[idx * 3], normals[idx * 3 + 1], normals[idx * 3 + 2], 1.0f});

            if (normalVector.y > max_y) {
                max_y = normalVector.y;
                result = idx;
            }
        }

        Integer value = DICE_FACES_VALUES.get(result);
        return value == null ? 0 : value;
    }

}
