package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.DICE_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VBO_STRIDE;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.VERTEX_SIZE;


public class DiceObject extends BitmapTexturedObject {

    public DiceObject(Context context, GLShaderProgram program) {
        super(context, DICE_OBJECT, R.drawable.dice_texture, program);
    }

    @Override
    protected int getDimension(Bitmap bmp) {
        return 0;
    }
    @Override
    public int getFacesCount() {
        return 0;
    }

    @Override
    protected void createVertexesVBO() {
        final float[] vertexes = {
                // Front face
                -1.0f, 1.0f, 1.0f, 0.25f, 0.333f,
                -1.0f, -1.0f, 1.0f, 0.25f, 0.666f,
                1.0f, 1.0f, 1.0f, 0.5f, 0.333f,
                -1.0f, -1.0f, 1.0f, 0.25f, 0.666f,
                1.0f, -1.0f, 1.0f, 0.5f, 0.666f,
                1.0f, 1.0f, 1.0f, 0.5f, 0.333f,

                // Right face
                1.0f, 1.0f, 1.0f,  0.5f, 0.333f,
                1.0f, -1.0f, 1.0f, 0.5f, 0.666f,
                1.0f, 1.0f, -1.0f,  0.75f, 0.333f,
                1.0f, -1.0f, 1.0f, 0.5f, 0.666f,
                1.0f, -1.0f, -1.0f, 0.75f, 0.666f,
                1.0f, 1.0f, -1.0f, 0.75f, 0.333f,

                // Back face
                1.0f, 1.0f, -1.0f, 0.75f, 0.333f,
                1.0f, -1.0f, -1.0f, 0.75f, 0.666f,
                -1.0f, 1.0f, -1.0f, 1f, 0.333f,
                1.0f, -1.0f, -1.0f, 0.75f, 0.666f,
                -1.0f, -1.0f, -1.0f, 1f, 0.666f,
                -1.0f, 1.0f, -1.0f, 1f, 0.333f,

                // Left face
                -1.0f, 1.0f, -1.0f, 0f, 0.333f,
                -1.0f, -1.0f, -1.0f, 0f, 0.666f,
                -1.0f, 1.0f, 1.0f, 0.25f, 0.333f,
                -1.0f, -1.0f, -1.0f, 0f, 0.666f,
                -1.0f, -1.0f, 1.0f, 0.25f, 0.666f,
                -1.0f, 1.0f, 1.0f, 0.25f, 0.333f,

                // Top face
                -1.0f, 1.0f, -1.0f, 0.25f, 0f,
                -1.0f, 1.0f, 1.0f, 0.25f, 0.333f,
                1.0f, 1.0f, -1.0f,0.5f, 0f,
                -1.0f, 1.0f, 1.0f, 0.25f, 0.333f,
                1.0f, 1.0f, 1.0f, 0.5f, 0.333f,
                1.0f, 1.0f, -1.0f,0.5f, 0f,

                // Bottom face
                1.0f, -1.0f, -1.0f, 0.25f, 0.666f,
                1.0f, -1.0f, 1.0f, 0.25f, 1f,
                -1.0f, -1.0f, -1.0f, 0.5f, 0.666f,
                1.0f, -1.0f, 1.0f, 0.25f, 1f,
                -1.0f, -1.0f, 1.0f, 0.5f, 1f,
                -1.0f, -1.0f, -1.0f, 0.5f, 0.666f
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
}

    @Override
    protected void createTexelsVBO() {

    }

    @Override
    protected void createNormalsVBO() {
        final float[] normal =
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
}
