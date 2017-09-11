package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.linearmath.Transform;
import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.DICE_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VBO_STRIDE;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEX_SIZE;


public class DiceObject extends PNode {

    private short[] DICE_FACE_VALUES = {2, 1, 5, 6, 3, 4};

    private float[] normal;

    public DiceObject(Context context, GLShaderProgram program) {
        super(context, DICE_OBJECT, R.drawable.dice_texture, program, 10f, 1);
    }

    @Override
    protected int getDimension(Bitmap bmp) {
        return 0;
    }
    @Override
    public int getFacesCount() {
        return 0;
    }

    public float[] getNormal() {
        return normal;
    }

    @Override
    protected void createVertexesVBO() {
        final float[] vertexes = {
                // Front face
                -0.1f, 0.1f, 0.1f, 0.25f, 0.333f,
                -0.1f, -0.1f, 0.1f, 0.25f, 0.666f,
                0.1f, 0.1f, 0.1f, 0.5f, 0.333f,
                -0.1f, -0.1f, 0.1f, 0.25f, 0.666f,
                0.1f, -0.1f, 0.1f, 0.5f, 0.666f,
                0.1f, 0.1f, 0.1f, 0.5f, 0.333f,

                // Right face
                0.1f, 0.1f, 0.1f,  0.5f, 0.333f,
                0.1f, -0.1f, 0.1f, 0.5f, 0.666f,
                0.1f, 0.1f, -0.1f,  0.75f, 0.333f,
                0.1f, -0.1f, 0.1f, 0.5f, 0.666f,
                0.1f, -0.1f, -0.1f, 0.75f, 0.666f,
                0.1f, 0.1f, -0.1f, 0.75f, 0.333f,

                // Back face
                0.1f, 0.1f, -0.1f, 0.75f, 0.333f,
                0.1f, -0.1f, -0.1f, 0.75f, 0.666f,
                -0.1f, 0.1f, -0.1f, 1f, 0.333f,
                0.1f, -0.1f, -0.1f, 0.75f, 0.666f,
                -0.1f, -0.1f, -0.1f, 1f, 0.666f,
                -0.1f, 0.1f, -0.1f, 1f, 0.333f,

                // Left face
                -0.1f, 0.1f, -0.1f, 0f, 0.333f,
                -0.1f, -0.1f, -0.1f, 0f, 0.666f,
                -0.1f, 0.1f, 0.1f, 0.25f, 0.333f,
                -0.1f, -0.1f, -0.1f, 0f, 0.666f,
                -0.1f, -0.1f, 0.1f, 0.25f, 0.666f,
                -0.1f, 0.1f, 0.1f, 0.25f, 0.333f,

                // Top face
                -0.1f, 0.1f, -0.1f, 0.25f, 0f,
                -0.1f, 0.1f, 0.1f, 0.25f, 0.333f,
                0.1f, 0.1f, -0.1f,0.5f, 0f,
                -0.1f, 0.1f, 0.1f, 0.25f, 0.333f,
                0.1f, 0.1f, 0.1f, 0.5f, 0.333f,
                0.1f, 0.1f, -0.1f,0.5f, 0f,

                // Bottom face
                0.1f, -0.1f, -0.1f, 0.25f, 0.666f,
                0.1f, -0.1f, 0.1f, 0.25f, 1f,
                -0.1f, -0.1f, -0.1f, 0.5f, 0.666f,
                0.1f, -0.1f, 0.1f, 0.25f, 1f,
                -0.1f, -0.1f, 0.1f, 0.5f, 1f,
                -0.1f, -0.1f, -0.1f, 0.5f, 0.666f
    };

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        /** координаты вершин*/
        vertexData.position(0);
        getVertexVBO().setParamValue(VERTEX_SIZE, VBO_STRIDE, 0, vertexData);
        /** координаты текстур*/
        getTexelVBO().setParamValue(TEXEL_UV_SIZE, VBO_STRIDE, VERTEX_SIZE * 4, vertexData);
        vertexData.limit(0);

        createCollisionShape(vertexes);
}

    @Override
    protected void createCollisionShape(float[] vertexes) {
        _shape = new BoxShape(new Vector3f(0.1f, 0.1f, 0.1f));
    }

    @Override
    protected void createTexelsVBO() {

    }

    @Override
    protected void createNormalsVBO() {
        normal = new float[]
                {
                        // Front face
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        // Right face
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        // Back face
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        // Left face
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        // Top face
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        // Bottom face
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f
                };

        FloatBuffer normalData = ByteBuffer
                .allocateDirect(normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalData.put(normal);
        normalData.position(0);
        getNormalVBO().setParamValue(VERTEX_SIZE, 0, 0, normalData);
        normalData.limit(0);
    }

    @Override
    protected int createFacesIBO() {
        return 0;
    }

    public int getTopFaceDiceValue(Transform transform, float[] view_matrix) {
        int result = 0;
        float max_y = 0f;
        for (int i = 0; i < 6; i++) {
            int idx = i * 18;
            Vector3f normal_vector = new Vector3f(normal[idx], normal[idx + 1], normal[idx + 2]);

            //Matrix4f m = new Matrix4f(new float[16]);
            //transform.getMatrix(m);
            //m.transform(normal_vector);
            transform.transform(normal_vector);//TODO: ??? multithread protection
//            float[] glMat = new float[16];
//            transform.getOpenGLMatrix(glMat);
//            float[] mMatrix = new float[16];
//            Matrix.multiplyMM(mMatrix, 0, view_matrix, 0, glMat, 0);
            float ty = normal_vector.y;// transformVector4fy(mMatrix, normal_vector);
            if (ty > max_y) {
                max_y = ty;
                result = i;
            }
        }

        return DICE_FACE_VALUES[result];
    }

    private float transformVector4fy(float[] mat, Vector3f vec) {
        return vec.x * mat[4] + vec.y * mat[5] + vec.z * mat[6];
    }
}
