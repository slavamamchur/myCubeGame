package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.ArrayList;
import java.util.List;

public class SpherePrimitiveObject extends GameItemObject {

    protected float halfSize;
    protected int mRefineLevel;
    protected float _mPosX;
    protected float _mPosY;
    protected float _mPosZ;
    protected List<Float> _mVertices;
    protected List<Integer> _mIndices;
    protected int _mCountVertex;
    protected int _mCountIndex;
    protected int _mCountTriangle;


    public SpherePrimitiveObject(SysUtilsWrapperInterface sysUtilsWrapper, String textureResName, GLShaderProgram program, float mass, int tag, float halfSize) {
        super(sysUtilsWrapper, textureResName, program, mass, tag);

        this.halfSize = halfSize;
        mRefineLevel = 2;
        _mPosX = _mPosY = _mPosZ = 0;

        CreateIcosphere(mRefineLevel);

    }

    public SpherePrimitiveObject(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, int color, float mass, int tag, float halfSize) {
        super(sysUtilsWrapper, program, color, mass, tag);

        this.halfSize = halfSize;
        mRefineLevel = 2;
        _mPosX = _mPosY = _mPosZ = 0;

        CreateIcosphere(mRefineLevel);
    }

    private int GetMiddlePoint(int p1, int p2) {
        float point1[] = new float[3];
        float point2[] = new float[3];;
        float middle[] = new float[3];;

        //Given the index of each vertex, we can get component x,y,z
        //by adding an offset (0,1,2)
        point1[0] = _mVertices.get(p1 * 3);
        point1[1] = _mVertices.get(p1 * 3 + 1);
        point1[2] = _mVertices.get(p1 * 3 + 2);
        point2[0] = _mVertices.get(p2 * 3);
        point2[1] = _mVertices.get(p2 * 3 + 1);
        point2[2] = _mVertices.get(p2 * 3 + 2);
        middle[0] = (point1[0] + point2[0]) / 2.0f;
        middle[1] = (point1[1] + point2[1]) / 2.0f;
        middle[2] = (point1[2] + point2[2]) / 2.0f;

        //Normalize the new vertex to make sure it's on a unit sphere
        float len = len(middle[0], middle[1], middle[2]);
        middle[0] /= len;
        middle[1] /= len;
        middle[2] /= len;

        //TODO: set scale
        _mVertices.add(middle[0]);
        _mVertices.add(middle[1]);
        _mVertices.add(middle[2]);

        _mCountVertex++;

        return _mCountVertex - 1;
    }

    private float len(float x, float y, float z) {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }

