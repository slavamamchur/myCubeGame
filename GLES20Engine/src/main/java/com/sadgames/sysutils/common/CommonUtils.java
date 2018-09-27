package com.sadgames.sysutils.common;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.GameMapController;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameMapEntity;

import java.lang.ref.WeakReference;
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
}
