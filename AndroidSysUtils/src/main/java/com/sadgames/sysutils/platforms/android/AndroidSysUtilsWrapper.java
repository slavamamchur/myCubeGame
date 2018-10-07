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
import android.opengl.ETC1;
import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.EntityControllerInterface;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.CommonUtils;
import com.sadgames.sysutils.common.ETC1Utils;
import com.sadgames.sysutils.common.LuaUtils;
import com.sadgames.sysutils.common.MathUtils;
import com.sadgames.sysutils.common.SettingsManagerInterface;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.luaj.vm2.LuaTable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.util.Arrays;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;
import static com.sadgames.sysutils.common.SysUtilsConsts.BYTES_IN_MB;

public class AndroidSysUtilsWrapper implements SysUtilsWrapperInterface {

    private static final Object lockObject = new Object();
    private static MediaPlayer mMediaPlayer;
    protected static SysUtilsWrapperInterface instance = null;

    protected Context context;

    protected AndroidSysUtilsWrapper(Context context) {
        this.context = context;
    }

    public static SysUtilsWrapperInterface getInstance(Context context) {
        synchronized (lockObject) {
            instance = instance != null ? instance : new AndroidSysUtilsWrapper(context);
            return instance;
        }
    }

    /** Math    sysutils ---------------------------------------------------------------------------*/
    @Override
    public Matrix4f createTransform() {
        return new Matrix4f();
    }

    @Override
    public Vector3f createVector3f(float vx, float vy, float vz) {
        return new Vector3f(vx, vy, vz);
    }

    @Override
    public Vector3f mulMV(Matrix4f matrix, LuaTable vector) {
        return mulMV(MathUtils.getOpenGlMatrix(matrix), LuaUtils.luaTable2FloatArray(vector));
    }

    @Override
    public Vector3f mulMV(float[] matrix, LuaTable vector) {
        return mulMV(matrix, LuaUtils.luaTable2FloatArray(vector));
    }

    @Override
    public Vector3f mulMV(float[] matrix, float[] vector) {
        float[] result = new float[4];
        Matrix.multiplyMV(result, 0, matrix, 0, vector, 0);

        return new Vector3f(result[0], result[1], result[2]);
    }

    @Override
    public void mulMM(float[] result, int resultOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset) {
        Matrix.multiplyMM(result, resultOffset, lhs, lhsOffset, rhs, rhsOffset);
    }

    @Override
    public void rotateM(float[] m, int mOffset, float a, float x, float y, float z) {
        Matrix.rotateM(m, mOffset, a, x, y, z);
    }

    /** ------------------------------------------------------------------------------------------*/

    /** Prefs    sysutils ---------------------------------------------------------------------------*/
    @SuppressWarnings("all")
    public static SharedPreferences getDefaultSharedPrefs(Context ctx) {
        return getDefaultSharedPreferences(ctx);
    }
    /** ------------------------------------------------------------------------------------------*/

    /** Sound    sysutils ---------------------------------------------------------------------------*/
    //TODO: Replace with OpenAL api
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

