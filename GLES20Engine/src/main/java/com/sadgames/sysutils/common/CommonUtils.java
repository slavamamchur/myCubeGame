package com.sadgames.sysutils.common;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ETC1;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.GameMapController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.gl3dengine.glrender.GdxExt;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.util.Scanner;

import static com.sadgames.sysutils.common.DBUtils.loadBitmapFromDB;

public class CommonUtils {

    public static String convertStreamToString(java.io.InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String readTextFromFile(String fileName) {
        try {
            return GdxExt.files == null ? "" : GdxExt.files.internal(fileName).readString();
        } catch (Exception e) {
            return "";
        }
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

    public static SettingsManagerInterface getSettingsManager() {
        return GDXSettingsManager.getInstance();
    }

    public static void downloadBitmapIfNotCached(String textureResName,
                                          boolean isRelief) {
        GameMapController gmc = new GameMapController();

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

    public static InputStream getResourceStream(String fileName) {
        try {
            return GdxExt.files == null ? null : GdxExt.files.internal(fileName).read();
        } catch (Exception e) {
            return null;
        }
    }

    public static BitmapWrapperInterface getBitmapFromFile(String file, boolean isRelief) {
        BitmapWrapperInterface result;
        byte[] bitmapArray;

        try (InputStream source = getResourceStream("textures/" + file)) {

            if (file.endsWith("pkm"))
                result = new BitmapWrapper(ETC1Utils.ETC1Texture.createFromStream(source));
            else {
                bitmapArray = new byte[source.available()];
                source.read(bitmapArray);

                result = new BitmapWrapper(bitmapArray);
            }
        }
        catch (Exception exception) { result = null; }

        if (result == null)
            try {
                bitmapArray = loadBitmapFromDB(file, isRelief);
                result = bitmapArray != null ? new BitmapWrapper(bitmapArray) : null;
            }
            catch (SQLException | IOException exception) { result = null; }

        if (result == null)
            try {
                result = new BitmapWrapper(Integer.parseInt(file));
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

    private static BitmapWrapperInterface compressTexture(ByteBuffer input, int width, int height) {
        Pixmap pixmap = new GlPixmap(input, width, height, Pixmap.Format.RGB888);
        ByteBuffer compressedImage = ETC1.encodeImage(pixmap).compressedData;
        pixmap.dispose();

        return new BitmapWrapper(new ETC1Utils.ETC1Texture(width, height, compressedImage));
    }

    public static BitmapWrapperInterface packToETC1(BitmapWrapperInterface bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ByteBuffer bb = ByteBuffer.allocateDirect(width * height * 3).order(ByteOrder.nativeOrder());
        ByteBuffer rawImage = ((ByteBuffer)bitmap.getRawData());

        rawImage.rewind();
        for (int i = 0; i < height * width; i++) {
            int value = rawImage.getInt();

            bb.put((byte) (value >> 24));
            bb.put((byte) (value >> 16));
            bb.put((byte) (value >> 8));
        }

        bitmap.release(); //TODO: store Pixmap ptr and dispose it -> move code after compress
        bitmap = null;

        bb.rewind();
        BitmapWrapperInterface texture = compressTexture(bb, width, height);

        bb.limit(0);

        return texture;
    }

    public static Pixmap createPixmap(int width, int height, int fillColor, Pixmap.Format format) {
        Pixmap map = new GlPixmap(width, height, format);
        map.setColor(fillColor);
        map.fill();

        return  map;
    }
}
