package com.sadgames.gl3dengine.gamelogic.client.entities;

import com.sadgames.gl3dengine.gamelogic.client.GameConst;
import com.sadgames.gl3dengine.gamelogic.client.GameLogic;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.LinkedRESTObjectInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.sysutils.common.CommonUtils.getBitmapFromFile;
import static com.sadgames.sysutils.common.CommonUtils.packToETC1;

public class GameMap extends TopographicMapObject implements LinkedRESTObjectInterface {

    private GameEntity gameEntity;
    private GameLogic gameLogic;

    public GameMap(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, GameEntity gameEntity, GameLogic gameLogic) {
        super(sysUtilsWrapper, program, gameEntity == null ? null : gameEntity.getMapId());

        //castShadow = true;
        this.gameEntity = gameEntity;
        this.gameLogic = gameLogic;

        setCubeMap(true);
        setGlCubeMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.SEA_BOTTOM_TEXTURE));
        setGlNormalMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.NORMALMAP_TEXTURE));
        setGlDUDVMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.DUDVMAP_TEXTURE));

        //TODO: Remove
        setGlBlendingMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.BLENDING_MAP_TEXTURE));
    }

    @Override
    public BasicNamedDbEntity getLinkedRESTObject() {
        return gameEntity;
    }

    @Override
    protected BitmapWrapperInterface getReliefMap() {
        return textureResName != null ? getBitmapFromFile(getSysUtilsWrapper(), textureResName, true) : null;
    }

    @Override
    public AbstractTexture loadTexture() {
        BitmapWrapperInterface textureBmp = getBitmapFromFile(getSysUtilsWrapper(), textureResName, false);
        scaleX = LAND_WIDTH / textureBmp.getWidth() * 1f;
        scaleZ = LAND_HEIGHT / textureBmp.getHeight() * 1f; //todo: get from glTexture

        gameLogic.onPrepareMapTexture(textureBmp);//TODO: Remove

        textureBmp = packToETC1(getSysUtilsWrapper(), textureBmp);
        AbstractTexture glTexture = BitmapTexture.createInstance(textureBmp);
        TextureCacheManager textureCache = TextureCacheManager.getInstance(getSysUtilsWrapper());
        textureCache.putItem(glTexture, textureResName, textureCache.getItemSize(glTexture) );

        return glTexture;
    }

}
