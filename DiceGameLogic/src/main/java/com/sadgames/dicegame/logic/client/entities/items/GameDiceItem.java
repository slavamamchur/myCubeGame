package com.sadgames.dicegame.logic.client.entities.items;

import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

public class GameDiceItem extends Blender3DObject {

    private static final short BOX_SHAPE_TYPE = 1;
    private static final float GAME_DICE_HALF_SIZE = 0.15f;

    public GameDiceItem(SysUtilsWrapperInterface sysUtilsWrapper, String objFileName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, objFileName, program, mass, tag);

        setInitialScale(GAME_DICE_HALF_SIZE);
        setInitialTranslation(0f, 0.08f, 0f);
        setTwoSidedSurface(false);
        setCollisionShapeType(BOX_SHAPE_TYPE);
        setItemName(objFileName);
    }

}
