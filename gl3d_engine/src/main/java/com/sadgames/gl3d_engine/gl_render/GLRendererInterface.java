package com.sadgames.gl3d_engine.gl_render;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.sadgames.gl3d_engine.gl_render.scene.GLScene;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public interface GLRendererInterface {

    GLScene getSceneObject();

    void onSurfaceCreated(GL10 gl, EGLConfig config);

    void onSurfaceChanged(GL10 gl, int width, int height);

    void onDrawFrame(GL10 gl);

    DiscreteDynamicsWorld getPhysicalWorldObject();

}
