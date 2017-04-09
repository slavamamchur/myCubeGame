package com.cubegames.slava.cubegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.cubegames.slava.cubegame.api.GameMapController;
import com.cubegames.slava.cubegame.model.GameMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static com.cubegames.slava.cubegame.Utils.createProgram;
import static com.cubegames.slava.cubegame.Utils.createShader;
import static com.cubegames.slava.cubegame.Utils.loadBitmapFromDB;
import static com.cubegames.slava.cubegame.Utils.loadGLTexture;

class MapGLRenderer implements GLSurfaceView.Renderer {

    private final static int POSITION_COUNT = 3;
    private static final int TEXTURE_COUNT = 2;
    private static final int V_SIZE = (POSITION_COUNT + TEXTURE_COUNT);
    private static final int STRIDE = V_SIZE * 4;
    private final static long TIME = 10000L;

    private final static float LAND_WIDTH = 2.0f;
    private final static float LAND_HEIGHT = 2.0f;

    private final static float LIGHT_X = 1f; //-3f;
    private final static float LIGHT_Y = 2.5f; //2f;
    private final static float LIGHT_Z = -3.2f; //-3f; ///???

    private final static float CAMERA_X = 0f;
    private final static float CAMERA_Y = 2.5f;
    private final static float CAMERA_Z = -3.2f;

    private final static float CAMERA_LOOK_X = 0f;
    private final static float CAMERA_LOOK_Y = 0f;
    private final static float CAMERA_LOOK_Z = 0f;

    private final static float CAMERA_UP_X = 0f;
    private final static float CAMERA_UP_Y = 1f;
    private final static float CAMERA_UP_Z = 0f;

    private Context context;
    private int mapGLTexture = 0;
    private String mapID;

    private FloatBuffer vertexData;
    private FloatBuffer normalData;
    private ShortBuffer indexData;

    private int aPositionLocation;
    private int aTextureLocation;
    private int aNormalLocation;

    private int uCameraLocation;
    private int uLightPositionLocation;
    private int uTextureUnitLocation;
    private int uMatrixLocation;

