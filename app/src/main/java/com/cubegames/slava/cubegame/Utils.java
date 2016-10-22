package com.cubegames.slava.cubegame;


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
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CubegameBitmapCache";
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
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
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
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
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

}
