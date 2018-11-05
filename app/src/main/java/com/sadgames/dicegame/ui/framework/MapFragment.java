package com.sadgames.dicegame.ui.framework;

import android.app.Activity;
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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.sadgames.dicegame.AndroidRestApiWrapper;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.gl3dengine.gamelogic.client.GameLogic;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestApiInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.glrender.FakeGdxApp;
import com.sadgames.gl3dengine.glrender.GdxExt;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.sysutils.common.GdxDbInterface;
import com.sadgames.sysutils.platforms.android.AndroidGLES20Renderer;

import java.util.ArrayList;

public class MapFragment extends Fragment { //TODO: AndroidFragmentApplication

    private Activity activity = null;
    private GameLogic gameLogic;
    private GLScene scene;
    private Audio audio = null;
    private Files files = null;
    private Input input = null;
    private Preferences preferences = null;
    private RestApiInterface restApi = null;
    private GdxDbInterface database = null;
    private Application app = null;
    private boolean isWaitingForAudio = false;
    private int wasFocusChanged = -1;

    protected boolean firstResume = true;

    public MapGLSurfaceView glMapSurfaceView;
    public AndroidGLES20Renderer glRenderer;

    public MapFragment() {}

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //TODO: initializeForView
        GdxExt.app = new FakeGdxApp();
        gameLogic = new GameLogic();
        scene = new GLScene(gameLogic);
        glRenderer = new AndroidGLES20Renderer(getContext(), scene);
        glMapSurfaceView = (MapGLSurfaceView) glRenderer.getView();

        glMapSurfaceView.setScene(scene);

        return  glMapSurfaceView;
    }

    @Override
    public void onDetach() {
        AndroidRestApiWrapper.releaseInstance();

        super.onDetach();
    }

    @Override
    public void onPause() {
        boolean isContinuous = glRenderer.isContinuousRendering();
        boolean isContinuousEnforced = AndroidGLES20Renderer.enforceContinuousRendering;
        AndroidGLES20Renderer.enforceContinuousRendering = true;

        app = GdxExt.app;
        audio = GdxExt.audio;
        files = GdxExt.files;
        preferences = GdxExt.preferences;
        restApi = GdxExt.restAPI;
        database = GdxExt.dataBase;  //TODO: add input

        glRenderer.setContinuousRendering(true);
        glRenderer.pause();

        if (activity != null && activity.isFinishing()) {
            glRenderer.destroy();
        }

        AndroidGLES20Renderer.enforceContinuousRendering = isContinuousEnforced;
        glRenderer.setContinuousRendering(isContinuous);

        glMapSurfaceView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        glMapSurfaceView.onResume();

        if (!firstResume) {
            GdxExt.app = app;
            GdxExt.audio = audio;
            GdxExt.files = files;
            GdxExt.preferences = preferences;
            GdxExt.restAPI = restApi;
            GdxExt.dataBase = database; //TODO: add input

            glRenderer.resume();
        } else
            firstResume = false;

        /*this.isWaitingForAudio = true; //TODO: find solution
        if (this.wasFocusChanged == 1 || this.wasFocusChanged == -1) {
            ((AndroidAudio)this.audio).resume();
            this.isWaitingForAudio = false;
        }*/

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
