package com.sadgames.dicegame.logic.client.entities.items;

import com.sadgames.dicegame.logic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.logic.client.GameConst.CHIP_MESH_OBJECT;

public class ChipItem extends Blender3DObject {
    private static final float CHIP_DEFAULT_WEIGHT = 1.0f;

    private InstancePlayer player;

    public ChipItem(SysUtilsWrapperInterface sysUtilsWrapper, GLShaderProgram program, InstancePlayer player) {
        super(sysUtilsWrapper, CHIP_MESH_OBJECT, program, getChipColor(player), CHIP_DEFAULT_WEIGHT, COLLISION_OBJECT);

        this.player = player;
        this.initialScale = 0.2f;
        this.initialTranslation = new Vector3f(0f, 0.08f, 0f);

        setTwoSidedSurface(false);
        setCastShadow(false);//TODO: test!!!
        setItemName(CHIP_MESH_OBJECT + "_" + player.getName());
    }

    @SuppressWarnings("unused") public InstancePlayer getPlayer() {
        return player;
    }

    @SuppressWarnings("all")
    public static int getChipColor(InstancePlayer player) {
        return 0xFF000000 | player.getColor();
    }
}
