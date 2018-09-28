package com.sadgames.sysutils.common;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.GameMapController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class CommonUtils {

    public static String convertStreamToString(java.io.InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void forceGCandWait() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<>(obj);
        obj = null;

        System.gc();
        System.runFinalization();
        /** wait for garbage collector finished*/
        while(ref.get() != null)
            try {Thread.sleep(100);} catch (InterruptedException e) {} //System.gc();
    }

    public static void downloadBitmapIfNotCached(SysUtilsWrapperInterface sysUtilsWrapper,
                                          String textureResName,
                                          boolean isRelief) {
        GameMapController gmc = new GameMapController(sysUtilsWrapper);

        GameMapEntity map = (GameMapEntity) gmc.find(textureResName);
        if (map == null || map.getId() == null || map.getId().isEmpty())
            return;

        try {
            if (isRelief)
                gmc.saveMapRelief(map);
            else
                gmc.saveMapImage(map);
        } catch (Exception e) {}
    }

    public static BitmapWrapperInterface getBitmapFromFile(SysUtilsWrapperInterface sysUtilsWrapper,
                                                           String file,
                                                           boolean isRelief) {
        BitmapWrapperInterface result;

        try {
            InputStream source = sysUtilsWrapper.iGetResourceStream("textures/" + file);

            if (file.endsWith("pkm"))
                result = sysUtilsWrapper.iCreateETC1Texture(source);
            else {
                result = sysUtilsWrapper.iCreateBitmap(source);
            }
        }
        catch (Exception exception) { result = null; }

        result = result != null ? result : sysUtilsWrapper.iLoadBitmapFromDB(file, isRelief);

        if (result == null)
            try {
                result = sysUtilsWrapper.iCreateColorBitmap(Integer.parseInt(file));
            }
            catch (Exception exception) { result = null; }

        return result;
    }

    public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        /** Raw height and width of image*/
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

    public static BitmapWrapperInterface packToETC1(SysUtilsWrapperInterface sysUtilsWrapper,
                                                    BitmapWrapperInterface bitmap) {
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

        BitmapWrapperInterface texture = sysUtilsWrapper.iCompressTexture(bb, width, height, 3, 3 * width);

        bb.limit(0);

        return texture;
    }
}