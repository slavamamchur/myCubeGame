package com.sadgames.dicegame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.sadgames.gl3dengine.gamelogic.client.GameLogic;
import com.sadgames.gl3dengine.glrender.GdxExt;
import com.sadgames.gl3dengine.glrender.scene.GLScene;

import java.io.File;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration(); //TODO: setup config
		GdxExt.restAPI = DesktopRestApiWrapper.getInstance();
		GdxExt.files = new LwjglFiles();
		GdxExt.preferences = new LwjglPreferences(new LwjglFileHandle(new File(config.preferencesDirectory, "DiceGamePrefs"),
												  config.preferencesFileType));

		GameLogic logic = new GameLogic();
		GLScene scene = new GLScene(logic);
		//TODO: Init database and check login, then create hardcoded entity and call logic.InitMap(entity)
		scene.setLuaEngine(logic.initScriptEngine(scene)); //TODO: remove stub

		new LwjglApplication(scene, config);
	}
}