    @Override
    public InputStream iGetResourceStream(String fileName) {
        try {
            return context.getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Resource sysutils ---------------------------------------------------------------------------*/

    private String readTextFromAssets(String filename) {
        try {
            return CommonUtils.convertStreamToString(iGetResourceStream((filename)));
        } catch (Exception e) {
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

    //TODO: Load all textures from server and save compressed to DB (draw path by OGL)
    public static BitmapWrapperInterface createETC1Texture(InputStream input) throws IOException {
        int width;
        int height;
        byte[] ioBuffer = new byte[4096];
        {
            if (input.read(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE) != ETC1.ETC_PKM_HEADER_SIZE) {
                throw new IOException("Unable to read PKM file header.");
            }
            ByteBuffer headerBuffer = ByteBuffer.allocateDirect(ETC1.ETC_PKM_HEADER_SIZE)
                    .order(ByteOrder.nativeOrder());
            headerBuffer.put(ioBuffer, 0, ETC1.ETC_PKM_HEADER_SIZE).position(0);
            if (!ETC1.isValid(headerBuffer)) {
                throw new IOException("Not a PKM file.");
            }
            width = ETC1.getWidth(headerBuffer);
            height = ETC1.getHeight(headerBuffer);
        }
        int encodedSize = ETC1.getEncodedDataSize(width, height);
        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(ByteOrder.nativeOrder());
        for (int i = 0; i < encodedSize; ) {
            int chunkSize = Math.min(ioBuffer.length, encodedSize - i);
            if (input.read(ioBuffer, 0, chunkSize) != chunkSize) {
                throw new IOException("Unable to read PKM file data.");
            }
            dataBuffer.put(ioBuffer, 0, chunkSize);
            i += chunkSize;
        }
        dataBuffer.position(0);

        return new AndroidBitmapWrapper(new ETC1Utils.ETC1Texture(width, height, dataBuffer));
    }

    public static BitmapWrapperInterface compressTexture(Buffer input, int width, int height, int pixelSize, int stride){
        int encodedImageSize = ETC1.getEncodedDataSize(width, height);
        ByteBuffer compressedImage = ByteBuffer.allocateDirect(encodedImageSize).
                order(ByteOrder.nativeOrder());
        ETC1.encodeImage(input, width, height, pixelSize, stride, compressedImage);

        return new AndroidBitmapWrapper(new ETC1Utils.ETC1Texture(width, height, compressedImage));
    }

    private BitmapWrapperInterface createBitmap(InputStream source) {
        Bitmap bitmap;
        BitmapWrapperInterface result;
        final BitmapFactory.Options options = getiBitmapOptions();
        bitmap = BitmapFactory.decodeStream(source, null, options);
        result = bitmap == null ? null : new AndroidBitmapWrapper(bitmap);
        return result;
    }

    private BitmapWrapperInterface createColorBitmap(int color) {
        Bitmap bmp = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(color);

        return new AndroidBitmapWrapper(bmp);
    }

    /*public int[] getRowPixels(Bitmap bmp, int[] rowPixels, int y) {
        bmp.getPixels(rowPixels, 0, bmp.getWidth(), 0, y, bmp.getWidth(), 1);

        return rowPixels;
    }*/

    /** ------------------------------------------------------------------------------------------*/

    /** DB sysutils ---------------------------------------------------------------------------------*/

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
        options.inSampleSize = CommonUtils.calculateInSampleSize(
                        options.outWidth,
                        options.outHeight,
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

    private BitmapWrapperInterface loadBitmapFromDB(String textureResName, boolean isRelief) {
        AndroidBitmapWrapper bitmap;
        byte[] bitmapArray = null;
        Cursor imageData = null;
        AndroidSQLiteDBHelper dbHelper = null;
        SQLiteDatabase db = null;

        try {
            CommonUtils.downloadBitmapIfNotCached(this, textureResName, isRelief);

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
    public String iReadTextFromFile(String fileName) {
        return readTextFromAssets(fileName);
    }

    @Override
    public BitmapWrapperInterface iCreateColorBitmap(int color) {
        return createColorBitmap(color);
    }

    @Override
    public BitmapWrapperInterface iCreateBitmap(InputStream source) {
        return createBitmap(source);
    }

    @Override
    public BitmapWrapperInterface iCompressTexture(Buffer input, int width, int height, int pixelSize, int stride) {
        return compressTexture(input, width, height, pixelSize, stride);
    }

    @Override
    public BitmapWrapperInterface iCreateETC1Texture(InputStream input) throws IOException {
        return createETC1Texture(input);
    }

    @Override
    public Connection iGetDBConnection(String dbName) {
        return null; //TODO:
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
    public BitmapWrapperInterface iLoadBitmapFromDB(String textureResName, boolean isRelief) {
        return loadBitmapFromDB(textureResName, isRelief);
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

    @Override
    public EntityControllerInterface iGetEntityController(String action,
                                                          Class<? extends BasicEntity> entityType,
                                                          Class<? extends GenericCollectionResponse> listType,
                                                          int method) {
        return AndroidRESTControllerFabric.createInstance(this, action, entityType, listType, method);
    }
}
