package com.sadgames.sysutils;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.sadgames.dicegame.rest_api.GameMapController;
import com.sadgames.dicegame.rest_api.model.GameMap;
import com.sadgames.gl3d_engine.utils.ISettingsManager;
import com.sadgames.gl3d_engine.utils.ISysUtilsWrapper;

import java.io.IOException;
import java.util.Arrays;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.sadgames.gl3d_engine.utils.JavaPlatformUtils.convertStreamToString;
import static com.sadgames.sysutils.AndroidSQLiteDBHelper.CHUNK_NUMBER_FIELD;
import static com.sadgames.sysutils.AndroidSQLiteDBHelper.MAP_ID_FIELD;
import static com.sadgames.sysutils.AndroidSQLiteDBHelper.MAP_IMAGE_FIELD;
import static com.sadgames.sysutils.AndroidSQLiteDBHelper.MAP_UPDATED_DATE;
import static com.sadgames.sysutils.AndroidSQLiteDBHelper.TABLE_NAME;

public class AndroidSysUtilsWrapper implements ISysUtilsWrapper {

    public static  final int BYTES_IN_2MB = 2 * 1024 * 1024;
    private static MediaPlayer mMediaPlayer;

    private Context context;

    public AndroidSysUtilsWrapper(Context context) {
        this.context = context;
    }

    /** Prefs    utils ---------------------------------------------------------------------------*/
    public static SharedPreferences getDefaultSharedPrefs(Context ctx) {
        return getDefaultSharedPreferences(ctx);
    }
    /** ------------------------------------------------------------------------------------------*/

