package com.sadgames.dicegame.game_logic.items;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.sadgames.dicegame.rest_api.model.Game;
import com.sadgames.dicegame.rest_api.model.points.AbstractGamePoint;
import com.sadgames.gl3d_engine.gl_render.scene.objects.TopographicMapObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.IBitmapWrapper;
import com.sadgames.sysutils.ISysUtilsWrapper;
import com.sadgames.sysutils.platforms.android.AndroidBitmapWrapper;

import java.util.ArrayList;

import javax.vecmath.Vector2f;

public class GameMapObject extends TopographicMapObject {

    private Game gameEntity;

    public GameMapObject(ISysUtilsWrapper sysUtilsWrapper, GLShaderProgram program, Game gameEntity) {
        super(sysUtilsWrapper, program, gameEntity == null ? null : gameEntity.getMapId());

        this.gameEntity = gameEntity;
    }

    public Game getGameEntity() {
        return gameEntity;
    }

    @Override
    protected IBitmapWrapper getReliefMap() {
        return textureResName != null ? getSysUtilsWrapper().iLoadReliefFromDB(textureResName) : null;
    }

    @Override
    protected int loadTexture() {
        Bitmap textureBmp = getSysUtilsWrapper().iGetBitmapFromFile(textureResName);

        scaleX = LAND_WIDTH / textureBmp.getWidth() * 1f;
        scaleZ = LAND_HEIGHT / textureBmp.getHeight() * 1f;

        IBitmapWrapper pic = new AndroidBitmapWrapper(textureBmp); //TODO: remove temp

        ArrayList<Vector2f> way = new ArrayList<>();
        for (AbstractGamePoint point : gameEntity.getGamePoints())
            way.add(new Vector2f(point.xPos, point.yPos));

        pic.drawPath(way, Color.GREEN, Color.RED);

        return BitmapTexture.createInstance(textureBmp).getTextureId();
    }

}
