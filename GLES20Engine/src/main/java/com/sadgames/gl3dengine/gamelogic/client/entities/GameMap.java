package com.sadgames.gl3dengine.gamelogic.client.entities;

import com.sadgames.gl3dengine.gamelogic.client.GameConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.LinkedRESTObjectInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.manager.TextureCacheManager;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.sysutils.common.CommonUtils.getBitmapFromFile;

public class GameMap extends TopographicMapObject implements LinkedRESTObjectInterface {

    private GameEntity gameEntity;

    public GameMap(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, GameEntity gameEntity) {
        super(sysUtilsWrapper, program, gameEntity == null ? null : gameEntity.getMapId());

        //castShadow = true;
        this.gameEntity = gameEntity;

        //TODO: get from material object
        setCubeMap(true);
        setGlCubeMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.SEA_BOTTOM_TEXTURE));
        setGlNormalMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.NORMALMAP_TEXTURE));
        setGlDUDVMap(TextureCacheManager.getInstance(sysUtilsWrapper).getItem(GameConst.DUDVMAP_TEXTURE));
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
        AbstractTexture glTexture = super.loadTexture();
        scaleX = LAND_WIDTH / glTexture.getWidth() * 1f;
        scaleZ = LAND_HEIGHT / glTexture.getHeight() * 1f;

        return glTexture;
    }

}
