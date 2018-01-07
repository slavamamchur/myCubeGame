package com.cubegames.slava.cubegame.gl_render;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.cubegames.slava.cubegame.R;
import com.cubegames.slava.cubegame.Utils;
import com.cubegames.slava.cubegame.gl_render.scene.GLCamera;
import com.cubegames.slava.cubegame.gl_render.scene.GLLightSource;
import com.cubegames.slava.cubegame.gl_render.scene.GLScene;
import com.cubegames.slava.cubegame.gl_render.scene.objects.ColorShapeObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.DiceObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.LandObject;
import com.cubegames.slava.cubegame.gl_render.scene.objects.WaterObject;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static com.cubegames.slava.cubegame.Utils.forceGC_and_Sync;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.CHIP_MESH_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.TERRAIN_MESH_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.WATER_MESH_OBJECT;

public class MapGLRenderer implements GLSurfaceView.Renderer {

    private final static float LIGHT_X = -2.2F;
    private final static float LIGHT_Y = 1.7F;
    private final static float LIGHT_Z = -3.2F;

    private final static float CAMERA_X = 0;//0
    private final static float CAMERA_Y = 2F;//2
    private final static float CAMERA_Z = 3f;//-4

    private final static float CAMERA_LOOK_X = 0;
    private final static float CAMERA_LOOK_Y = 0;
    private final static float CAMERA_LOOK_Z = 0;

    private final static float CAMERA_UP_X = 0;
    private final static float CAMERA_UP_Y = 1;
    private final static float CAMERA_UP_Z = 0;

    private Context context;

    private String mapID;
    private Game gameEntity = null;
    private GameInstance gameInstanceEntity = null;

    private BroadphaseInterface _broadphase;
    private DefaultCollisionConfiguration _collisionConfiguration;
    private CollisionDispatcher _dispatcher;
    private SequentialImpulseConstraintSolver _solver;
    private DiscreteDynamicsWorld _world;

    /** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     *  we multiply this by our transformation matrices. */
    private static final float[] LIGHT_POS_IN_MODEL_SPACE = new float[] {LIGHT_X, LIGHT_Y, LIGHT_Z, 1.0f};

    private GLScene mScene;

    public MapGLRenderer(Context context) {
        this.context = context;
    }

    public GLScene getmScene() {
        return mScene;
    }

