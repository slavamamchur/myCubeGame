package com.sadgames.dicegame.game_logic.items;

import com.sadgames.gl3d_engine.gl_render.scene.objects.GameItemObject;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.IBitmapWrapper;
import com.sadgames.sysutils.ISysUtilsWrapper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.GAME_ITEM_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.VBO_STRIDE;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.VERTEX_SIZE;
import static com.sadgames.sysutils.MathUtils.crossProduct;

public class ChipObject extends GameItemObject {

    private float[] vertexes;

    public ChipObject(ISysUtilsWrapper sysUtilsWrapper, GLShaderProgram program, int color) {
        super(sysUtilsWrapper, GAME_ITEM_OBJECT, program, color, 1f, COLLISION_OBJECT);
    }

    @Override
    protected int getDimension(IBitmapWrapper bmp) {
        return 0;
    }

    @Override
    public int getFacesCount() {
        return 12;
    }

    @Override
    protected void createVertexesVBO() {
        vertexes = new float[]{
                // Front face
               -0.05f, 0.0f, 0.05f,   0f,   1f,
                   0f, 0.1f,    0f, 0.5f, 0.5f,
                0.05f, 0.0f, 0.05f,   1f,   1f,

                // Right face
                0.05f, 0.0f, 0.05f,   1f,   1f,
                   0f, 0.1f,    0f, 0.5f, 0.5f,
                0.05f, 0.0f,-0.05f,   1f,   0f,

                // Back face
                0.05f, 0.0f,-0.05f,   1f,   0f,
                   0f, 0.1f,    0f, 0.5f, 0.5f,
               -0.05f, 0.0f,-0.05f,   0f,   0f,

                // Left face
               -0.05f, 0.0f,-0.05f,   0f,   0f,
                   0f, 0.1f,    0f, 0.5f, 0.5f,
               -0.05f, 0.0f, 0.05f,   0f,   1f

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
    protected void createTexelsVBO() {

    }

    @Override
    protected void createNormalsVBO() {
        int k = 0;
        float[] normal = new float[36];

        for (int i = 0; i < 4; i++) {
            int base0 = i * 15;
            int base1 = i * 15 + 5;
            int base2 = i * 15 + 10;

            float x0 = vertexes[base0];
            float y0 = vertexes[base0 + 1];
            float z0 = vertexes[base0 + 2];

            float x1 = vertexes[base1];
            float y1 = vertexes[base1 + 1];
            float z1 = vertexes[base1 + 2];

            float x2 = vertexes[base2];
            float y2 = vertexes[base2 + 1];
            float z2 = vertexes[base2 + 2];

            float[] cross = crossProduct(new float[]{x1 - x0, y1 - y0, z1 - z0}, new float[]{x2 - x0, y2 - y0, z2 - z0});

            for (int j = 0; j < 3; j++) {
                int base = i * 9 + j * 3;
                normal[base] = -cross[0];
                normal[base + 1] = -cross[1];
                normal[base + 2] = -cross[2];
            }
        }

        FloatBuffer normalData = ByteBuffer
                .allocateDirect(normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalData.put(normal);
        normalData.position(0);
        getNormalVBO().setParamValue(VERTEX_SIZE, 0, 0, normalData);
        normalData.limit(0);

        vertexes = null;
    }

    @Override
    protected int createFacesIBO() {
        short[] index = { 0, 2, 1, 3, 5, 4, 6, 8, 7, 9, 11, 10};

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

        /** do not clear for RAM - buffered objects!!! */
        indexData.limit(0);
        indexData = null;
        setIndexData(indexData);

        return buffers[0];
    }
}
