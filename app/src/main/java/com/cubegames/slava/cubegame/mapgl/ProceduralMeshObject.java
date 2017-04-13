package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;
import static com.cubegames.slava.cubegame.Utils.chain;
import static com.cubegames.slava.cubegame.Utils.coord2idx;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.TEXEL_UV_SIZE;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.VBO_ITEM_SIZE;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.VBO_STRIDE;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.VERTEX_SIZE;

public abstract class ProceduralMeshObject extends GLSceneObject {

    private float LAND_WIDTH;
    private float LAND_HEIGHT;

    private float[] vertexes;
    private int dimension;

    public ProceduralMeshObject(Context context, GLObjectType type, float landWidth, float landHeight, int dimension, GLShaderProgram program) {
        super(context, type, program);

        this.dimension = dimension;
        LAND_WIDTH = landWidth;
        LAND_HEIGHT = landHeight;
    }

    protected abstract float getValueY(float valX, float valZ, float tu, float tv);

    @Override
    public int getFacesCount() {
        return 2 * (dimension + 1) * dimension + (dimension - 1);
    }

    @Override
    protected void createVertexesVBO() {
        vertexes = new float[(dimension + 1) * (dimension + 1) * VBO_ITEM_SIZE];

        float td = 1f / dimension;
        float dx = LAND_WIDTH / dimension;
        float dz = LAND_HEIGHT / dimension;
        float x0 = -LAND_WIDTH / 2f;
        float z0 = -LAND_HEIGHT / 2f;
        int k = 0;

        for (int j = 0; j <= dimension; j++){
            //TODO: bitmap line to array
            for (int i = 0; i <= dimension; i++){
                vertexes[k] = x0 + i * dx; /** x*/
                vertexes[k + 2] = z0 + j * dz; /** z*/

                vertexes[k + 3] = i * td; /** u*/
                vertexes[k + 4] = j * td; /** v*/

                vertexes[k + 1] = getValueY(vertexes[k], vertexes[k + 2], vertexes[k + 3], vertexes[k + 4]); /** y*/

                k += 5;
            }
        }

        FloatBuffer vertexData = ByteBuffer
                .allocateDirect(vertexes.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertexes);
        /** координаты вершин*/
        vertexData.position(0);
        setVBOParamData(getVertexVBO(), VERTEX_SIZE, VBO_STRIDE, 0, vertexData);
        /** координаты текстур*/
        setVBOParamData(getTexelVBO(), TEXEL_UV_SIZE, VBO_STRIDE, VERTEX_SIZE * 4, vertexData);
        vertexData.limit(0);
        vertexData = null;
    }

    @Override
    protected void createTexelsVBO() {
    }

    @Override
    protected void createNormalsVBO() {
        float dx = LAND_WIDTH / dimension;
        float dz = LAND_HEIGHT / dimension;
        float [] normal = new float[(dimension + 1) * (dimension + 1) * VERTEX_SIZE];
        int k = 0;

        for (int j = 0; j <= dimension; j++)
            for (int i = 0; i <= dimension; i++) {
                if ((i == dimension) && (j == dimension)) {
                    /** Nx = (y[jmax][imax - 1] - y[jmax][imax]) * dz*/
                    normal[k] = vertexes[coord2idx(i - 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1] * dz;
                    /** Nz = dx * (y[jmax - 1][imax] - y[jmax][imax])*/
                    normal[k + 2] = dx * (vertexes[coord2idx(i, j - 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                } else if (i == dimension) {
                    /** Nx = (y[j][imax - 1] - y[j][imax]) * dz*/
                    normal[k] = vertexes[coord2idx(i - 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1] * dz;
                    /** Nz = -dx * (y[j + 1][imax] - y[j][imax])*/
                    normal[k + 2] = -dx * (vertexes[coord2idx(i, j + 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                } else if (j == dimension) {
                    /** Nx = -(y[jmax][i + 1] - y[jmax][i]) * dz*/
                    normal[k] = -(vertexes[coord2idx(i + 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1]) * dz;
                    /** Nz = dx * (y[jmax - 1][i] - y[jmax][i])*/
                    normal[k + 2] = dx * (vertexes[coord2idx(i, j - 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                } else {
                    /** Nx = -(y[j][i + 1] - y[j][i]) * dz*/
                    normal[k] = -(vertexes[coord2idx(i + 1, j, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1]) * dz;
                    /** Nz = -dx * (y[j + 1][i] - y[j][i])*/
                    normal[k + 2] = -dx * (vertexes[coord2idx(i, j + 1, dimension, VBO_ITEM_SIZE) + 1] - vertexes[coord2idx(i, j, dimension, VBO_ITEM_SIZE) + 1]);
                }

                normal[k + 1] = dx * dz; /**set Ny value*/

                k += 3;
            }

        FloatBuffer normalData = ByteBuffer
                .allocateDirect(normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalData.put(normal);
        normalData.position(0);
        setVBOParamData(getNormalVBO(), VERTEX_SIZE, 0, 0, normalData);
        normalData.limit(0);

        vertexes = null;
    }

    @Override
    protected int createFacesIBO() {
        short[] index = new short[2 * (dimension + 1) * dimension + (dimension - 1)];
        int k=0;
        int j=0;

        while (j < dimension) {
            /** лента слева направо*/
            for (int i = 0; i <= dimension; i++) {
                index[k] = chain(j, i, dimension);
                k++;
                index[k] = chain(j+1, i, dimension);
                k++;
            }
            if (j < dimension - 1){
                /** вставим хвостовой индекс для связки*/
                index[k] = chain(j + 1, dimension, dimension);
                k++;
            }
            /** переводим ряд*/
            j++;

            /** проверяем достижение конца*/
            if (j < dimension){
                /** лента справа налево*/
                for (int i = dimension; i >= 0; i--) {
                    index[k] = chain(j, i, dimension);
                    k++;
                    index[k] = chain(j + 1, i, dimension);
                    k++;
                }
                if (j < dimension - 1){
                    /** вставим хвостовой индекс для связки*/
                    index[k] = chain(j + 1, 0, dimension);
                    k++;
                }
                /** переводим ряд*/
                j++;
            }
        }

        ShortBuffer indexData = ByteBuffer
                .allocateDirect(index.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indexData.put(index);
        index = null;
        final int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        indexData.position(0);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData.capacity() * 2, indexData, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        indexData.limit(0);

        return buffers[0];
    }

}
