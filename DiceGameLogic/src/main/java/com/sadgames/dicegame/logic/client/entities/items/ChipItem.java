package com.sadgames.dicegame.logic.client.entities.items;

import com.sadgames.dicegame.logic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.gl3dengine.glrender.scene.objects.PyramidPrimitiveObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.dicegame.logic.client.GameConst.CHIP_MESH_OBJECT;

public class ChipItem extends PyramidPrimitiveObject { //TODO: Blender3DObject: import blender model -> players_chip

    public static final float CHIP_BOTTOM_HALF_WIDTH = 0.05f;
    public static final float CHIP_HEIGHT = 0.1f;

    private InstancePlayer player;

    public ChipItem(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, InstancePlayer player) {
        super(sysUtilsWrapper, program, 0xFF000000 | player.getColor(), CHIP_BOTTOM_HALF_WIDTH, CHIP_HEIGHT);

        this.player = player;
        setItemName(CHIP_MESH_OBJECT + "_" + player.getName());

        //Matrix4f transformMatrix = new Matrix4f();
        //transformMatrix.setIdentity();
        //transformMatrix.setScale(0.1f);
        //transformMatrix.setTranslation(new Vector3f(0f, 0.1f, 0f));
    }

    public InstancePlayer getPlayer() {
        return player;
    }


}
