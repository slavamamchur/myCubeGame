package com.sadgames.dicegame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.input.GestureDetector;
import com.sadgames.gl3dengine.MyGestureListener;
import com.sadgames.gl3dengine.glrender.GdxExt;
import com.sadgames.gl3dengine.glrender.scene.GLScene;

/**
 * Created by Slava Mamchur on 06.11.2018.
 */
public class MyLwjglApplication extends LwjglApplication {

    public MyLwjglApplication(GLScene listener, LwjglApplicationConfiguration config) {
        super(listener, config);

        GdxExt.input.setInputProcessor(new GestureDetector(new MyGestureListener(listener)));
    }
}
