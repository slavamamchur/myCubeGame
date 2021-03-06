package com.sadgames.sysutils.platforms.android;

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
import android.opengl.ETC1Util;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sadgames.gl3dengine.glrender.BitmapWrapperInterface;
import com.sadgames.sysutils.common.SettingsManagerInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.vecmath.Vector3f;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;
import static com.sadgames.sysutils.common.CommonUtils.convertStreamToString;
import static com.sadgames.sysutils.common.SysUtilsConsts.BYTES_IN_MB;

public abstract class AndroidSysUtilsWrapper implements SysUtilsWrapperInterface {

    protected static final Object lockObject = new Object();

    protected static MediaPlayer mMediaPlayer;
    protected static SysUtilsWrapperInterface instance = null;

    protected Context context;

    @SuppressWarnings("all")
    protected AndroidSysUtilsWrapper(Context context) {
        this.context = context;
    }

    protected abstract void downloadBitmapIfNotCached(String textureResName, boolean isRelief);

    /** Math    sysutils ---------------------------------------------------------------------------*/
    @Override
    public Vector3f mulMV(float[] matrix, float[] vector) {
        float[] result = new float[4];
        Matrix.multiplyMV(result, 0, matrix, 0, vector, 0);

        return new Vector3f(result[0], result[1], result[2]);
    }
    /** ------------------------------------------------------------------------------------------*/

    /** Prefs    sysutils ---------------------------------------------------------------------------*/
    @SuppressWarnings("all")
    public static SharedPreferences getDefaultSharedPrefs(Context ctx) {
        return getDefaultSharedPreferences(ctx);
    }
    /** ------------------------------------------------------------------------------------------*/

    /** Sound    sysutils ---------------------------------------------------------------------------*/
    private static void stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void playSound(String file) {
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

    /** Resource sysutils ---------------------------------------------------------------------------*/
    private String readTextFromAssets(String filename) {
        try {
            return convertStreamToString(context.getAssets().open(filename, Context.MODE_WORLD_READABLE));
        } catch (IOException e) {
            return "";
        }
    }

    /** ------------------------------------------------------------------------------------------*/

    /** Bitmap sysutils ----------------------------------------------------------------------------*/
    @NonNull
    private static BitmapFactory.Options getiBitmapOptions() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        return options;
    }

    private AndroidBitmapWrapper getBitmapFromFile(String file, boolean isRelief) {
        AndroidBitmapWrapper result;
        Bitmap bitmap;

        try {
            InputStream source = context.getAssets().open("textures/" + file);

            if (file.endsWith("pkm")) {
                ETC1Util.ETC1Texture compressedBitmap = ETC1Util.createTexture(source);
                result = compressedBitmap == null ? null : new AndroidBitmapWrapper(compressedBitmap);
            }
            else {
                final BitmapFactory.Options options = getiBitmapOptions();
                bitmap = BitmapFactory.decodeStream(source, null, options);
                result = bitmap == null ? null : new AndroidBitmapWrapper(bitmap);
            }
        }
        catch (Exception exception) { result = null; }

        result = result != null ? result : loadBitmapFromDB(file, isRelief);

        if (result == null)
            try {
                result = new AndroidBitmapWrapper(createColorBitmap(Integer.parseInt(file)));
            }
            catch (Exception exception) { result = null; }

        return result;
    }

    @NonNull
    private Bitmap createColorBitmap(int color) {
        Bitmap bmp = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(color);

        return bmp;
    }

    @SuppressWarnings("unused")
    public int[] getRowPixels(Bitmap bmp, int[] rowPixels, int y) {
        bmp.getPixels(rowPixels, 0, bmp.getWidth(), 0, y, bmp.getWidth(), 1);

        return rowPixels;
    }

    /** ------------------------------------------------------------------------------------------*/

    /** DB sysutils ---------------------------------------------------------------------------------*/
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
        cv.put(AndroidSQLiteDBHelper.MAP_ID_FIELD, map_id);
        cv.put(AndroidSQLiteDBHelper.CHUNK_NUMBER_FIELD, chunkNumber);
        cv.put(AndroidSQLiteDBHelper.MAP_UPDATED_DATE, updatedDate);
        cv.put(AndroidSQLiteDBHelper.MAP_IMAGE_FIELD, chunkData);

