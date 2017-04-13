package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;
import android.graphics.Bitmap;

import com.cubegames.slava.cubegame.api.GameMapController;
import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.Utils.loadBitmapFromDB;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;

public class LandObject extends ProceduralMeshObject {

    private String mapID;

    public LandObject(Context context, int dimension, GLShaderProgram program, String mapID) {
        super(context, TERRAIN_OBJECT, 3.0f, 3.0f, dimension, program);

        this.mapID =  mapID;
    }

    @Override
    protected float getValueY(float valX, float valZ, float tu, float tv) {
        //TODO: generate Y value by texture point color -> bmp -> ByteBuffer -> optimize or line(dz) to array
        /*int color = getTextureBitmap().getPixel(Math.round((getTextureBitmap().getWidth() - 1) * tu),
                                                Math.round((getTextureBitmap().getHeight() - 1) * tv));
        float y = (float)Math.exp(-3 * (valX * valX + valZ * valZ));

        if (CheckColorType(color).equals(BLUE))
            y = -y;*/

        return 0;//y;
    }

    @Override
    protected Bitmap getTextureBitmap() {
        GameMapController gmc = new GameMapController(context);
        gmc.saveMapImage(new GameMap(mapID));
        return loadBitmapFromDB(context, mapID);
    }
}
