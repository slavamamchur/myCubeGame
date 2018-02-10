package com.sadgames.dicegame.game_logic.items;

import android.graphics.Bitmap;

import com.bulletphysics.collision.shapes.BoxShape;
import com.sadgames.dicegame.gl3d_engine.gl_render.scene.objects.GameItemObject;
import com.sadgames.dicegame.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.dicegame.gl3d_engine.utils.ISysUtilsWrapper;
import com.sadgames.dicegame.gl3d_engine.utils.MathUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.game_logic.GameConsts.DICE_TEXTURE;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.GAME_ITEM_OBJECT;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.VBO_STRIDE;
import static com.sadgames.dicegame.gl3d_engine.gl_render.GLRenderConsts.VERTEX_SIZE;


public class DiceObject extends GameItemObject {

    private short[] DICE_FACE_VALUES = {2, 1, 5, 6, 3, 4};

    private float[] normal;

    public DiceObject(ISysUtilsWrapper sysUtilsWrapper, GLShaderProgram program) {
        super(sysUtilsWrapper, GAME_ITEM_OBJECT, DICE_TEXTURE, program, 10f, MOVING_OBJECT);
    }

    @Override
    protected void initItem() {
        setItemName(DICE_MESH_OBJECT_1);
    }

    @Override
    protected int getDimension(Bitmap bmp) {
        return 0;
    }
    @Override
    public int getFacesCount() {
        return 36;
    } //24

    public float[] getNormal() {
        return normal;
    }

    @Override
    protected void createVertexesVBO() { //TODO: wrong order ???
        final float[] vertexes = {
                // Front face
                -0.1f, 0.1f, 0.1f, 0.25f, 0.333f,
                -0.1f, -0.1f, 0.1f, 0.25f, 0.666f,
                0.1f, 0.1f, 0.1f, 0.5f, 0.333f,
                -0.1f, -0.1f, 0.1f, 0.25f, 0.666f,//
                0.1f, -0.1f, 0.1f, 0.5f, 0.666f,
                0.1f, 0.1f, 0.1f, 0.5f, 0.333f,//

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

        /*short[] index = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27 ,28, 29, 30, 31, 32, 33, 34, 35 };

        ShortBuffer indexData = ByteBuffer
                .allocateDirect(index.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indexData.put(index);
        final int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        indexData.position(0);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData.capacity() * 2, indexData, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        indexData.limit(0);
        indexData = null;
        setIndexData(indexData);

        return buffers[0];*/
    }

    public int getTopFaceDiceValue(/*Transform transform*/) {
        int result = 0;
        float max_y = 0f;

        for (int i = 0; i < 6; i++) {
            int idx = i * 18;//12
            Vector3f normal_vector = MathUtils.mulMV(getModelMatrix(), new float[]{normal[idx], normal[idx + 1], normal[idx + 2], 1.0f});
            //new Vector3f(normal[idx], normal[idx + 1], normal[idx + 2]);
            //transform.transform(normal_vector);

            float ty = normal_vector.y;
            if (ty > max_y) {
                max_y = ty;
                result = i;
            }
        }

        return DICE_FACE_VALUES[result];
    }



    @Override
    public void render() {
        super.render();
        //glDrawElements(GL_TRIANGLE_STRIP, getFacesCount(), GL_UNSIGNED_SHORT, 0); //TODO: GL_TRIANGLE_FAN ???
    }
}
