package com.sadgames.gl3dengine.glrender;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.sadgames.gl3dengine.glrender.scene.GLScene;

public interface GLRendererInterface {

    GLScene getScene();

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();

    DiscreteDynamicsWorld getPhysicalWorldObject();

    void onDispose();

}
