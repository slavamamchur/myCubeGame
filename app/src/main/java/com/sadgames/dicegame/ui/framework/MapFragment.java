package com.sadgames.dicegame.ui.framework;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sadgames.dicegame.AndroidRestApiWrapper;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.gl3dengine.gamelogic.client.GameLogic;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.sysutils.platforms.android.AndroidGLES20Renderer;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private GameLogic gameLogic;
    private GLScene scene;

    public MapGLSurfaceView glMapSurfaceView;
    public AndroidGLES20Renderer glRenderer;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        scene = new GLScene();
        glRenderer = new AndroidGLES20Renderer(scene);
        gameLogic = new GameLogic();
        scene.setGameEventsCallBackListener(gameLogic);

        glMapSurfaceView = new MapGLSurfaceView(getContext()); //TODO: init from libGDX but save touche listener
        return  glMapSurfaceView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        glMapSurfaceView.setScene(scene);
        glMapSurfaceView.setRenderer(glRenderer);
    }

    @Override
    public void onDetach() {
        AndroidRestApiWrapper.releaseInstance();

        super.onDetach();
    }

    @Override
    public void onPause() {
        glMapSurfaceView.onPause();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        glMapSurfaceView.onResume();
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
