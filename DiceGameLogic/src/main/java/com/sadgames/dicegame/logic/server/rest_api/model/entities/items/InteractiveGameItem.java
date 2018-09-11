package com.sadgames.dicegame.logic.server.rest_api.model.entities.items;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import com.sadgames.gl3dengine.glrender.scene.GLScene;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import javax.vecmath.Vector3f;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InteractiveGameItem implements Parcelable {

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

    public InteractiveGameItem() {}

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

    protected InteractiveGameItem(Parcel in) {
        loadFromParcel(in);
    }

    public static final Creator<InteractiveGameItem> CREATOR = new Creator<InteractiveGameItem>() {
        @Override
        public InteractiveGameItem createFromParcel(Parcel in) {
            return new InteractiveGameItem(in);
        }

        @Override
        public InteractiveGameItem[] newArray(int size) {
            return new InteractiveGameItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        save2Parcel(dest);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    private void loadFromParcel(Parcel in) {
        itemName = in.readString();
        itemParentName = in.readString();

        xPos = in.readFloat();
        yPos = in.readFloat();
        zPos = in.readFloat();

        initialScale = in.readFloat();

        boolean[] isTwoSided = new boolean[1];
        in.readBooleanArray(isTwoSided);
        hasTwoSidedSurface = isTwoSided[0];

        collisionShapeType = (short) in.readInt();
        mass = in.readFloat();
        tag = in.readInt();
        type = GLObjectType.values()[in.readInt()];

        onInitEventHandler = in.readString();
    }

    private void save2Parcel(Parcel dest) {
        dest.writeString(itemName);
        dest.writeString(itemParentName);

        dest.writeFloat(xPos);
        dest.writeFloat(yPos);
        dest.writeFloat(zPos);

        dest.writeFloat(initialScale);

        dest.writeBooleanArray(new boolean[]{hasTwoSidedSurface});

        dest.writeInt(collisionShapeType);
        dest.writeFloat(mass);
        dest.writeInt(tag);
        dest.writeInt(type.ordinal());

        dest.writeString(onInitEventHandler);
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
