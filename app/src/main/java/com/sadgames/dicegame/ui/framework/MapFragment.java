package com.sadgames.dicegame.ui.framework;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.sadgames.dicegame.AndroidRestApiWrapper;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.gl3dengine.gamelogic.client.GameLogic;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.glrender.GdxExt;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.sysutils.common.GdxDbInterface;

import java.util.ArrayList;

public class MapFragment extends AndroidFragmentApplication {

    private GameLogic gameLogic;
    private GLScene scene;
    private Preferences preferences = null;
    private RestApiInterface restApi = null;
    private GdxDbInterface database = null;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        gameLogic = new GameLogic();
        scene = new GLScene(gameLogic);

        return initializeForView(scene);
    }

    @Override
    public void onDetach() {
        AndroidRestApiWrapper.releaseInstance();

        super.onDetach();
    }

    @Override
    public void onPause() {
        preferences = GdxExt.preferences;
        restApi = GdxExt.restAPI;
        database = GdxExt.dataBase;

        super.onPause();
    }

    @Override
    public void onResume() {
        if (!firstResume) {
            GdxExt.preferences = preferences;
            GdxExt.restAPI = restApi;
            GdxExt.dataBase = database;
        }

        super.onResume();
    }

    public void InitMap(GameMapEntity map) {
        gameLogic.setMapEntity(map);
    }

    public void InitMap(GameEntity game) {
        gameLogic.setGameEntity(game);

        if (game != null) {
            GameMapEntity map = new GameMapEntity();
            map.setId(game.getMapId());
            InitMap(map);

            scene.setLuaEngine(gameLogic.initScriptEngine(scene));
        }
    }

    public void InitMap(GameInstanceEntity gameInst) {
        gameLogic.setGameInstanceEntity(gameInst);
        gameLogic.setSavedPlayers(new ArrayList<>(gameInst != null ? gameInst.getPlayers() : null));
        InitMap(gameInst == null ? null : gameInst.getGame());
    }

    public void saveMapImage(Intent data, GameMapEntity map){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                RestApiService.startActionUploadMapImage(getContext(), map, picturePath);
            }

            cursor.close();
        }
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }
    public GLScene getScene() {
        return scene;
    }
}