    public void setMapID(String mapID) {
        this.mapID = mapID;
    }
    public void setGameEntity(Game gameEntity) {
        this.gameEntity = gameEntity;
    }
    public void setGameInstanceEntity(GameInstance gameInstanceEntity) {
        this.gameInstanceEntity = gameInstanceEntity;
        if (mScene != null)
            mScene.setGameInstanceEntity(gameInstanceEntity);
    }
    public DiscreteDynamicsWorld get_world() {
        return _world;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initGLRender();
        initScene();
        loadScene();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        mScene.getCamera().setProjectionMatrix(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawScene();
    }

    private void initGLRender() {
        glClearColor(0f, 0.7f, 1f, 1f);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    private void initScene() {
        mScene = new GLScene(context);
        mScene.setGameInstanceEntity(gameInstanceEntity);

        mScene.setCamera(new GLCamera(CAMERA_X, CAMERA_Y, CAMERA_Z,
                                      CAMERA_LOOK_X, CAMERA_LOOK_Y, CAMERA_LOOK_Z,
                                      CAMERA_UP_X, CAMERA_UP_Y, CAMERA_UP_Z));

        mScene.setLightSource(new GLLightSource(LIGHT_POS_IN_MODEL_SPACE, mScene.getCamera()));

        initPhysics();
    }

    private void initPhysics() {
        _broadphase = new DbvtBroadphase();

        _collisionConfiguration = new DefaultCollisionConfiguration();
        _dispatcher = new CollisionDispatcher(_collisionConfiguration);

        _solver = new SequentialImpulseConstraintSolver();

        _world = new DiscreteDynamicsWorld(_dispatcher, _broadphase, _solver, _collisionConfiguration);
        _world.setGravity(new Vector3f(0f, -9.8f, 0f));
    }

    private DiceObject dice_1;
    public DiceObject getDice_1() {
        return dice_1;
    }

    private void loadScene() {
        int skyboxMap = Utils.loadGLCubeMapTexture(context, new int[]{R.drawable.skybox_right,
                R.drawable.skybox_left,
                R.drawable.skybox_top,
                R.drawable.skybox_bottom,
                R.drawable.skybox_back,
                R.drawable.skybox_front});

        GLSceneObject water = new WaterObject(context, mScene.getCachedShader(TERRAIN_OBJECT));
        water.loadObject();
        water.setGlTextureId(skyboxMap);
        mScene.addObject(water, WATER_MESH_OBJECT);

        LandObject terrain = new LandObject(context, mScene.getCachedShader(TERRAIN_OBJECT), gameEntity);
        terrain.loadObject();
        mScene.addObject(terrain, TERRAIN_MESH_OBJECT);
        terrain.createRigidBody();
        _world.addRigidBody(terrain.get_body());

        if (gameInstanceEntity != null && gameEntity.getGamePoints() != null)
            placeChips();

        dice_1 = new DiceObject(context, mScene.getCachedShader(TERRAIN_OBJECT));
        dice_1.loadObject();
        Matrix.setIdentityM(dice_1.getModelMatrix(), 0);
        Matrix.translateM(dice_1.getModelMatrix(), 0, 100f, 0f, 0);

        mScene.addObject(dice_1, DICE_MESH_OBJECT_1);
        /*dice_1.createRigidBody();
        //Transform tr = new Transform(new Matrix4f(dice_1.getModelMatrix()));
        //dice_1.get_body().setWorldTransform(tr);
        Random rnd = new Random(System.currentTimeMillis());
        int direction = rnd.nextInt(2);
        float fy = 2f + rnd.nextInt(3) * 1f;
        float fxz = fy * 2f / 3f;
        fxz = direction == 1 && (rnd.nextInt(2) > 0) ? -1*fxz : fxz;
        dice_1.get_body().setLinearVelocity(direction == 0 ? new Vector3f(0f,fy,fxz) : new Vector3f(fxz,fy,0f));
        _world.addRigidBody(dice_1.get_body());*/

        mScene.set_world(_world);

        forceGC_and_Sync();

        mScene.startSimulation();
    }

    public void placeChips() {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            int currentPointIdx = player.getCurrentPoint();
            playersOnWayPoints[currentPointIdx]++;
            int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
            AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

            GLSceneObject chip = new ColorShapeObject(context, mScene.getCachedShader(TERRAIN_OBJECT), 0xFF000000 | player.getColor());
            chip.loadObject();

            PointF chipPlace = getChipPlace(point, playersCnt, true);
            chip.setPosition(chipPlace);
            Matrix.setIdentityM(chip.getModelMatrix(), 0);
            Matrix.translateM(chip.getModelMatrix(), 0, chipPlace.x, -0.1f, chipPlace.y);

            mScene.addObject(chip, CHIP_MESH_OBJECT + "_" + String.format("%d", i));
        }
    }

    public void moveChips() {
        int[] playersOnWayPoints = new int[gameEntity.getGamePoints().size()];

        for (int i = 0; i < gameInstanceEntity.getPlayers().size(); i++) {
            InstancePlayer player = gameInstanceEntity.getPlayers().get(i);
            int currentPointIdx = player.getCurrentPoint();
            playersOnWayPoints[currentPointIdx]++;
            int playersCnt = playersOnWayPoints[currentPointIdx] - 1;
            AbstractGamePoint point = gameEntity.getGamePoints().get(currentPointIdx);

            GLSceneObject chip = getmScene().getObject( CHIP_MESH_OBJECT + "_" + String.format("%d", i));

            PointF chipPlace = getChipPlace(point, playersCnt, true);
            chip.setPosition(chipPlace);
            Matrix.setIdentityM(chip.getModelMatrix(), 0);
            Matrix.translateM(chip.getModelMatrix(), 0, chipPlace.x, -0.1f, chipPlace.y);
        }
    }

    public PointF getChipPlace(AbstractGamePoint endGamePoint, int playersCnt, boolean rotate) {
        double toX2 = endGamePoint.getxPos();
        double toZ2 = endGamePoint.getyPos();

        if(rotate) {
            double angle = getChipRotationAngle(playersCnt);
            toX2 = endGamePoint.getxPos() - 10 * Math.sin(angle);
            toZ2 = endGamePoint.getyPos() - 10 * Math.cos(angle);
        }

        LandObject land = (LandObject) mScene.getObject(TERRAIN_MESH_OBJECT);
        return land.tex2WorldCoord(new PointF((float)toX2, (float)toZ2));
    }

    private double getChipRotationAngle(int playersCnt) {
        if (playersCnt == 0)
            return  0;

        int part = 8, b;
        double angle;

        do {
            angle = 360 / part;
            b = part - 1;

            part /= 2;
        } while ( ((playersCnt & part) == 0) && (part != 1) );

        return Math.toRadians((2 * playersCnt - b) * angle);
    }

    private void drawScene() {
        mScene.drawScene();
    }

}
