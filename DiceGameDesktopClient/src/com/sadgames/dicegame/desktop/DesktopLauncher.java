package com.sadgames.dicegame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.cubegames.vaa.client.Consts;
import com.cubegames.vaa.client.RestClient;
import com.sadgames.gl3dengine.gamelogic.client.GameLogic;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.glrender.GdxExt;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.sysutils.common.SettingsManagerInterface;

import java.io.File;
import java.util.ArrayList;

import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public class DesktopLauncher {

	private static final String TEST_GAME_INSTANCE_ID = "5bb9b8ed674b7d1ff899ca75";

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 618;
		config.vSyncEnabled = true;
		config.fullscreen = false;

		GdxExt.restAPI = DesktopRestApiWrapper.getInstance();
		GdxExt.files = new LwjglFiles();
		GdxExt.preferences = new LwjglPreferences(new LwjglFileHandle(new File(config.preferencesDirectory, "DiceGamePrefs"),
												  config.preferencesFileType));
		GdxExt.dataBase = new DesktopGdxDbWrapper();

		GameLogic logic = new GameLogic();
		GLScene scene = new GLScene(logic);

		/** TEST CODE ------------------------------------------------------------------------------ */
		RestClient restClient = new RestClient(getSettingsManager().getWebServiceUrl(Consts.BASE_URL));
		if (checkLogin(restClient)) {
			GameInstanceEntity gameInst = restClient.getInstance(TEST_GAME_INSTANCE_ID);
			logic.setGameInstanceEntity(gameInst);
			logic.setSavedPlayers(new ArrayList<>(gameInst != null ? gameInst.getPlayers() : null));
			GameEntity game = gameInst.getGame();

			logic.setGameEntity(game);
			if (game != null) {
				GameMapEntity map = new GameMapEntity();
				map.setId(game.getMapId());
				logic.setMapEntity(map);

				scene.setLuaEngine(logic.initScriptEngine(scene));
			}

			new LwjglApplication(scene, config);
		}
		else
			throw new RuntimeException("Can not login to server.");
		/** TEST CODE ------------------------------------------------------------------------------ */
	}

	private static boolean checkLogin(RestClient restClient) {
		SettingsManagerInterface setings = getSettingsManager();
		setings.setIn_2D_Mode(false);
		boolean result = setings.isLoggedIn();

		if (!result) {
			restClient.login("dima", "123");
			setings.setAuthToken(restClient.getToken());

			result = setings.isLoggedIn();
		}
		else
			restClient.setToken(setings.getAuthToken());

		return result;
	}

}
