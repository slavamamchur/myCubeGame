package com.sadgames.gl3dengine.gamelogic.client.entities;

import com.sadgames.gl3dengine.gamelogic.client.DiceGameLogic;
import com.sadgames.gl3dengine.gamelogic.client.GameConst;
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

public class GameMap extends TopographicMapObject implements LinkedRESTObjectInterface {

    private GameEntity gameEntity;
    private DiceGameLogic gameLogic;

    public GameMap(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, GameEntity gameEntity, DiceGameLogic gameLogic) {
        super(sysUtilsWrapper, program, gameEntity == null ? null : gameEntity.getMapId());

        //castShadow = true;
        this.gameEntity = gameEntity;
        this.gameLogic = gameLogic;

        setCubeMap(true);
        setGlCubeMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.SEA_BOTTOM_TEXTURE));
        setGlNormalMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.NORMALMAP_TEXTURE));
        setGlDUDVMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.DUDVMAP_TEXTURE));
        setGlBlendingMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.BLENDING_MAP_TEXTURE));
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

        gameLogic.onPrepareMapTexture(textureBmp);

        textureBmp = getSysUtilsWrapper().packToETC1(textureBmp);
        AbstractTexture glTexture = BitmapTexture.createInstance(textureBmp);
        TextureCacheManager textureCache = TextureCacheManager.getInstance(getSysUtilsWrapper());
        textureCache.putItem(glTexture, textureResName, textureCache.getItemSize(glTexture) );

        return glTexture;
    }

}
