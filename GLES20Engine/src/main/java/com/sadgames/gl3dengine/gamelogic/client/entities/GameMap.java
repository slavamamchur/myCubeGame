package com.sadgames.gl3dengine.gamelogic.client.entities;

import com.badlogic.gdx.graphics.Pixmap;
import com.sadgames.gl3dengine.gamelogic.client.GameConst;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.LinkedRESTObjectInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameEntity;
import com.sadgames.gl3dengine.glrender.scene.objects.TopographicMapObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.gl3dengine.manager.TextureCacheManager;

import java.sql.SQLException;

import static com.sadgames.sysutils.common.DBUtils.loadBitmapFromDB;

public class GameMap extends TopographicMapObject implements LinkedRESTObjectInterface {

    private GameEntity gameEntity;

    public GameMap(GLShaderProgram program, GameEntity gameEntity) {
        super(program, gameEntity == null ? null : gameEntity.getMapId());

        //castShadow = true;
        this.gameEntity = gameEntity;

        //TODO: get from material object
        setCubeMap(true);
        setGlCubeMap(TextureCacheManager.getInstance().getItem(GameConst.SEA_BOTTOM_TEXTURE));
        setGlNormalMap(TextureCacheManager.getInstance().getItem(GameConst.NORMALMAP_TEXTURE));
        setGlDUDVMap(TextureCacheManager.getInstance().getItem(GameConst.DUDVMAP_TEXTURE));
    }

    @Override
    public BasicNamedDbEntity getLinkedRESTObject() {
        return gameEntity;
    }

    @Override
    protected Pixmap getReliefMap() {
        Pixmap result = null;

        try {
            byte[] data = loadBitmapFromDB(textureResName, true);
            result = textureResName != null && data != null ? new Pixmap(data, 0, data.length) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public AbstractTexture loadTexture() {
        AbstractTexture glTexture = super.loadTexture();
        scaleX = LAND_WIDTH / glTexture.getWidth() * 1f;
        scaleZ = LAND_HEIGHT / glTexture.getHeight() * 1f;

        return glTexture;
    }

}
