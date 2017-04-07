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

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static com.cubegames.slava.cubegame.Utils.ColorType.BLUE;
import static com.cubegames.slava.cubegame.Utils.ColorType.GREEN;
import static com.cubegames.slava.cubegame.Utils.ColorType.UNKNOWN;
import static com.cubegames.slava.cubegame.Utils.ColorType.WHITE;
import static com.cubegames.slava.cubegame.Utils.ColorType.YELLOW;

public class Utils {

    public enum ColorType {
        BLUE,
        GREEN,
        YELLOW,
        WHITE,
        UNKNOWN
    }

    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }

    public static ColorType CheckColorType(Integer color){
        int R = Color.red(color);
        int G = Color.green(color);
        int B = Color.blue(color);

        if ((G <= B) && (R < G))
            return BLUE; // h = 0
        else if ((R < G) && (B < G))
            return GREEN; // h from 0 to 30%
        else if ((G < R) && (B < G))
            return YELLOW; // h from 30 to 60%
        else if ((G == R) && (B == G) && (R >= 180))
            return WHITE; // h from 60 to 100%
        else
            return UNKNOWN;
    }

    public static String readTextFromRaw(Context context, int resourceId) {
        return convertStreamToString(context.getResources().openRawResource(resourceId));
    }

    public static String convertStreamToString(java.io.InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static int loadGLTexture(Bitmap bitmap) {
        // создание объекта текстуры
        final int[] textureIds = new int[1];


        //создаем пустой массив из одного элемента
        //в этот массив OpenGL ES запишет свободный номер текстуры,
        // получаем свободное имя текстуры, которое будет записано в names[0]

        glGenTextures(1, textureIds, 0);/// skip check!!!
        if (textureIds[0] == 0) {
            return 0;
        }

        /*//This flag is turned on by default and should be turned off if you need a non-scaled version of the bitmap.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        // получение Bitmap
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);*/

        if (bitmap == null) {
            glDeleteTextures(1, textureIds, 0);
            return 0;
        }

        //glActiveTexture — select active texture unit
        glActiveTexture(GL_TEXTURE0);
        //делаем текстуру с именем textureIds[0] текущей
        glBindTexture(GL_TEXTURE_2D, textureIds[0]);

        //учитываем прозрачность текстуры
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        //включаем фильтры
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //переписываем Bitmap в память видеокарты
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        // удаляем Bitmap из памяти, т.к. картинка уже переписана в видеопамять
        bitmap.recycle();

        // сброс приязки объекта текстуры к блоку текстуры
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureIds[0];
    }

    ///------DB Utils
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
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
    //----------------------------------------------------------------------------------------------

    /// ----- Shader utils
    public static int createProgram(int vertexShaderId, int fragmentShaderId) {

        final int programId = glCreateProgram();
        if (programId == 0) {
            return 0;
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;

    }

    static int createShader(Context context, int type, int shaderRawId) {
        return createShader(type, readTextFromRaw(context, shaderRawId));
    }

    static int createShader(int type, String shaderText) {
        final int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            return 0;
        }
        glShaderSource(shaderId, shaderText);
        glCompileShader(shaderId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            return 0;
        }
        return shaderId;
    }
    //----------------------------------------------------------------------------------------------

}
