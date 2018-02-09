package com.cubegames.slava.cubegame.gl3d_engine.gl_render;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.GLCamera;
import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.GLLightSource;
import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.GLScene;
import com.cubegames.slava.cubegame.gl3d_engine.gl_render.scene.objects.PNode;

public interface IGameEventsCallBack {

    void onStopMovingObject(PNode gameObject);
    void onRollingObjectStart(PNode gameObject);
    void onRollingObjectStop(PNode gameObject);

    void onInitGLCamera(GLCamera camera);
    void onInitLightSource(GLLightSource lightSource);

    void onInitPhysics(DynamicsWorld dynamicsWorld);

    void onLoadSceneObjects(GLScene glSceneObject, DynamicsWorld dynamicsWorldObject);
}
