package com.sadgames.dicegame.game_logic.items;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.sadgames.dicegame.rest_api.model.Game;
import com.sadgames.dicegame.rest_api.model.points.AbstractGamePoint;
import com.sadgames.gl3d_engine.gl_render.scene.objects.TopographicMapObject;
import com.sadgames.gl3d_engine.gl_render.scene.objects.materials.textures.BitmapTexture;
import com.sadgames.gl3d_engine.gl_render.scene.shaders.GLShaderProgram;
import com.sadgames.gl3d_engine.utils.ISysUtilsWrapper;

public class GameMapObject extends TopographicMapObject {

    private Game gameEntity;

    public GameMapObject(ISysUtilsWrapper sysUtilsWrapper, GLShaderProgram program, Game gameEntity) {
        super(sysUtilsWrapper, program, gameEntity == null ? null : gameEntity.getMapId());

        this.gameEntity = gameEntity;
    }

    public Game getGameEntity() {
        return gameEntity;
    }

    @Override
    protected Bitmap getReliefMap() {
        return textureResName != null ? getSysUtilsWrapper().iLoadReliefFromDB(textureResName) : null;
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

        return BitmapTexture.createInstance(textureBmp).getTextureId();
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
