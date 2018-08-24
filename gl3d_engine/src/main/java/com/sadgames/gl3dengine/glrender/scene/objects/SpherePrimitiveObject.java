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
    protected List<Float> _mVertices = new ArrayList<>();
    protected int _mCountVertex;
    protected int _mCountIndex;
    protected int _mCountNormal;
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
        float len = (float) Math.sqrt(middle[0] * middle[0] + middle[1] * middle[1] + middle[2] * middle[2]);
        middle[0] /= len;
        middle[1] /= len;
        middle[2] /= len;

        _mVertices.add(middle[0]);
        _mVertices.add(middle[1]);
        _mVertices.add(middle[2]);

        _mCountVertex++;

        return _mCountVertex - 1;
    }

    private void CreateIcosphere(int refineLevel) {
        //TODO:
        //Start by creating the 12 vertices of an icosahedron
        //Phi is the golden ratio
        //http://paulbourke.net/geometry/platonic/
        /*float phi = (1.0f + sqrt(5.0f)) / 2.0f;

        _mCountVertex = 12;
        _mVertices = new vector<GLfloat>;
        _mVertices->push_back(-1); _mVertices->push_back( phi); _mVertices->push_back(0); //v0
        _mVertices->push_back( 1); _mVertices->push_back( phi); _mVertices->push_back(0); //v1
        _mVertices->push_back(-1); _mVertices->push_back(-phi); _mVertices->push_back(0); //v2
        _mVertices->push_back( 1); _mVertices->push_back(-phi); _mVertices->push_back(0); //v3

        _mVertices->push_back(0); _mVertices->push_back(-1); _mVertices->push_back( phi); //v4
        _mVertices->push_back(0); _mVertices->push_back( 1); _mVertices->push_back( phi); //v5
        _mVertices->push_back(0); _mVertices->push_back(-1); _mVertices->push_back(-phi); //v6
        _mVertices->push_back(0); _mVertices->push_back( 1); _mVertices->push_back(-phi); //v7

        _mVertices->push_back( phi); _mVertices->push_back(0); _mVertices->push_back(-1); //v8
        _mVertices->push_back( phi); _mVertices->push_back(0); _mVertices->push_back( 1); //v9
        _mVertices->push_back(-phi); _mVertices->push_back(0); _mVertices->push_back(-1); //v10
        _mVertices->push_back(-phi); _mVertices->push_back(0); _mVertices->push_back( 1); //v11

        //Normalize vertices to make sure they're on a unit sphere
        for (vector<GLfloat>::size_type i = 0; i < _mVertices->size(); i += 3)
        {
            GLfloat x = _mVertices->at(i + 0);
            GLfloat y = _mVertices->at(i + 1);
            GLfloat z = _mVertices->at(i + 2);
            float len = sqrt(x*x + y*y + z*z);

            _mVertices->at(i+0) /= len;
            _mVertices->at(i+1) /= len;
            _mVertices->at(i+2) /= len;
        }

        //then create the 20 triangles of the icosahedron (indices)
        //var faces = new List<TriangleIndices>();
        _mIndices = new vector<GLuint>;
        _mCountTriangle = 20;
        _mCountIndex = 20*3;

        //5 faces around point 0
        _mIndices->push_back(0); _mIndices->push_back(11); _mIndices->push_back( 5);
        _mIndices->push_back(0); _mIndices->push_back( 5); _mIndices->push_back( 1);
        _mIndices->push_back(0); _mIndices->push_back( 1); _mIndices->push_back( 7);
        _mIndices->push_back(0); _mIndices->push_back( 7); _mIndices->push_back(10);
        _mIndices->push_back(0); _mIndices->push_back(10); _mIndices->push_back(11);

        //5 adjacent faces
        _mIndices->push_back( 1); _mIndices->push_back( 5); _mIndices->push_back(9);
        _mIndices->push_back( 5); _mIndices->push_back(11); _mIndices->push_back(4);
        _mIndices->push_back(11); _mIndices->push_back(10); _mIndices->push_back(2);
        _mIndices->push_back(10); _mIndices->push_back( 7); _mIndices->push_back(6);
        _mIndices->push_back( 7); _mIndices->push_back( 1); _mIndices->push_back(8);

        //5 faces around point 3
        _mIndices->push_back(3); _mIndices->push_back(9); _mIndices->push_back(4);
        _mIndices->push_back(3); _mIndices->push_back(4); _mIndices->push_back(2);
        _mIndices->push_back(3); _mIndices->push_back(2); _mIndices->push_back(6);
        _mIndices->push_back(3); _mIndices->push_back(6); _mIndices->push_back(8);
        _mIndices->push_back(3); _mIndices->push_back(8); _mIndices->push_back(9);

        //5 adjacent faces
        _mIndices->push_back(4); _mIndices->push_back(9); _mIndices->push_back( 5);
        _mIndices->push_back(2); _mIndices->push_back(4); _mIndices->push_back(11);
        _mIndices->push_back(6); _mIndices->push_back(2); _mIndices->push_back(10);
        _mIndices->push_back(8); _mIndices->push_back(6); _mIndices->push_back( 7);
        _mIndices->push_back(9); _mIndices->push_back(8); _mIndices->push_back( 1);

        //Refine triangles
        int currentTriCount = _mCountTriangle;
        for (int r = 0; r < refineLevel; r++)
        {
            vector<GLuint> *indicesTemp = new vector<GLuint>;
            for (int t = 0, i = 0; t<currentTriCount; t++, i += 3)
            {
                //replace triangle by 4 triangles
                int a = GetMiddlePoint(_mIndices->at(i), _mIndices->at(i+1));
                int b = GetMiddlePoint(_mIndices->at(i+1), _mIndices->at(i+2));
                int c = GetMiddlePoint(_mIndices->at(i+2), _mIndices->at(i));

                //Add the new indices
                //T1
                indicesTemp->push_back(_mIndices->at(i));
                indicesTemp->push_back(a);
                indicesTemp->push_back(c);
                //T2
                indicesTemp->push_back(_mIndices->at(i + 1));
                indicesTemp->push_back(b);
                indicesTemp->push_back(a);
                //T3
                indicesTemp->push_back(_mIndices->at(i + 2));
                indicesTemp->push_back(c);
                indicesTemp->push_back(b);
                //T4
                indicesTemp->push_back(a);
                indicesTemp->push_back(b);
                indicesTemp->push_back(c);
            }

            //replace _mIndices with indicesTemp to keep the new index array
            _mIndices = indicesTemp;
            indicesTemp = NULL;

            //update triangle & indices count
            _mCountTriangle *= 4;
            _mCountIndex *= 4;
            currentTriCount = _mCountTriangle;
        }*/
    }

    @Override
    protected float[] getVertexesArray() {
        float[] a = new float[_mVertices.size()];
        for (int i = 0; i < _mVertices.size(); i++)
            a[i] = _mVertices.get(i);

        return a;
    }

    @Override
    protected float[] getNormalsArray() {
        return new float[0];
    }

    @Override
    protected short[] getFacesArray() {
        return new short[0];
    }

    @Override
    public int getFacesCount() {
        return 0;
    }
}
