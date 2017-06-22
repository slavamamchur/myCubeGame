package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.cubegames.slava.cubegame.Utils.ColorType;
import com.cubegames.slava.cubegame.api.GameMapController;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameMap;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;

import static com.cubegames.slava.cubegame.Utils.CheckColorType;
import static com.cubegames.slava.cubegame.Utils.ColorType.BLUE;
import static com.cubegames.slava.cubegame.Utils.ColorType.CYAN;
import static com.cubegames.slava.cubegame.Utils.ColorType.UNKNOWN;
import static com.cubegames.slava.cubegame.Utils.DELTA_COLOR_VALUES;
import static com.cubegames.slava.cubegame.Utils.INVERT_LIGHT_FACTOR;
import static com.cubegames.slava.cubegame.Utils.MAX_HEIGHT_VALUES;
import static com.cubegames.slava.cubegame.Utils.MIN_COLOR_VALUES;
import static com.cubegames.slava.cubegame.Utils.MIN_HEIGHT_VALUES;
import static com.cubegames.slava.cubegame.Utils.loadGLTexture;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType.TERRAIN_OBJECT;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.LAND_SIZE_IN_WORLD_SPACE;

public class LandObject extends ProceduralMeshObject {
    private Game gameEntity;
    private float scaleX;
    private float scaleZ;

    public LandObject(Context context, GLShaderProgram program, Game gameEntity) {
        super(context, TERRAIN_OBJECT, gameEntity == null ? null : gameEntity.getMapId(), LAND_SIZE_IN_WORLD_SPACE, program);

        this.gameEntity = gameEntity;
    }

    public Game getGameEntity() {
        return gameEntity;
    }
    public float getScaleX() {
        return scaleX;
    }
    public float getScaleZ() {
        return scaleZ;
    }

    @Override
    protected float getYValue(float valX, float valZ, Bitmap map, float tu, float tv) {
        int xCoord = Math.round((map.getWidth() - 1) * tu);
        int yCoord = Math.round((map.getHeight() - 1) * tv);
        xCoord = xCoord > dimension ? dimension : xCoord;
        yCoord = yCoord > dimension ? dimension : yCoord;

        return getYValueInternal(map, xCoord, yCoord, map.getPixel(xCoord, yCoord));
    }

    protected float getYValueInternal(Bitmap map, int xCoord, int yCoord, int vColor) {
        //float y = (float)Math.exp(-1.3 * (valX * valX + valZ * valZ)); !!!SUN and SKY formula (sphere and dome)

        ColorType cType = CheckColorType(vColor);

        if (cType.equals(UNKNOWN)) {
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
        y = cType.equals(BLUE) || cType.equals(CYAN) ? -y - 0.25f : y;

        return y;
    }

    @Override
    protected Bitmap getReliefMap() {
        if (mapID != null) {
            GameMapController gmc = new GameMapController(context);
            GameMap map = gmc.find(mapID);
            return gmc.getMapRelief(map);
        }
        else
            return null;
    }

    @Override
    protected int getDimension(Bitmap bmp) {
        return  bmp.getWidth() - 1;
    }

    @NonNull
    private int interpolateUnknownColorValue(Bitmap map, int xCoord, int yCoord) {
        int count = 0, R = 0, G = 0, B = 0;

        for (int j = yCoord - 1; j <= yCoord + 1; j++)
            for (int i = xCoord - 1; i <= xCoord + 1; i++)
                try {
                    if ( !((i == xCoord) && (j == yCoord)) && (i <= dimension)
                         && (j <= dimension)
                        ) {
                        int color = map.getPixel(i, j);
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

    @Override
    protected int loadTexture() {
        Bitmap textureBmp = getTextureBitmap();

        scaleX = LAND_WIDTH / textureBmp.getWidth() * 1f;
        scaleZ = LAND_HEIGHT / textureBmp.getHeight() * 1f;

        final Paint paint = new Paint();
        paint.setPathEffect(new DashPathEffect(new float[]{10, 5}, 0));

        Canvas canvas = new Canvas(textureBmp);

        drawPath(paint, canvas);

        return loadGLTexture(textureBmp);
    }

    public PointF tex2WorldCoord(PointF texPoint) {
        PointF result = new PointF();
        result.x = texPoint.x * scaleX  - (LAND_WIDTH / 2);
        result.y = texPoint.y * scaleZ  - (LAND_HEIGHT / 2);

        return result;
    }

    private void drawPath(Paint paint, Canvas canvas) {
        if (gameEntity != null) {
            Path path = new Path();
            if (gameEntity.getGamePoints() != null && gameEntity.getGamePoints().size() > 0) {
                AbstractGamePoint point = gameEntity.getGamePoints().get(0);
                path.moveTo(point.getxPos(), point.getyPos());

                for (int i = 1; i < gameEntity.getGamePoints().size(); i++) {
                    AbstractGamePoint endPoint = gameEntity.getGamePoints().get(i);
                    path.lineTo(endPoint.getxPos(), endPoint.getyPos());
                }
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                canvas.drawPath(path, paint);

                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.FILL);
                for (int i = 0; i < gameEntity.getGamePoints().size(); i++) {
                    AbstractGamePoint endPoint = gameEntity.getGamePoints().get(i);
                    canvas.drawCircle(endPoint.getxPos(), endPoint.getyPos(), 10f, paint);
                }
            }
        }
    }

}
