package com.sadgames.gl3d_engine.gl_render.scene.objects;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.sysutils.IBitmapWrapper;
import com.sadgames.sysutils.ISysUtilsWrapper;

import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public abstract class TopographicMapObject extends ProceduralSurfaceObject {

    protected enum ColorType {
        CYAN,
        BLUE,
        GREEN,
        YELLOW,
        BROWN,
        WHITE,
        UNKNOWN
    }

    protected static final float MIN_CYAN = 130f + 160f + 232f;
    protected static final float MIN_BLUE = 13f + 30f + 70f;
    protected static final float MIN_GREEN = 0f + 56f + 17f;
    protected static final float MIN_YELLOW = 145f + 133f + 30f;
    protected static final float MIN_BROWN = 83f + 17f + 0f;
    protected static final float MIN_WHITE = 127f + 127f + 127f;

    protected static final float MAX_CYAN = 220f + 245f + 245f;
    protected static final float MAX_BLUE = 129f + 159f + 231f;
    protected static final float MAX_GREEN = 85f + 255f + 164f;
    protected static final float MAX_YELLOW = 246f + 246f + 162f;
    protected static final float MAX_BROWN = 192f + 135f + 58f;
    protected static final float MAX_WHITE = 255f + 255f + 255f;

    protected static final float[] MIN_HEIGHT_VALUES = {0.05f, 0.55f, 0.0f, 0.21f, 1.21f, 2.55f};
    protected static final float[] MAX_HEIGHT_VALUES = {0.5f, 10f, 0.2f, 1.2f, 2.5f, 10f};
    protected static final float[] DELTA_COLOR_VALUES = {MAX_CYAN - MIN_CYAN,
            MAX_BLUE - MIN_BLUE,
            MAX_GREEN - MIN_GREEN,
            MAX_YELLOW - MIN_YELLOW,
            MAX_BROWN - MIN_BROWN,
            MAX_WHITE - MIN_WHITE};

    protected static final float[] MIN_COLOR_VALUES = {MIN_CYAN,
            MIN_BLUE,
            MIN_GREEN,
            MIN_YELLOW,
            MIN_BROWN,
            MIN_WHITE};

    protected static final boolean[] INVERT_LIGHT_FACTOR = {true, true, false, true, true, false};


    protected float scaleX;
    protected float scaleZ;

    public TopographicMapObject(ISysUtilsWrapper sysUtilsWrapper, GLShaderProgram program, String mapName) {
        super(sysUtilsWrapper, TERRAIN_OBJECT, mapName, LAND_SIZE_IN_WORLD_SPACE, program);

        isCastShadow = false;
        setCubeMap(true);
    }

    public float getScaleX() {
        return scaleX;
    }
    public float getScaleZ() {
        return scaleZ;
    }

    @Override
    protected float getYValue(float valX, float valZ, IBitmapWrapper map, float tu, float tv) {
        int xCoord = Math.round((map.getWidth() - 1) * tu);
        int yCoord = Math.round((map.getHeight() - 1) * tv);
        xCoord = xCoord > dimension ? dimension : xCoord;
        yCoord = yCoord > dimension ? dimension : yCoord;

        return getYValueInternal(map, xCoord, yCoord, map.getPixelColor(new Point(xCoord, yCoord)));
    }

    protected float getYValueInternal(IBitmapWrapper map, int xCoord, int yCoord, int vColor) {
        //float y = (float)Math.exp(-1.3 * (valX * valX + valZ * valZ)); !!!SUN and SKY formula (sphere and dome)

        ColorType cType = CheckColorType(vColor);

        if (cType.equals(ColorType.UNKNOWN)) {
            vColor = interpolateUnknownColorValue(map, xCoord, yCoord);
            cType = CheckColorType(vColor);
        }

        float deltaY = getLandScale() * (MAX_HEIGHT_VALUES[cType.ordinal()] - MIN_HEIGHT_VALUES[cType.ordinal()]);
        float minY = getLandScale() * MIN_HEIGHT_VALUES[cType.ordinal()];

        float colorLight = Color.red(vColor) + Color.green(vColor) + Color.blue(vColor) - MIN_COLOR_VALUES[cType.ordinal()];
        float deltaLight = DELTA_COLOR_VALUES[cType.ordinal()];

        float kXZ = colorLight / deltaLight;
        kXZ = INVERT_LIGHT_FACTOR[cType.ordinal()] ? 1.0f - kXZ : kXZ;

        float y = minY + deltaY * kXZ;
        y = cType.equals(ColorType.BLUE) || cType.equals(ColorType.CYAN) ? -y - 0.25f : y;

        return y;
    }

    protected static ColorType CheckColorType(Integer color){
        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);

        if ((G <= B) && (R < G))
            return G <= 0.5 * B ? ColorType.BLUE : ColorType.CYAN; //B <= 231 ? BLUE : CYAN;//
        else if ((R < G) && (B < G))
            return ColorType.GREEN;
        else if ((G <= R) && (B < G))
            return G <= 0.7 * R ? ColorType.BROWN : ColorType.YELLOW;
        else if ((R > G) && (R > B))
            return ColorType.BROWN;
        else if ((G == R) && (B == G) && (R >= 180))
            return ColorType.WHITE;
        else {
            int RR = R;
            return ColorType.UNKNOWN;
        }
    }

    @Override
    protected int getDimension(IBitmapWrapper bmp) {
        return  bmp.getWidth() - 1;
    }

    @NonNull
    protected int interpolateUnknownColorValue(IBitmapWrapper map, int xCoord, int yCoord) {
        int count = 0, R = 0, G = 0, B = 0;

        for (int j = yCoord - 1; j <= yCoord + 1; j++)
            for (int i = xCoord - 1; i <= xCoord + 1; i++)
                try {
                    if ( !((i == xCoord) && (j == yCoord)) && (i <= dimension)
                         && (j <= dimension)
                        ) {
                        int color = map.getPixelColor(new Point(i, j));
                        R += Color.red(color);
                        G += Color.green(color);
                        B += Color.blue(color);

                        count++;
                    }
                } catch (IllegalArgumentException e) {}

        R /= count;
        G /= count;
        B /= count;

        return Color.argb(255, R, G, B);
    }

    public PointF map2WorldCoord(PointF texPoint) {
        PointF result = new PointF();
        result.x = texPoint.x * scaleX  - (LAND_WIDTH / 2);
        result.y = texPoint.y * scaleZ  - (LAND_HEIGHT / 2);

        return result;
    }

}
