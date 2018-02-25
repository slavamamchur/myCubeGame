package com.sadgames.dicegame.logic.client.entities;

import com.sadgames.dicegame.logic.server.rest_api.LinkedRESTObjectInterface;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.ArrayList;

import javax.vecmath.Vector2f;

import static com.sadgames.dicegame.logic.client.GameConst.BLENDING_MAP_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.DUDVMAP_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.NORMALMAP_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.PATH_COLOR;
import static com.sadgames.dicegame.logic.client.GameConst.SEA_BOTTOM_TEXTURE;
import static com.sadgames.dicegame.logic.client.GameConst.WAY_POINT_COLOR;

public class DiceGameMap extends TopographicMapObject implements LinkedRESTObjectInterface {

    private GameEntity gameEntity;

    public DiceGameMap(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, GameEntity gameEntity) {
        super(sysUtilsWrapper, program, gameEntity == null ? null : gameEntity.getMapId());

        this.gameEntity = gameEntity;
        setGlCubeMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(SEA_BOTTOM_TEXTURE));
        setGlNormalMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(NORMALMAP_TEXTURE));
        setGlDUDVMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(DUDVMAP_TEXTURE));
        setGlBlendingMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(BLENDING_MAP_TEXTURE));
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
    public AbstractTexture loadTexture() {
        BitmapWrapperInterface textureBmp = getSysUtilsWrapper().iGetBitmapFromFile(textureResName);
        scaleX = LAND_WIDTH / textureBmp.getWidth() * 1f;
        scaleZ = LAND_HEIGHT / textureBmp.getHeight() * 1f;

        drawPath(textureBmp);

        textureBmp = getSysUtilsWrapper().packToETC1(textureBmp);
        AbstractTexture glTexture = BitmapTexture.createInstance(textureBmp);
        TextureCacheManager textureCache = TextureCacheManager.getInstance(getSysUtilsWrapper());
        textureCache.putItem(glTexture, textureResName, textureCache.getItemSize(glTexture) );

        return glTexture;
    }

    private void drawPath(BitmapWrapperInterface textureBmp) {
        float scaleFactor = textureBmp.getWidth() * 1f / TopographicMapObject.DEFAULT_TEXTURE_SIZE;
        ArrayList<Vector2f> way = new ArrayList<>();
        for (AbstractGamePoint point : gameEntity.getGamePoints())
            way.add(new Vector2f(point.xPos * scaleFactor, point.yPos * scaleFactor));
        textureBmp.drawPath(way, PATH_COLOR, WAY_POINT_COLOR, scaleFactor);
    }

}
