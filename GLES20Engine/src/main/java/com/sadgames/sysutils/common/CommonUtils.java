package com.sadgames.sysutils.common;

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
}
