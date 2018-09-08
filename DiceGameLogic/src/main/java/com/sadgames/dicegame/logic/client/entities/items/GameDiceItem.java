package com.sadgames.dicegame.logic.client.entities.items;

import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.MathUtils;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.DICE_MESH_OBJECT_1;

public class GameDiceItem extends Blender3DObject {

    public static final float GAME_DICE_HALF_SIZE = 0.15f;
    private static final float DICE_DEFAULT_WEIGHT = 10f;
    private static final short BOX_SHAPE_TYPE = 1;

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

        setInitialScale(GAME_DICE_HALF_SIZE);
        setInitialTranslation(0f, 0.08f, 0f);
        setTwoSidedSurface(false);
        setCollisionShapeType(BOX_SHAPE_TYPE);
        setItemName(DICE_MESH_OBJECT_1);
    }

    @SuppressWarnings("unused") public void generateInitialTransform() {
        Random rnd = new Random(System.currentTimeMillis());
        Matrix4f transformer = new Matrix4f();
        Matrix4f transformingObject = new Matrix4f();

        transformingObject.setIdentity();
        transformer.setIdentity();

        transformer.setTranslation(new Vector3f(0f, 0.5f, 2.5f));
        transformingObject.mul(transformer);

        transformer.rotX((float) Math.toRadians(rnd.nextInt(4) * 90f));
        transformingObject.mul(transformer);

        transformer.rotY((float) Math.toRadians(rnd.nextInt(4) * 90f));
        transformingObject.mul(transformer);

        transformer.rotZ((float) Math.toRadians(rnd.nextInt(4) * 90f));
        transformingObject.mul(transformer);

        setPWorldTransform(transformingObject);
    }

    @SuppressWarnings("unused") public void generateForceVector() {
        Random rnd = new Random(System.currentTimeMillis());
        float fxz = 3.5f + rnd.nextInt(2) * 1f;
        float fy = fxz * 3f / 4f;
        float[] fVector = new float[] {0f, fy, -fxz, 1f};

        Matrix4f transform = new Matrix4f();
        transform.rotY((float) Math.toRadians(45.0 - rnd.nextInt(91) * 1.0));

        Vector3f force = sysUtilsWrapper.mulMV(MathUtils.getOpenGlMatrix(transform), fVector);
        get_body().setLinearVelocity(force);
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
