package com.sadgames.dicegame.logic.client.entities.items;

import com.bulletphysics.collision.shapes.BoxShape;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.DICE_MESH_OBJECT_1;

public class GameDiceItem extends Blender3DObject {

    public static final float GAME_DICE_HALF_SIZE = 0.15f;
    private static final float DICE_DEFAULT_WEIGHT = 10f;
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

    public GameDiceItem(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program) {
        super(sysUtilsWrapper, DICE_MESH_OBJECT_1, program, DICE_DEFAULT_WEIGHT, MOVING_OBJECT);

        this.initialScale = GAME_DICE_HALF_SIZE;
        this.initialTranslation = new Vector3f(0f, 0.08f, 0f);

        setTwoSidedSurface(false);
        setItemName(DICE_MESH_OBJECT_1);
    }

    @Override
    protected void createCollisionShape(float[] vertexes) {
        _shape = new BoxShape(new Vector3f(GAME_DICE_HALF_SIZE, GAME_DICE_HALF_SIZE, GAME_DICE_HALF_SIZE));
    }

    public int getTopFaceDiceValue() {
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
