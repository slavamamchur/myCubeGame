package com.cubegames.slava.cubegame.mapgl;

import android.content.Context;
import android.graphics.Color;

import com.cubegames.slava.cubegame.Utils.ColorType;

import static com.cubegames.slava.cubegame.Utils.CheckColorType;
import static com.cubegames.slava.cubegame.Utils.ColorType.BLUE;
import static com.cubegames.slava.cubegame.Utils.ColorType.CYAN;
import static com.cubegames.slava.cubegame.Utils.DELTA_COLOR_VALUES;
import static com.cubegames.slava.cubegame.Utils.INVERT_LIGHT_FACTOR;
import static com.cubegames.slava.cubegame.Utils.MAX_HEIGHT_VALUES;
import static com.cubegames.slava.cubegame.Utils.MIN_COLOR_VALUES;
import static com.cubegames.slava.cubegame.Utils.MIN_HEIGHT_VALUES;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.mapgl.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public class LandObject extends ProceduralMeshObject {

    public LandObject(Context context, int dimension, GLShaderProgram program, String mapID) {
        super(context, TERRAIN_OBJECT, mapID, LAND_SIZE_IN_WORLD_SPACE, dimension, program);
    }

    @Override
    protected float getValueY(float valX, float valZ, int[] rowPixels, float tv) {
        //float y = (float)Math.exp(-1.3 * (valX * valX + valZ * valZ));

        int vColor = rowPixels[Math.round((getTextureBmp().getWidth() - 1) * tv)];
        ColorType cType = CheckColorType(vColor);

        float deltaY = getLandScale() * (MAX_HEIGHT_VALUES[cType.ordinal()] - MIN_HEIGHT_VALUES[cType.ordinal()]);
        float minY = getLandScale() * MIN_HEIGHT_VALUES[cType.ordinal()];

        float colorLight = Color.red(vColor) + Color.green(vColor) + Color.blue(vColor) - MIN_COLOR_VALUES[cType.ordinal()];
        float deltaLight = DELTA_COLOR_VALUES[cType.ordinal()];

        float kXZ = colorLight / deltaLight;
        kXZ = INVERT_LIGHT_FACTOR[cType.ordinal()] ? 1.0f - kXZ : kXZ;

        float y = minY + deltaY * kXZ;
        y = cType.equals(BLUE) || cType.equals(CYAN) ? -y : y;

        return y;
    }

}
