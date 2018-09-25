package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.vecmath.Vector4f;

import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.get_GL_TRIANGLE_STRIP_value;
import static com.sadgames.gl3dengine.glrender.GLES20JniWrapper.glDrawArrays;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType.GUI_OBJECT;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXEL_UV_SIZE;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEX_SIZE;

public class GUI2DImageObject extends AbstractGL3DObject {

    private float left;
    private float top;
    private float right;
    private float bottom;
    private boolean isReftectedY;

    public GUI2DImageObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, Vector4f box, boolean isReftectedY) {
        super(sysUtilsWrapper, GUI_OBJECT, program);

        this.left = box.x;
        this.top = box.y;
        this.right = box.z;
        this.bottom = box.w;
        this.isReftectedY = isReftectedY;

        setCastShadow(false);
    }

    @Override
    public int getFacesCount() {
        return 4;
    }

    @Override
    protected void createVertexesVBO() {

        float[] vertexes = new float[] {
                left, top, 0.0f,
                left, bottom, 0.0f,
                right, top, 0.0f,
                right, bottom, 0.0f,
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
        glDrawArrays(get_GL_TRIANGLE_STRIP_value(), 0, getFacesCount());
    }

    @Override
    protected void createTexelsVBO() {
        float[] texcoords = new float[] {
                0.0f, 1.0f - (isReftectedY ? 1 : 0),
                0.0f, 0.0f + (isReftectedY ? 1 : 0),
                1.0f, 1.0f - (isReftectedY ? 1 : 0),
                1.0f, 0.0f + (isReftectedY ? 1 : 0)
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
