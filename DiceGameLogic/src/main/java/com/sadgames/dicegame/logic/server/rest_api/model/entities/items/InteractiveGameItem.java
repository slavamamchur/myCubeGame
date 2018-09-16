package com.sadgames.dicegame.logic.server.rest_api.model.entities.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import javax.vecmath.Vector3f;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InteractiveGameItem {

    public String itemName;
    public String itemParentName;

    public float xPos;
    public float yPos;
    public float zPos;
    public float initialScale;

    public boolean hasTwoSidedSurface;
    public short collisionShapeType;
    public float mass;
    public int tag;

    public GLObjectType type;

    public String onInitEventHandler;

    @SuppressWarnings("unused")public InteractiveGameItem() {}

    public InteractiveGameItem(String itemName,
                               String itemParentName,
                               Vector3f pos,
                               float initialScale,
                               boolean hasTwoSidedSurface,
                               short collisionShapeType,
                               float mass,
                               int tag,
                               GLObjectType type,
                               String onInitEventHandler
                               )
    {
        this.itemName = itemName;
        this.itemParentName = itemParentName;
        this.xPos = pos.x;
        this.yPos = pos.y;
        this.zPos = pos.z;
        this.initialScale = initialScale;
        this.hasTwoSidedSurface = hasTwoSidedSurface;
        this.collisionShapeType = collisionShapeType;
        this.mass = mass;
        this.tag = tag;
        this.type = type;
        this.onInitEventHandler = onInitEventHandler;
    }

    public Blender3DObject createSceneObject(SysUtilsWrapperInterface sysUtilsWrapper, GLScene scene) {
        Blender3DObject object = new Blender3DObject(sysUtilsWrapper,
                                                     itemName,
                                                     scene.getCachedShader(type),
                                                     mass,
                                                     tag);

        initObject(object);

        return object;
    }

    @SuppressWarnings("unused")
    public Blender3DObject createSceneObject(SysUtilsWrapperInterface sysUtilsWrapper, GLScene scene, int color) {
        Blender3DObject object = new Blender3DObject(sysUtilsWrapper,
                itemName,
                scene.getCachedShader(type),
               0xFF000000 | color,
                mass,
                tag);

        initObject(object);

        return object;
    }

    private void initObject(Blender3DObject object) {
        object.setItemName(itemName);
        object.setInitialScale(initialScale);
        object.setInitialTranslation(xPos, yPos, zPos);
        object.setTwoSidedSurface(hasTwoSidedSurface);
        object.setCollisionShapeType(collisionShapeType);
    }
}