    /** Sound    utils ---------------------------------------------------------------------------*/
    public static void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static void playSound(Context c, int rid) {
        stopSound();

        mMediaPlayer = MediaPlayer.create(c, rid);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopSound();
            }
        });

        mMediaPlayer.start();
    }

    public static void playSound(Context context, String file) {
        AssetFileDescriptor afd = null;
        stopSound();

        try {
            afd = context.getAssets().openFd(file);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength()
            );
            afd.close();

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSound();
                }
            });
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /** ------------------------------------------------------------------------------------------*/

    /** Resource utils ---------------------------------------------------------------------------*/
    public static String readTextFromAssets(Context context, String filename) {
        try {
            return convertStreamToString(context.getAssets().open(filename, Context.MODE_WORLD_READABLE));
        } catch (IOException e) {
            return "";
        }
    }

    /** ------------------------------------------------------------------------------------------*/

    /** Bitmap utils ----------------------------------------------------------------------------*/
    @NonNull
    private static BitmapFactory.Options getiBitmapOptions() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        return options;
    }

    public static Bitmap getBitmapFromFile(Context context, String file) {
        Bitmap result;

        try {
            final BitmapFactory.Options options = getiBitmapOptions();
            result = BitmapFactory.decodeStream(context.getAssets().open("textures/" + file), null, options);
        }
        catch (Exception exception) { result = null; }

        return result;
    }

    @NonNull
    public static Bitmap createColorBitmap(int color) {
        Bitmap bmp = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(color);

        return bmp;
    }

    public static int[] getRowPixels(Bitmap bmp, int[] rowPixels, int y) {
        bmp.getPixels(rowPixels, 0, bmp.getWidth(), 0, y, bmp.getWidth(), 1);

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

    private static void saveChunk2DB(SQLiteDatabase db, String map_id, int chunkNumber, Long updatedDate, byte[] chunkData) {
        ContentValues cv = new ContentValues();
        cv.put(MAP_ID_FIELD, map_id);
        cv.put(CHUNK_NUMBER_FIELD, chunkNumber);
        cv.put(MAP_UPDATED_DATE, updatedDate);
        cv.put(MAP_IMAGE_FIELD, chunkData);

        db.replaceOrThrow(TABLE_NAME, null, cv);
    }

    public static void saveBitmap2DB(Context context, byte[] bitmapArray, String map_id, Long updatedDate) throws IOException {
        AndroidSQLiteDBHelper dbHelper = new AndroidSQLiteDBHelper(context, AndroidSQLiteDBHelper.DB_NAME, null, AndroidSQLiteDBHelper.DB_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("delete from " + TABLE_NAME + " where " + MAP_ID_FIELD + " = \"" + map_id + "\"");

        int chunkCount = bitmapArray.length / BYTES_IN_2MB;
        final int lastChunkSize = bitmapArray.length % BYTES_IN_2MB;
        chunkCount = lastChunkSize > 0 ? chunkCount + 1 : chunkCount;

        for (int i = 0; i < chunkCount; i++) {
            int chunkSize = (i == (chunkCount - 1)) && (lastChunkSize > 0) ? lastChunkSize : BYTES_IN_2MB;
            byte[] chunkData = Arrays.copyOfRange(bitmapArray, i * chunkSize, (i + 1) * chunkSize);

            saveChunk2DB(db, map_id, i, updatedDate, chunkData);
        }

        db.close();
        dbHelper.close();
    }

    private static void downloadBitmapIfNotCached(ISysUtilsWrapper wrapper, String textureResName, boolean isRelief) {
        GameMapController gmc = new GameMapController(wrapper);
        GameMap map = gmc.find(textureResName);
        if (isRelief)
            gmc.saveMapRelief(map);
        else
            gmc.saveMapImage(map);
    }

    public static Bitmap loadBitmapFromDB(ISysUtilsWrapper wrapper, Context context, String textureResName, boolean isRelief) {
        Bitmap bitmap = null;
        byte[] bitmapArray = null;
        Cursor imageData = null;
        AndroidSQLiteDBHelper dbHelper = null;
        SQLiteDatabase db = null;

        try {
            downloadBitmapIfNotCached(wrapper, textureResName, isRelief);

            dbHelper = new AndroidSQLiteDBHelper(context, AndroidSQLiteDBHelper.DB_NAME, null, AndroidSQLiteDBHelper.DB_VERSION);
            db = dbHelper.getReadableDatabase();

            imageData = db.rawQuery("select " + MAP_IMAGE_FIELD +
                            " from " + TABLE_NAME +
                            " where " + MAP_ID_FIELD + " = ?" +
                            " order by " + CHUNK_NUMBER_FIELD,
                    new String[] { (isRelief ? "rel_" : "") + textureResName });

            if (imageData != null && imageData.moveToFirst()) {
                int dataPtr = 0;
                int chunkCount = imageData.getCount();
                int imageSize = chunkCount * BYTES_IN_2MB;

                imageData.moveToLast();
                byte[] lastChunk = imageData.getBlob(imageData.getColumnIndex(MAP_IMAGE_FIELD));
                imageSize = lastChunk.length < BYTES_IN_2MB ? imageSize - BYTES_IN_2MB + lastChunk.length : imageSize;
                bitmapArray = new byte[imageSize];
                lastChunk = null;
                imageData.moveToFirst();

                do {
                    byte[] chunkData = imageData.getBlob(imageData.getColumnIndex(MAP_IMAGE_FIELD));
                    System.arraycopy(chunkData, 0, bitmapArray, dataPtr, chunkData.length);

                    dataPtr += chunkData.length;
                } while (imageData.moveToNext());
            }

            imageData.close();
            db.close();
            dbHelper.close();

            if (bitmapArray != null) {
                final BitmapFactory.Options options = getiBitmapOptions();
                //TODO: LOD settings
                /*options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);
                options.inSampleSize = calculateInSampleSize(options, options.outWidth / 2, options.outHeight / 2);*/
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);
            }

        }
        finally {
            bitmapArray = null;

            if (imageData != null) imageData.close();
            if (db != null) db.close();
            if (dbHelper != null) dbHelper.close();
        }

        return bitmap; //TODO: return byte buffer
    }

    public static boolean isBitmapCached(Context context, String map_id, Long updatedDate) {
        Cursor imageData = null;
        AndroidSQLiteDBHelper dbHelper = null;
        SQLiteDatabase db = null;
        boolean result = false;

        try {
            dbHelper = new AndroidSQLiteDBHelper(context, AndroidSQLiteDBHelper.DB_NAME, null, AndroidSQLiteDBHelper.DB_VERSION);
            db = dbHelper.getReadableDatabase();

            imageData = db.rawQuery("select count(" + MAP_ID_FIELD + ") as CNT" +
                            " from " + TABLE_NAME +
                            " where " + MAP_ID_FIELD + " = ?"
                            + " and " + MAP_UPDATED_DATE + " = ?",
                    new String[] { map_id, String.valueOf(updatedDate) });

            result = (imageData != null) && imageData.moveToFirst() && (imageData.getInt(0) > 0);

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

    @Override
    public String iReadTextFromFile(String fileName) {
        return readTextFromAssets(context, fileName);
    }

    @Override
    public Bitmap iGetBitmapFromFile(String file) {
        return getBitmapFromFile(context, file);
    }

    @Override
    public Bitmap iCreateColorBitmap(int color) {
        return createColorBitmap(color);
    }

    @Override
    public Bitmap iLoadBitmapFromDB(String textureResName) {
        return loadBitmapFromDB(this, context, textureResName, false);
    }

    @Override
    public Bitmap iLoadReliefFromDB(String textureResName) {
        return loadBitmapFromDB(this, context, textureResName, true);
    }

    @Override
    public boolean iIsBitmapCached(String map_id, Long updatedDate) {
        return isBitmapCached(context, map_id, updatedDate);
    }

    @Override
    public void iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException {
        saveBitmap2DB(context, bitmapArray, map_id, updatedDate);
    }

    @Override
    public void iPlaySound(String file) {
        playSound(context, file);
    }

    @Override
    public void iStopSound() {
        stopSound();
    }

    @Override
    public ISettingsManager iGetSettingsManager() {
        return AndroidSettingsManager.getInstance(context);
    }
}
