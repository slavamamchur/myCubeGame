package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

public class SpherePrimitiveObject extends GameItemObject {

    private final static float PI = 3.1415926535f;
    private final static float PI_2 = 1.57079632679f;
    private final static short RINGS = 20;
    private final static short SECTORS = 30;

    protected float halfSize;
    protected float[] points;
    protected float[] normals;
    protected float[] texcoords;
    protected short[] indices;

    public SpherePrimitiveObject(SysUtilsWrapperInterface sysUtilsWrapper, String textureResName, GLShaderProgram program, float mass, int tag, float halfSize) {
        super(sysUtilsWrapper, textureResName, program, mass, tag);

        this.halfSize = halfSize;

        CreateIcosphere(halfSize, RINGS, SECTORS);
    }

    public SpherePrimitiveObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, int color, float mass, int tag, float halfSize) {
        super(sysUtilsWrapper, program, color, mass, tag);

        this.halfSize = halfSize;

        CreateIcosphere(halfSize, RINGS, SECTORS);
    }


    private void CreateIcosphere(float radius, short rings, short sectors) {
        float R = 1f/(float)(rings-1);
        float S = 1f/(float)(sectors-1);
        short r, s;
        float x, y, z;

        points = new float[rings * sectors * 3];
        normals = new float[rings * sectors * 3];
        texcoords = new float[rings * sectors * 2];

        int t = 0, v = 0, n = 0;
        for(r = 0; r < rings; r++) {
            for(s = 0; s < sectors; s++) {
                y = (float) Math.sin( -PI_2 + PI * r * R );
                x = (float) (Math.cos(2*PI * s * S) * Math.sin( PI * r * R ));
                z = (float) (Math.sin(2*PI * s * S) * Math.sin( PI * r * R ));

                texcoords[t++] = s*S;
                texcoords[t++] = r*R;

                points[v++] = x * radius;
                points[v++] = y * radius;
                points[v++] = z * radius;

                normals[n++] = x;
                normals[n++] = y;
                normals[n++] = z;
            }
        }

        int counter = 0;
        indices = new short[rings * sectors * 6];
        for(r = 0; r < rings - 1; r++){
            for(s = 0; s < sectors-1; s++) {
                indices[counter++] = (short) (r * sectors + s);       //(a)
                indices[counter++] = (short) (r * sectors + (s+1));    //(b)
                indices[counter++] = (short) ((r+1) * sectors + (s+1));  // (c)
                indices[counter++] = (short) ((r+1) * sectors + (s+1));  // (c)
                indices[counter++] = (short) (r * sectors + (s+1));    //(b)
                indices[counter++] = (short) ((r+1) * sectors + s);     //(d)
            }
        }

    }

    @Override
    protected float[] getVertexesArray() {
        return points;
    }

    @Override
    protected float[] getNormalsArray() {
        return normals;
    }

    @Override
    protected short[] getFacesArray() {
        return indices;
    }

    @Override
    public int getFacesCount() {
        return indices.length;
    }

}
