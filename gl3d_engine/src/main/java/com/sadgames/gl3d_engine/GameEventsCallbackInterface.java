package com.sadgames.gl3d_engine;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.sadgames.gl3d_engine.gl_render.scene.GLCamera;
import com.sadgames.gl3d_engine.gl_render.scene.GLLightSource;
import com.sadgames.gl3d_engine.gl_render.scene.GLScene;
import com.sadgames.gl3d_engine.gl_render.scene.objects.PNodeObject;

public interface GameEventsCallbackInterface {

    void onStopMovingObject(PNodeObject gameObject);
    void onRollingObjectStart(PNodeObject gameObject);
    void onRollingObjectStop(PNodeObject gameObject);

    void onInitGLCamera(GLCamera camera);
    void onInitLightSource(GLLightSource lightSource);

    void onInitPhysics(DynamicsWorld dynamicsWorld);

    void onLoadSceneObjects(GLScene glSceneObject, DynamicsWorld dynamicsWorldObject);
}
