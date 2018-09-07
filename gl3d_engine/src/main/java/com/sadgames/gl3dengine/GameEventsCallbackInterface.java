package com.sadgames.gl3dengine;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.camera.GLCamera;
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource;
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject;

import org.luaj.vm2.LuaValue;

public interface GameEventsCallbackInterface {

    void onStopMovingObject(PNodeObject gameObject);
    void onRollingObjectStart(PNodeObject gameObject);
    void onRollingObjectStop(PNodeObject gameObject);

    void onInitGLCamera(GLCamera camera);
    void onInitLightSource(GLLightSource lightSource);

    void onInitPhysics(DynamicsWorld dynamicsWorld);

    void onLoadSceneObjects(GLScene glSceneObject, DynamicsWorld dynamicsWorldObject);

    void onBeforeDrawFrame(long frametime);

    void onPerformUserAction(String action, LuaValue[] params);
}
