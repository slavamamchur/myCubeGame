package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cubegames.slava.cubegame.api.GameMapController;
import com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType;
import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.Utils.loadBitmapFromDB;

public abstract class BitmapTexturedObject extends GLSceneObject {

    private int textureResId = -1;
    private String mapID = null;

    public BitmapTexturedObject(Context context, GLObjectType type, String mapID, GLShaderProgram program) {
        super(context, type, program);

        this.mapID = mapID;
    }

    public BitmapTexturedObject(Context context, GLObjectType type, int textureResId, GLShaderProgram program) {
        super(context, type, program);

        this.textureResId = textureResId;
    }

    @Override
    protected Bitmap getTextureBitmap() {
        if (textureResId >= 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inScaled = false;

            return BitmapFactory.decodeResource(context.getResources(), textureResId, options);
        }
        else if (mapID != null) {
            GameMapController gmc = new GameMapController(context);
            gmc.saveMapImage(new GameMap(mapID));
            return loadBitmapFromDB(context, mapID);
        }
        else
            return null;
    }
}
