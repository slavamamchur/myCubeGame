package com.sadgames.dicegame.logic.client.entities.items;

import com.sadgames.dicegame.logic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.gl3d_engine.gl_render.scene.objects.PyramidPrimitiveObject;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;

import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.CHIP_MESH_OBJECT;

public class ChipItem extends PyramidPrimitiveObject {

    public static final float CHIP_BOTTOM_HALF_WIDTH = 0.05f;
    public static final float CHIP_HEIGHT = 0.1f;

    private InstancePlayer player;

    public ChipItem(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, InstancePlayer player) {
        super(sysUtilsWrapper, program, 0xFF000000 | player.getColor(), CHIP_BOTTOM_HALF_WIDTH, CHIP_HEIGHT);

        this.player = player;
        setItemName(CHIP_MESH_OBJECT + "_" + player.getName());
    }

    public InstancePlayer getPlayer() {
        return player;
    }


}
