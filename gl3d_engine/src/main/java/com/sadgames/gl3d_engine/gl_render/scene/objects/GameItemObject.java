package com.sadgames.gl3d_engine.gl_render.scene.objects;

import com.sadgames.gl3d_engine.gl_render.GLRenderConsts;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.SysUtilsWrapperInterface;

public abstract class GameItemObject extends PNodeObject {
    private static int itemNumber = 0;

    private String itemName;

    public GameItemObject(SysUtilsWrapperInterface sysUtilsWrapper, GLRenderConsts.GLObjectType type, String textureResName, GLShaderProgram program, float mass, int tag) {
        super(sysUtilsWrapper, type, textureResName, program, mass, tag);
        initItem();
    }

    public GameItemObject(SysUtilsWrapperInterface sysUtilsWrapper, GLRenderConsts.GLObjectType type, GLShaderProgram program, int color, float mass, int tag) {
        super(sysUtilsWrapper, type, program, color, mass, tag);
        initItem();
    }

    protected void initItem() {
        itemName = "ITEM_" + itemNumber;
        itemNumber++;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
