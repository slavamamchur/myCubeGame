package com.sadgames.dicegame.game_logic.entities;

import com.sadgames.dicegame.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.BitmapWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.scene.objects.TopographicMapObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;

import java.util.ArrayList;

import javax.vecmath.Vector2f;

import static com.sadgames.dicegame.game_logic.GameConsts.PATH_COLOR;
import static com.sadgames.dicegame.game_logic.GameConsts.WAY_POINT_COLOR;

public class DiceGameMap extends TopographicMapObject implements LinkedRESTObjectInterface {

    private GameEntity gameEntity;

    public DiceGameMap(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, GameEntity gameEntity) {
        super(sysUtilsWrapper, program, gameEntity == null ? null : gameEntity.getMapId());

        this.gameEntity = gameEntity;
    }

    @Override
    public BasicNamedDbEntity getLinkedRESTObject() {
        return gameEntity;
    }

    @Override
    protected BitmapWrapperInterface getReliefMap() {
        return textureResName != null ? getSysUtilsWrapper().iGetReliefFromFile(textureResName) : null;
    }

    @Override
    protected AbstractTexture loadTexture() {
        BitmapWrapperInterface textureBmp = getSysUtilsWrapper().iGetBitmapFromFile(textureResName);

        scaleX = LAND_WIDTH / textureBmp.getWidth() * 1f;
        scaleZ = LAND_HEIGHT / textureBmp.getHeight() * 1f;
        float scaleFactor = textureBmp.getWidth() * 1f / TopographicMapObject.DEFAULT_TEXTURE_SIZE;

        ArrayList<Vector2f> way = new ArrayList<>();
        for (AbstractGamePoint point : gameEntity.getGamePoints())
            way.add(new Vector2f(point.xPos * scaleFactor, point.yPos * scaleFactor));

        textureBmp.drawPath(way, PATH_COLOR, WAY_POINT_COLOR, scaleFactor);

        return BitmapTexture.createInstance(getSysUtilsWrapper(), textureBmp);
    }

}
