package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.cubegames.slava.cubegame.Utils.ColorType;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;

import static com.cubegames.slava.cubegame.Utils.CheckColorType;
import static com.cubegames.slava.cubegame.Utils.ColorType.BLUE;
import static com.cubegames.slava.cubegame.Utils.ColorType.CYAN;
import static com.cubegames.slava.cubegame.Utils.ColorType.UNKNOWN;
import static com.cubegames.slava.cubegame.Utils.DELTA_COLOR_VALUES;
import static com.cubegames.slava.cubegame.Utils.INVERT_LIGHT_FACTOR;
import static com.cubegames.slava.cubegame.Utils.MAX_HEIGHT_VALUES;
import static com.cubegames.slava.cubegame.Utils.MIN_COLOR_VALUES;
import static com.cubegames.slava.cubegame.Utils.MIN_HEIGHT_VALUES;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LAND_INTERPOLATOR_DIM;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public class LandObject extends ProceduralMeshObject {

    public LandObject(Context context, int dimension, GLShaderProgram program, String mapID) {
        super(context, TERRAIN_OBJECT, mapID, LAND_SIZE_IN_WORLD_SPACE, dimension, program);
    }

    @Override
    protected float getYValue(float valX, float valZ, int[] rowPixels, float tu, float tv) {
        //float y = (float)Math.exp(-1.3 * (valX * valX + valZ * valZ)); !!!SUN and SKY formula (sphere and dome)

        int xCoord = Math.round((getTextureBmp().getWidth() - 1) * tu);
        int yCoord = Math.round((getTextureBmp().getHeight() - 1) * tv);
        xCoord = xCoord > 249 ? 249 : xCoord;
        yCoord = yCoord > 249 ? 249 : yCoord;

        int vColor = rowPixels[xCoord];
        ColorType cType = CheckColorType(vColor);

        if (cType.equals(UNKNOWN)) {
            vColor = interpolateUnknownColorValue(xCoord, yCoord);
            cType = CheckColorType(vColor);
        }

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

    @NonNull
    private int interpolateUnknownColorValue(int xCoord, int yCoord) {
        int count = 0, R = 0, G = 0, B = 0;

        for (int j = yCoord - 1; j <= yCoord + 1; j++)
            for (int i = xCoord - 1; i <= xCoord + 1; i++)
                try {
                    if ( !((i == xCoord) && (j == yCoord)) && (i <= LAND_INTERPOLATOR_DIM)
                         && (j <= LAND_INTERPOLATOR_DIM)
                        ) {
                        int color = getTextureBmp().getPixel(i, j);
                        R += Color.red(color);
                        G += Color.green(color);
                        B += Color.blue(color);

                        count++;
                    }
                } catch (IllegalArgumentException e) {
                }

        R /= count;
        G /= count;
        B /= count;

        return Color.argb(255, R, G, B);
    }

}
