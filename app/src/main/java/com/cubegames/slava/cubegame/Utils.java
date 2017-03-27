package com.cubegames.slava.cubegame;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class Utils {

    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }

    public static Bitmap loadBitmapFromFile(String fname) {
        Bitmap bitmap = null;

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/CubegameBitmapCache";
            File f = new File(path, fname + ".png");
            FileInputStream is = new FileInputStream(f);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            options.inSampleSize = calculateInSampleSize(options, options.outWidth / 2, options.outHeight / 2);
            options.inJustDecodeBounds = false;

            is.close();
            is = new FileInputStream(f);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

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

    public static void saveBitmap2File(Bitmap bmp, String fname) throws IOException {
        String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                "/CubegameBitmapCache";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fname + ".png");
        FileOutputStream fOut = new FileOutputStream(file);

        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        fOut.flush();
        fOut.close();
    }

    public static void saveBitmap2File(byte[] bitmapArray, String fname) throws IOException {
        //TODO: Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                "/CubegameBitmapCache";
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fname + ".png");
        FileOutputStream fOut = new FileOutputStream(file);

        if (bitmapArray != null) {
            fOut.write(bitmapArray);
            fOut.flush();
            fOut.close();
        }
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

}