    private int facesCounter = 0;
    private int programId;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    /**
     60      * Stores a copy of the model matrix specifically for the light position.
     61      */
    private float[] mLightModelMatrix = new float[16];

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     104      *  we multiply this by our transformation matrices. */
    private final float[] mLightPosInModelSpace = new float[] {LIGHT_X, LIGHT_Y, LIGHT_Z, 1.0f};
    /** Used to hold the current position of the light in world space (after transformation via model matrix). */
    private float[] mLightPosInWorldSpace = new float[4];
    /** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
    private float[] mLightPosInEyeSpace = new float[4];

    MapGLRenderer(Context context) {
        this.context = context;
    }

    void setMapID(String mapID) {
        this.mapID = mapID;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0.7f, 1f, 1f);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        //glHint(GL_GENERATE_MIPMAP_HINT, GL_NICEST);

        createAndUseProgram();

        getLocations();
        prepareData();
        bindData();

        createViewMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        setModelMatrix();

        bindMatrix();
        //bindCamera();
        bindLightSource();
        //bindTextures();

        //Scene draw call to OpenGL
        indexData.position(0);
        GLES20.glDrawElements(GL_TRIANGLE_STRIP, facesCounter, GL_UNSIGNED_SHORT, indexData);
    }

    private void bindCamera() {
        glUniform3f(uCameraLocation, CAMERA_X, CAMERA_Y, CAMERA_Z);
    }

    private void bindLightSource() {
        glUniform3fv(uLightPositionLocation, 1, mLightPosInWorldSpace, 0);
    }

    private void bindTextures() {
        glBindTexture(GL_TEXTURE_2D, mapGLTexture);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);
    }

    private void createAndUseProgram() {
        programId = createProgram(createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader),
                                  createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader));
        glUseProgram(programId);
    }

    private void getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        aNormalLocation = glGetAttribLocation(programId, "a_Normal");
        aTextureLocation = glGetAttribLocation(programId, "a_Texture");
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit");
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
        uLightPositionLocation = glGetUniformLocation(programId, "u_lightPosition");
        uCameraLocation = glGetUniformLocation(programId, "u_camera");
    }

    private void prepareData() {
        final Bitmap bitmap = getMapTextureFromDB();

        float[] vertices = generateLandMeshUV(bitmap, 30, 30);
        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);

        float[] normals = generateLandNormals(vertices, 30, 30);
        normalData = ByteBuffer
                .allocateDirect(normals.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalData.put(normals);

        short[] index = generateLandFaces(30, 30);
        indexData = ByteBuffer
                .allocateDirect(index.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indexData.put(index);
        facesCounter = index.length;

        mapGLTexture = loadGLTexture(bitmap);
    }

    private short chain(int j, int i, int imax){
        return (short) (i+j*(imax+1));
    }

    private short[] generateLandFaces(int imax, int jmax) {

        short[] index = new short[2*(imax+1)*jmax + (jmax-1)];
        int k=0;
        int j=0;
        while (j < jmax) {
            // лента слева направо
            for (int i = 0; i <= imax; i++) {
                index[k] = chain(j,i,imax);
                k++;
                index[k] = chain(j+1,i,imax);
                k++;
            }
            if (j < jmax-1){
                // вставим хвостовой индекс для связки
                index[k] = chain(j+1,imax,imax);
                k++;
            }
            // переводим ряд
            j++;

            // проверяем достижение конца
            if (j < jmax){
                // лента справа налево
                for (int i = imax; i >= 0; i--) {
                    index[k] = chain(j,i,imax);
                    k++;
                    index[k] = chain(j+1,i,imax);
                    k++;
                }
                if (j < jmax-1){
                    // вставим хвостовой индекс для связки
                    index[k] = chain(j+1,0,imax);
                    k++;
                }
                // переводим ряд
                j++;
            }
        }

        return index;
    }

    private Bitmap getMapTextureFromDB() {
        GameMapController gmc = new GameMapController(context);
        gmc.saveMapImage(new GameMap(mapID));
        return loadBitmapFromDB(context, mapID);
    }

    private float[] generateLandMeshUV(Bitmap bitmap, int imax, int jmax) {

        float [] vertex = new float[(jmax+1)*(imax+1)*(POSITION_COUNT + TEXTURE_COUNT)];

        float tdx = 1f / imax;
        float tdy = 1f / jmax;
        float dx = LAND_WIDTH / imax;
        float dz = LAND_HEIGHT / jmax;
        float x0 = -LAND_WIDTH / 2f;
        float z0 = -LAND_HEIGHT / 2f;
        int k = 0;

        for (int j = 0; j <= jmax; j++){
            for (int i = 0; i <= imax; i++){
                vertex[k] = x0 + i*dx; //x
                vertex[k + 2] = z0 + j*dz; //z
                //TODO: generate by texture
                vertex[k + 1] = (float)Math.exp(-3*(vertex[k]*vertex[k]+vertex[k + 2]*vertex[k + 2])); //y = (float)Math.exp(-3*(x[i]*x[i]+z[j]*z[j]));

                vertex[k + 3] = i*tdx; //u
                vertex[k + 4] = j*tdy; //v

                k += 5;
            }
        }

        return vertex;
    }

    private int toLinear(int i, int j, int i_max, int el_size){
        return j * (i_max + 1) * el_size + i * el_size;
    }

    private float[] generateLandNormals(float [] vertex, int imax, int jmax) {

        float dx = LAND_WIDTH / imax;
        float dz = LAND_HEIGHT / jmax;
        float [] normal = new float[(jmax+1)*(imax+1)*POSITION_COUNT];
        int k = 0;

        for (int j = 0; j <= jmax; j++)
            for (int i = 0; i <= imax; i++) {

                if ((i == imax) && (j == jmax)) {
                    normal[k] = vertex[toLinear(i - 1, j, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1] * dz; //(y[jmax][imax-1] - y[jmax][imax])*dz;
                    normal[k + 2] = dx * (vertex[toLinear(i, j - 1, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1]); //dx * (y [jmax-1] [imax] - y[jmax ] [imax]);
                } else if (i == imax) {
                    normal[k] = vertex[toLinear(i - 1, j, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1] * dz; //( y [ j ] [ imax -1] - y [ j ] [ imax] ) * dz;
                    normal[k + 2] = -dx * (vertex[toLinear(i, j + 1, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1]); //- dx * ( y [ j+1 ] [ imax] - y [ j ] [ imax ] );
                } else if (j == jmax) {
                    normal[k] = -(vertex[toLinear(i + 1, j, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1]) * dz; //- ( y [ jmax ] [ i+1 ] - y [ jmax ] [ i ] ) * dz;
                    normal[k + 2] = dx * (vertex[toLinear(i, j - 1, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1]); //dx * ( y [ jmax-1 ] [ i ] - y [ jmax ] [ i ] );
                } else {
                    normal[k] = -(vertex[toLinear(i + 1, j, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1]) * dz; //- ( y [j] [i+1] - y [j] [i] ) * dz;
                    normal[k + 2] = -dx * (vertex[toLinear(i, j + 1, imax, V_SIZE) + 1] - vertex[toLinear(i, j, imax, V_SIZE) + 1]); //- dx * ( y [j+1] [i] - y [j] [i] );
                }

                normal[k + 1] = dx * dz; //set y value

                k += 3;
            }

        return normal;
    }

    private void bindData() {
        // координаты вершин
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        // координаты текстур
        vertexData.position(POSITION_COUNT);
        glVertexAttribPointer(aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);
        glEnableVertexAttribArray(aTextureLocation);

        //normals
        normalData.position(0);
        GLES20.glVertexAttribPointer(aNormalLocation, POSITION_COUNT, GLES20.GL_FLOAT, false,
                0, normalData);
        GLES20.glEnableVertexAttribArray(aNormalLocation);

        bindCamera();
        bindTextures();
    }

    private void createViewMatrix() {
        // точка полоения камеры
        float eyeX = CAMERA_X;
        float eyeY = CAMERA_Y;
        float eyeZ = CAMERA_Z;

        // точка направления камеры
        float centerX = CAMERA_LOOK_X;
        float centerY = CAMERA_LOOK_Y;
        float centerZ = CAMERA_LOOK_Z;

        // up-вектор
        float upX = CAMERA_UP_X;
        float upY = CAMERA_UP_Y;
        float upZ = CAMERA_UP_Z;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    private void createProjectionMatrix(int width, int height) {
        float ratio;
        float left = -0.5f;
        float right = 0.5f;
        float bottom = -0.5f;
        float top = 0.5f;
        float near = 2;
        float far = 12;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private void bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }

    private void setModelMatrix() {
        //сбрасываем model матрицу
        Matrix.setIdentityM(mModelMatrix, 0);
        //В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.
        float angle = -(float)(SystemClock.uptimeMillis() % TIME) / TIME * 360;
        //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z).
        Matrix.rotateM(mModelMatrix, 0, angle, 0, 1, 0);

        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.rotateM(mLightModelMatrix, 0, -angle, 0, 1, 0);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        //Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
    }

}
