package com.sadgames.sysutils.platforms.android;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.CommonUtils;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public class AndroidSysUtilsWrapper implements SysUtilsWrapperInterface {

    private static final Object lockObject = new Object();
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

    /** Bitmap sysutils ----------------------------------------------------------------------------*/
    @NonNull
    private static BitmapFactory.Options getiBitmapOptions() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        return options;
    }

    private BitmapWrapperInterface decodeImage(byte[] bitmapArray, boolean isRelief) {
        if (bitmapArray != null) {
            int scaleFactor = TEXTURE_RESOLUTION_SCALE[getSettingsManager().getGraphicsQualityLevel().ordinal()];
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
        else
            return isRelief ? new AndroidBitmapWrapper(null) : null;
    }
    /** ------------------------------------------------------------------------------------------*/

    @Override
    public BitmapWrapperInterface iDecodeImage(byte[] bitmapArray, boolean isRelief) {
        return decodeImage(bitmapArray, isRelief);
    }

}
