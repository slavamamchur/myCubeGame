package com.sadgames.dicegame.ui.framework;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sadgames.dicegame.AndroidRestApiWrapper;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.dicegame.logic.client.DiceGameLogic;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;
import com.sadgames.sysutils.platforms.android.AndroidDiceGameUtilsWrapper;
import com.sadgames.sysutils.platforms.android.AndroidGLES20Renderer;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    private DiceGameLogic gameLogic;

    public GLSurfaceView glMapSurfaceView;
    public AndroidGLES20Renderer glRenderer;

    public MapFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        SysUtilsWrapperInterface sysUtilsWrapper = AndroidDiceGameUtilsWrapper.getInstance(getContext());
        glRenderer = new AndroidGLES20Renderer(sysUtilsWrapper);
        gameLogic = new DiceGameLogic(sysUtilsWrapper, AndroidRestApiWrapper.getInstance(getContext()), glRenderer.getScene());
        glRenderer.getScene().setGameEventsCallBackListener(gameLogic);

        glMapSurfaceView = new MapGLSurfaceView(getContext());
        return  glMapSurfaceView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            gameLogic.initScriptEngine();
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

    public DiceGameLogic getGameLogic() {
        return gameLogic;
    }

}
