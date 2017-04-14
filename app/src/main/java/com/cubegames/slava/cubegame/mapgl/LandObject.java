package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;
import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.api.GameMapController;
import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.Utils.CheckColorType;
import static com.cubegames.slava.cubegame.Utils.ColorType.BLUE;
import static com.cubegames.slava.cubegame.Utils.loadBitmapFromDB;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;

public class LandObject extends ProceduralMeshObject {

    private String mapID;

    public LandObject(Context context, int dimension, GLShaderProgram program, String mapID) {
        super(context, TERRAIN_OBJECT, 3.0f, 3.0f, dimension, program);

        this.mapID =  mapID;
    }

    @Override
    protected float getValueY(float valX, float valZ, int[] rowPixels, float tv) {
        //TODO: generate Y value by texture point color
        float y = (float)Math.exp(-1.3 * (valX * valX + valZ * valZ));
        y = CheckColorType(rowPixels[Math.round((getTextureBmp().getWidth() - 1) * tv)]).equals(BLUE) ? -y : y;

        return y; //TODO: rivers layer as separate texture!!!
    }

    @Override
    protected Bitmap getTextureBitmap() {
        GameMapController gmc = new GameMapController(context);
        gmc.saveMapImage(new GameMap(mapID));
        return loadBitmapFromDB(context, mapID);
    }
}