        db.replaceOrThrow(AndroidSQLiteDBHelper.TABLE_NAME, null, cv);
    }

    private AndroidBitmapWrapper decodeImage(byte[] bitmapArray) {
        int scaleFactor = TEXTURE_RESOLUTION_SCALE[iGetSettingsManager().getGraphicsQualityLevel().ordinal()];
        final BitmapFactory.Options options = getiBitmapOptions();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);
        options.inSampleSize =
                calculateInSampleSize(options,
                        options.outWidth / scaleFactor,
                        options.outHeight / scaleFactor);
        options.inJustDecodeBounds = false;
        return new AndroidBitmapWrapper(BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options));
    }

    public void saveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException {
        AndroidSQLiteDBHelper dbHelper = new AndroidSQLiteDBHelper(context, AndroidSQLiteDBHelper.DB_NAME, null, AndroidSQLiteDBHelper.DB_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("delete from " + AndroidSQLiteDBHelper.TABLE_NAME + " where " + AndroidSQLiteDBHelper.MAP_ID_FIELD + " = \"" + map_id + "\"");

        int chunkCount = bitmapArray.length / (BYTES_IN_MB * 2);
        final int lastChunkSize = bitmapArray.length % (BYTES_IN_MB * 2);
        chunkCount = lastChunkSize > 0 ? chunkCount + 1 : chunkCount;

        for (int i = 0; i < chunkCount; i++) {
            int chunkSize = (i == (chunkCount - 1)) && (lastChunkSize > 0) ? lastChunkSize : (BYTES_IN_MB * 2);
            byte[] chunkData = Arrays.copyOfRange(bitmapArray, i * chunkSize, (i + 1) * chunkSize);

            saveChunk2DB(db, map_id, i, updatedDate, chunkData);
        }

        db.close();
        dbHelper.close();
    }

    @Nullable
    private AndroidBitmapWrapper loadBitmapFromDB(String textureResName, boolean isRelief) {
        AndroidBitmapWrapper bitmap = null;
        byte[] bitmapArray = null;
        Cursor imageData = null;
        AndroidSQLiteDBHelper dbHelper = null;
        SQLiteDatabase db = null;

        try {
            downloadBitmapIfNotCached(textureResName, isRelief);

            dbHelper = new AndroidSQLiteDBHelper(context, AndroidSQLiteDBHelper.DB_NAME, null, AndroidSQLiteDBHelper.DB_VERSION);
            db = dbHelper.getReadableDatabase();

            imageData = db.rawQuery("select " + AndroidSQLiteDBHelper.MAP_IMAGE_FIELD +
                            " from " + AndroidSQLiteDBHelper.TABLE_NAME +
                            " where " + AndroidSQLiteDBHelper.MAP_ID_FIELD + " = ?" +
                            " order by " + AndroidSQLiteDBHelper.CHUNK_NUMBER_FIELD,
                    new String[] { (isRelief ? "rel_" : "") + textureResName });

            if (imageData != null && imageData.moveToFirst()) {
                int dataPtr = 0;
                int chunkCount = imageData.getCount();
                int imageSize = chunkCount * (BYTES_IN_MB * 2);

                imageData.moveToLast();
                byte[] lastChunk = imageData.getBlob(imageData.getColumnIndex(AndroidSQLiteDBHelper.MAP_IMAGE_FIELD));
                imageSize = lastChunk.length < (BYTES_IN_MB * 2) ? imageSize - (BYTES_IN_MB * 2) + lastChunk.length : imageSize;
                bitmapArray = new byte[imageSize];
                imageData.moveToFirst();

                do {
                    byte[] chunkData = imageData.getBlob(imageData.getColumnIndex(AndroidSQLiteDBHelper.MAP_IMAGE_FIELD));
                    System.arraycopy(chunkData, 0, bitmapArray, dataPtr, chunkData.length);

                    dataPtr += chunkData.length;
                } while (imageData.moveToNext());
            }

            imageData.close();
            db.close();
            dbHelper.close();

            bitmap = bitmapArray != null ? decodeImage(bitmapArray) : isRelief ? new AndroidBitmapWrapper((Bitmap) null) : null;
        }
        finally {
            if (imageData != null) imageData.close();
            if (db != null) db.close();
            if (dbHelper != null) dbHelper.close();
        }

        return bitmap;
    }

    private boolean isBitmapCached(String map_id, Long updatedDate) {
        Cursor imageData = null;
        AndroidSQLiteDBHelper dbHelper = null;
        SQLiteDatabase db = null;
        boolean result = false;

        try {
            dbHelper = new AndroidSQLiteDBHelper(context, AndroidSQLiteDBHelper.DB_NAME, null, AndroidSQLiteDBHelper.DB_VERSION);
            db = dbHelper.getReadableDatabase();

            imageData = db.rawQuery("select count(" + AndroidSQLiteDBHelper.MAP_ID_FIELD + ") as CNT" +
                            " from " + AndroidSQLiteDBHelper.TABLE_NAME +
                            " where " + AndroidSQLiteDBHelper.MAP_ID_FIELD + " = ?"
                            + " and " + AndroidSQLiteDBHelper.MAP_UPDATED_DATE + " = ?",
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
    public BitmapWrapperInterface packToETC1(BitmapWrapperInterface bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ByteBuffer bb = ByteBuffer.allocateDirect(width * height * 3);

        for (int y = 0; y < height; y += 1) {
            for (int x = 0; x < width; x += 1) {
                int value = bitmap.getPixelColor(x, y);
                bb.put((byte) (value >> 16));
                bb.put((byte) (value >> 8));
                bb.put((byte) value);
            }
        }
        bb.rewind();

        bitmap.release();
        bitmap = null;

        ETC1Util.ETC1Texture texture = ETC1Util.compressTexture(bb, width, height, 3, 3 * width);

        bb.limit(0);

        return new AndroidBitmapWrapper(texture);
    }

    @Override
    public String iReadTextFromFile(String fileName) {
        return readTextFromAssets(fileName);
    }

    @Override
    public BitmapWrapperInterface iGetBitmapFromFile(String file) {
        return getBitmapFromFile(file, false);
    }

    @Override
    public BitmapWrapperInterface iGetReliefFromFile(String file) {
        return getBitmapFromFile(file, true);
    }

    @Override
    public BitmapWrapperInterface iCreateColorBitmap(int color) {
        return new AndroidBitmapWrapper(createColorBitmap(color));
    }

    @Override
    public boolean iIsBitmapCached(String map_id, Long updatedDate) {
        return isBitmapCached(map_id, updatedDate);
    }

    @Override
    public void iSaveBitmap2DB(byte[] bitmapArray, String map_id, Long updatedDate) throws IOException {
        saveBitmap2DB(bitmapArray, map_id, updatedDate);
    }

    @Override
    public void iPlaySound(String file) {
        playSound(file);
    }

    @Override
    public void iStopSound() {
        stopSound();
    }

    @Override
    public SettingsManagerInterface iGetSettingsManager() {
        return AndroidSettingsManager.getInstance(context);
    }

}
