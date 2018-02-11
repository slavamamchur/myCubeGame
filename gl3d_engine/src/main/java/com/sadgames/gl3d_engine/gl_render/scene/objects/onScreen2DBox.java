package com.sadgames.gl3d_engine.gl_render.scene.objects;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.VERTEX_SIZE;

public class onScreen2DBox extends GLSceneObject {

    private RectF box;

    public onScreen2DBox(GLShaderProgram program, RectF box) {
        super(GUI_OBJECT, program);

        this.box = box;
    }

    @Override
    public int getFacesCount() {
        return 4;
    }

    @Override
    protected void createVertexesVBO() {

        float[] vertexes = new float[] {
                box.left, box.top, 0.0f,
                box.left, box.bottom, 0.0f,
                box.right, box.top, 0.0f,
                box.right, box.bottom, 0.0f,
        };

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        vertexData.position(0);
        getVertexVBO().setParamValue(VERTEX_SIZE, 0, 0, vertexData);

        vertexData.limit(0);
    }

    @Override
    public void render() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, getFacesCount());
    }

    @Override
    protected Bitmap getTextureBitmap() {
        return null;
    }

    @Override
    protected void createTexelsVBO() {
        float[] texcoords = new float[] {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        FloatBuffer texelData = ByteBuffer
                .allocateDirect(texcoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texelData.put(texcoords);
        texelData.position(0);
        getTexelVBO().setParamValue(TEXEL_UV_SIZE, 0, 0, texelData);

        texelData.limit(0);
    }

    @Override
    protected void createNormalsVBO() {}

    @Override
    protected int createFacesIBO() {
        return 0;
    }
}
