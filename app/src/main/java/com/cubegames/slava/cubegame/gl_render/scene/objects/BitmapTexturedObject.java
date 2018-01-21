package com.cubegames.slava.cubegame.gl_render.scene.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.cubegames.slava.cubegame.api.GameMapController;
import com.cubegames.slava.cubegame.gl_render.GLRenderConsts.GLObjectType;
import com.cubegames.slava.cubegame.gl_render.scene.shaders.GLShaderProgram;
import com.cubegames.slava.cubegame.model.GameMap;

import org.springframework.util.StringUtils;

import static com.cubegames.slava.cubegame.Utils.loadBitmapFromDB;

public abstract class BitmapTexturedObject extends GLSceneObject {

    public BitmapTexturedObject(Context context, GLObjectType type, String mapID, GLShaderProgram program) {
        super(context, type, program);

        this.mapID = mapID;
    }

    public BitmapTexturedObject(Context context, GLObjectType type, int textureResId, GLShaderProgram program) {
        super(context, type, program);

        this.textureResId = textureResId;
    }

    public BitmapTexturedObject(Context context, GLObjectType type, GLShaderProgram program, int textureColor) {
        super(context, type, program);

        this.textureColor = textureColor;
    }

    protected abstract int getDimension(Bitmap bmp);

    @Override
    protected Bitmap getTextureBitmap() {
        if (textureResId >= 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inScaled = false;

            return BitmapFactory.decodeResource(context.getResources(), textureResId, options);
        }
        else if (StringUtils.hasText(mapID)) {
            GameMapController gmc = new GameMapController(context);
            GameMap map = gmc.find(mapID);
            gmc.saveMapImage(map);
            return loadBitmapFromDB(context, mapID);
        }
        else if (textureColor != 0) {
            Bitmap bmp = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.drawColor(textureColor);

            return bmp;
        }
        else
            return null;
    }

}
