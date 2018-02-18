package com.sadgames.gl3dengine.glrender;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.sadgames.gl3dengine.GameEventsCallbackInterface;
import com.sadgames.gl3dengine.glrender.scene.GLScene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public interface GLRendererInterface {

    GLScene getScene();

    void onSurfaceCreated(GL10 gl, EGLConfig config);

    void onSurfaceChanged(GL10 gl, int width, int height);

    void onDrawFrame(GL10 gl);

    DiscreteDynamicsWorld getPhysicalWorldObject();
    void setGameEventsCallBackListener(GameEventsCallbackInterface gameEventsCallBackListener);

}
