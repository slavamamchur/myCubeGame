package com.cubegames.slava.cubegame;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static com.cubegames.slava.cubegame.Utils.ColorType.BLUE;
import static com.cubegames.slava.cubegame.Utils.ColorType.BROWN;
import static com.cubegames.slava.cubegame.Utils.ColorType.CYAN;
import static com.cubegames.slava.cubegame.Utils.ColorType.GREEN;
import static com.cubegames.slava.cubegame.Utils.ColorType.YELLOW;

public class Utils {

    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }

    /** Array utils ------------------------------------------------------------------------------*/
    public static short chain(int j, int i, int imax){
        return (short) (i+j*(imax+1));
    }
    public static int coord2idx(int i, int j, int i_max, int el_size){
        return j * (i_max + 1) * el_size + i * el_size;
    }
    /** ------------------------------------------------------------------------------------------*/

    /** Resource utils ---------------------------------------------------------------------------*/
    public static String readTextFromRaw(Context context, int resourceId) {
        return convertStreamToString(context.getResources().openRawResource(resourceId));
    }

    public static String convertStreamToString(java.io.InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    /** ------------------------------------------------------------------------------------------*/

    /** Texture utils ----------------------------------------------------------------------------*/
    public enum ColorType {
        CYAN,
        BLUE,
        GREEN,
        YELLOW,
        BROWN,
        WHITE,
        UNKNOWN
    }

    public static final float MIN_CYAN = 130f + 160f + 232f;
    public static final float MIN_BLUE = 13f + 30f + 70f;
    public static final float MIN_GREEN = 0f + 56f + 17f;
    public static final float MIN_YELLOW = 145f + 133f + 30f;
    public static final float MIN_BROWN = 83f + 17f + 0f;
    public static final float MIN_WHITE = 127f + 127f + 127f;

    public static final float MAX_CYAN = 220f + 245f + 245f;
    public static final float MAX_BLUE = 129f + 159f + 231f;
    public static final float MAX_GREEN = 85f + 255f + 164f;//TODO:
    public static final float MAX_YELLOW = 246f + 246f + 162f;
    public static final float MAX_BROWN = 192f + 135f + 58f;
    public static final float MAX_WHITE = 255f + 255f + 255f;

    public static final float[] MIN_HEIGHT_VALUES = {0.05f, 0.55f, 0.0f, 0.21f, 1.21f, 2.55f};
    public static final float[] MAX_HEIGHT_VALUES = {0.5f, 10f, 0.2f, 1.2f, 2.5f, 10f};
    public static final float[] DELTA_COLOR_VALUES = {MAX_CYAN - MIN_CYAN,
                                                      MAX_BLUE - MIN_BLUE,
                                                      MAX_GREEN - MIN_GREEN,
                                                      MAX_YELLOW - MIN_YELLOW,
                                                      MAX_BROWN - MIN_BROWN,
                                                      MAX_WHITE - MIN_WHITE};

    public static final float[] MIN_COLOR_VALUES = {MIN_CYAN,
                                                    MIN_BLUE,
                                                    MIN_GREEN,
                                                    MIN_YELLOW,
                                                    MIN_BROWN,
                                                    MIN_WHITE};

    public static final boolean[] INVERT_LIGHT_FACTOR = {true, true, false, true, true, false};

    public static int loadGLTexture(Bitmap bitmap) {
        /** создание объекта текстуры*/
        final int[] textureIds = new int[1];
        /**создаем пустой массив из одного элемента
        //в этот массив OpenGL ES запишет свободный номер текстуры,
        // получаем свободное имя текстуры, которое будет записано в names[0]*/
        glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return 0;
        }

        if (bitmap == null) {
            glDeleteTextures(1, textureIds, 0);
            return 0;
        }

        /** glActiveTexture — select active texture unit*/
        glActiveTexture(GL_TEXTURE0);
        /** делаем текстуру с именем textureIds[0] текущей*/
        glBindTexture(GL_TEXTURE_2D, textureIds[0]);
        /** учитываем прозрачность текстуры*/
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        /** включаем фильтры*/
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        /** переписываем Bitmap в память видеокарты*/
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        /** удаляем Bitmap из памяти, т.к. картинка уже переписана в видеопамять*/
        bitmap.recycle();
        /** сброс приязки объекта текстуры к блоку текстуры*/
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureIds[0];
    }

    public static ColorType CheckColorType(Integer color){
        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);

        if ((G <= B) && (R < G))
            return G <= 0.5 * B ? BLUE : CYAN; //B <= 231 ? BLUE : CYAN;//
        else if ((R < G) && (B < G))
            return GREEN;
        else if ((G < R) && (B < G))
            return G <= 0.7 * R ? BROWN : YELLOW;
        /*else if ((G == R) && (B == G) && (R >= 180))
            return WHITE;*/
        else
            return CYAN; //TODO: UNKNOWN;
    }

    public static int[] getRowPixels(Bitmap bmp, int[] rowPixels, float dTy) {
        bmp.getPixels(rowPixels, 0, bmp.getWidth(), 0, Math.round((bmp.getHeight() - 1) * dTy), bmp.getWidth(), 1);

        return rowPixels;
    }

    /** ------------------------------------------------------------------------------------------*/

    /** DB utils ---------------------------------------------------------------------------------*/
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        /** Raw height and width of image*/
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            /** Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.*/
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void saveBitmap2DB(Context context, byte[] bitmapArray, String map_id, Long updatedDate) throws IOException {
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(context, SQLiteDBHelper.DB_NAME, null, SQLiteDBHelper.DB_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(SQLiteDBHelper.MAP_ID_FIELD, map_id);
        cv.put(SQLiteDBHelper.MAP_UPDATED_DATE, updatedDate);
        cv.put(SQLiteDBHelper.MAP_IMAGE_FIELD, bitmapArray);

        db.replaceOrThrow(SQLiteDBHelper.TABLE_NAME, null, cv);
        db.close();

        dbHelper.close();
    }

    public static Bitmap loadBitmapFromDB(Context context, String map_id) {
        Bitmap bitmap = null;
        Cursor imageData = null;
        SQLiteDBHelper dbHelper = null;
        SQLiteDatabase db = null;

        try {
            dbHelper = new SQLiteDBHelper(context, SQLiteDBHelper.DB_NAME, null, SQLiteDBHelper.DB_VERSION);
            db = dbHelper.getReadableDatabase();
            byte[] bitmapArray = null;

            imageData = db.rawQuery("select " + SQLiteDBHelper.MAP_IMAGE_FIELD +
                            " from " + SQLiteDBHelper.TABLE_NAME +
                            " where " + SQLiteDBHelper.MAP_ID_FIELD + " = ?",
                    new String[] { map_id });

            if (imageData != null && imageData.moveToFirst())
                bitmapArray = imageData.getBlob(imageData.getColumnIndex(SQLiteDBHelper.MAP_IMAGE_FIELD));

            imageData.close();
            db.close();
            dbHelper.close();

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inScaled = false;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);
            options.inSampleSize = calculateInSampleSize(options, options.outWidth / 2, options.outHeight / 2);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);

        }
        finally {
            if (imageData != null) imageData.close();
            if (db != null) db.close();
            if (dbHelper != null) dbHelper.close();
        }

        return bitmap;
    }

    public static boolean isBitmapCached(Context context, String map_id, Long updatedDate) {
        Cursor imageData = null;
        SQLiteDBHelper dbHelper = null;
        SQLiteDatabase db = null;
        boolean result = false;

        try {
            dbHelper = new SQLiteDBHelper(context, SQLiteDBHelper.DB_NAME, null, SQLiteDBHelper.DB_VERSION);
            db = dbHelper.getReadableDatabase();

            imageData = db.rawQuery("select " + SQLiteDBHelper.MAP_ID_FIELD +
                            " from " + SQLiteDBHelper.TABLE_NAME +
                            " where " + SQLiteDBHelper.MAP_ID_FIELD + " = ?" +
                            " and " + SQLiteDBHelper.MAP_UPDATED_DATE + " >= ?",
                    new String[] { map_id, String.valueOf(updatedDate) });

            result = imageData != null && imageData.moveToFirst();

            imageData.close();
            db.close();
            dbHelper.close();

        }
        finally {
            if (imageData != null) imageData.close();
            if (db != null) db.close();
            if (dbHelper != null) dbHelper.close();
        }

        return result;
    }
    /** ------------------------------------------------------------------------------------------*/

}
