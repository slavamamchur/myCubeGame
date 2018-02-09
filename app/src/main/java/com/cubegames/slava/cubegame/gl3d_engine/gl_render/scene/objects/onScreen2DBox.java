package com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.objects;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.VBO_STRIDE;
import static com.cubegames.slava.cubegame.gl3d_engine.gl_render.GLRenderConsts.VERTEX_SIZE;

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
                box.left, box.top, 0, 0, 1,
                box.left, box.bottom, 0, 0, 0,
                box.right, box.top, 0, 1, 1,
                box.right, box.bottom, 0, 1, 0
        };

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        vertexData.position(0);
        getVertexVBO().setParamValue(VERTEX_SIZE, VBO_STRIDE, 0, vertexData);
        getTexelVBO().setParamValue(TEXEL_UV_SIZE, VBO_STRIDE, VERTEX_SIZE * 4, vertexData);

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

    }
    @Override
    protected void createNormalsVBO() {

    }
    @Override
    protected int createFacesIBO() {
        return 0;
    }
}
