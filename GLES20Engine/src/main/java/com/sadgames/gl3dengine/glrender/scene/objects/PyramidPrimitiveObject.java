package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;

import static com.sadgames.sysutils.common.MathUtils.crossProduct;

public class PyramidPrimitiveObject extends GameItemObject {

    private static float[] vertexes;

    protected float bottomHalfWidth;
    protected float height;

    public PyramidPrimitiveObject(GLShaderProgram program, int color, float bottomHalfWidth, float height) {
        super(program, color, 1f, COLLISION_OBJECT);
        init(bottomHalfWidth, height);
    }

    public PyramidPrimitiveObject(String textureResName, GLShaderProgram program, float bottomHalfWidth, float height) {
        super(textureResName, program, 1f, COLLISION_OBJECT);
        init(bottomHalfWidth, height);
    }

    protected void init(float bottomHalfWidth, float height) {
        this.bottomHalfWidth = bottomHalfWidth;
        this.height = height;

        vertexes = new float[]{
                // Front face
               -bottomHalfWidth,   0.0f,  bottomHalfWidth,   0f,   1f,
                             0f, height,               0f, 0.5f, 0.5f,
                bottomHalfWidth,   0.0f,  bottomHalfWidth,   1f,   1f,

                // Right face
                bottomHalfWidth,   0.0f,  bottomHalfWidth,   1f,   1f,
                             0f, height,               0f, 0.5f, 0.5f,
                bottomHalfWidth,   0.0f, -bottomHalfWidth,   1f,   0f,

                // Back face
                bottomHalfWidth,   0.0f, -bottomHalfWidth,   1f,   0f,
                             0f, height,               0f, 0.5f, 0.5f,
               -bottomHalfWidth,   0.0f,           -0.05f,   0f,   0f,

                // Left face
               -bottomHalfWidth,   0.0f, -bottomHalfWidth,   0f,   0f,
                             0f, height,               0f, 0.5f, 0.5f,
               -bottomHalfWidth,   0.0f,  bottomHalfWidth,   0f,   1f
        };
    }

    @Override
    public int getFacesCount() {
        return 12;
    }

    @Override
    protected float[] getVertexesArray() {
        return vertexes;
    }

    @Override
    protected float[] getNormalsArray() {
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

        return normal;
    }

    @Override
    protected short[] getFacesArray() {
        return new short[]{ 0, 2, 1, 3, 5, 4, 6, 8, 7, 9, 11, 10};
    }
}
