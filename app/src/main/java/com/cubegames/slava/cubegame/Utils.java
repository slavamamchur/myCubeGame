package com.cubegames.slava.cubegame;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class Utils {

    public static String formatDateTime(long dateTime){
        return DateFormat.getDateTimeInstance().format(new Date(dateTime));
    }

    public static Bitmap loadBitmapFromFile(String fname) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CubegameBitmapCache";
        Bitmap bitmap = null;
        File f = new File(path, fname + ".png");
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
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