    private void CreateIcosphere(int refineLevel) {
        //TODO: set scale
        //Start by creating the 12 vertices of an icosahedron
        //Phi is the golden ratio
        //http://paulbourke.net/geometry/platonic/
        
        float phi = (float) ((1.0f + Math.sqrt(5.0)) / 2.0f);

        _mCountVertex = 12;
        _mVertices = new ArrayList<>();

        float plen = len(-1f, phi, 0f);

        _mVertices.add(-1f/plen); _mVertices.add( phi/plen); _mVertices.add(0f); //v0
        _mVertices.add( 1f/plen); _mVertices.add( phi/plen); _mVertices.add(0f); //v1
        _mVertices.add(-1f/plen); _mVertices.add(-phi/plen); _mVertices.add(0f); //v2
        _mVertices.add( 1f/plen); _mVertices.add(-phi/plen); _mVertices.add(0f); //v3

        _mVertices.add(0f); _mVertices.add(-1f/plen); _mVertices.add( phi/plen); //v4
        _mVertices.add(0f); _mVertices.add( 1f/plen); _mVertices.add( phi/plen); //v5
        _mVertices.add(0f); _mVertices.add(-1f/plen); _mVertices.add(-phi/plen); //v6
        _mVertices.add(0f); _mVertices.add( 1f/plen); _mVertices.add(-phi/plen); //v7

        _mVertices.add( phi/plen); _mVertices.add(0f); _mVertices.add(-1f/plen); //v8
        _mVertices.add( phi/plen); _mVertices.add(0f); _mVertices.add( 1f/plen); //v9
        _mVertices.add(-phi/plen); _mVertices.add(0f); _mVertices.add(-1f/plen); //v10
        _mVertices.add(-phi/plen); _mVertices.add(0f); _mVertices.add( 1f/plen); //v11

        //then create the 20 triangles of the icosahedron (indices)
        //var faces = new List<TriangleIndices>();
        _mIndices = new ArrayList<>();
        _mCountTriangle = 20;
        _mCountIndex = 20*3;

        //5 faces around point 0
        _mIndices.add(0); _mIndices.add(11); _mIndices.add( 5);
        _mIndices.add(0); _mIndices.add( 5); _mIndices.add( 1);
        _mIndices.add(0); _mIndices.add( 1); _mIndices.add( 7);
        _mIndices.add(0); _mIndices.add( 7); _mIndices.add(10);
        _mIndices.add(0); _mIndices.add(10); _mIndices.add(11);

        //5 adjacent faces
        _mIndices.add( 1); _mIndices.add( 5); _mIndices.add(9);
        _mIndices.add( 5); _mIndices.add(11); _mIndices.add(4);
        _mIndices.add(11); _mIndices.add(10); _mIndices.add(2);
        _mIndices.add(10); _mIndices.add( 7); _mIndices.add(6);
        _mIndices.add( 7); _mIndices.add( 1); _mIndices.add(8);

        //5 faces around point 3
        _mIndices.add(3); _mIndices.add(9); _mIndices.add(4);
        _mIndices.add(3); _mIndices.add(4); _mIndices.add(2);
        _mIndices.add(3); _mIndices.add(2); _mIndices.add(6);
        _mIndices.add(3); _mIndices.add(6); _mIndices.add(8);
        _mIndices.add(3); _mIndices.add(8); _mIndices.add(9);

        //5 adjacent faces
        _mIndices.add(4); _mIndices.add(9); _mIndices.add( 5);
        _mIndices.add(2); _mIndices.add(4); _mIndices.add(11);
        _mIndices.add(6); _mIndices.add(2); _mIndices.add(10);
        _mIndices.add(8); _mIndices.add(6); _mIndices.add( 7);
        _mIndices.add(9); _mIndices.add(8); _mIndices.add( 1);

        //Refine triangles
        int currentTriCount = _mCountTriangle;
        for (int r = 0; r < refineLevel; r++) {
            List<Integer> indicesTemp = new  ArrayList<>();
            for (int t = 0, i = 0; t<currentTriCount; t++, i += 3) {
                //replace triangle by 4 triangles
                int a = GetMiddlePoint(_mIndices.get(i), _mIndices.get(i+1));
                int b = GetMiddlePoint(_mIndices.get(i+1), _mIndices.get(i+2));
                int c = GetMiddlePoint(_mIndices.get(i+2), _mIndices.get(i));

                //Add the new indices
                //T1
                indicesTemp.add(_mIndices.get(i));
                indicesTemp.add(a);
                indicesTemp.add(c);
                //T2
                indicesTemp.add(_mIndices.get(i + 1));
                indicesTemp.add(b);
                indicesTemp.add(a);
                //T3
                indicesTemp.add(_mIndices.get(i + 2));
                indicesTemp.add(c);
                indicesTemp.add(b);
                //T4
                indicesTemp.add(a);
                indicesTemp.add(b);
                indicesTemp.add(c);
            }

            //replace _mIndices with indicesTemp to keep the new index array
            _mIndices = indicesTemp;

            //update triangle & indices count
            _mCountTriangle *= 4;
            _mCountIndex *= 4;
            currentTriCount = _mCountTriangle;
        }
    }

    @Override
    protected float[] getVertexesArray() {
        float[] a = new float[_mVertices.size()];
        for (int i = 0; i < _mVertices.size(); i++)
            a[i] = _mVertices.get(i)*halfSize;

        return a;
    }

    @Override
    protected float[] getNormalsArray() {
        float[] a = new float[_mVertices.size()];
        for (int i = 0; i < _mVertices.size(); i++)
            a[i] = -_mVertices.get(i)*halfSize;

        return a;
    }

    @Override
    protected short[] getFacesArray() {
        short[] a = new short[_mIndices.size()];
        for (int i = 0; i < _mIndices.size(); i++)
            a[i] = _mIndices.get(i).shortValue();

        return a;
    }

    @Override
    public int getFacesCount() {
        return _mIndices.size();
    }

}
