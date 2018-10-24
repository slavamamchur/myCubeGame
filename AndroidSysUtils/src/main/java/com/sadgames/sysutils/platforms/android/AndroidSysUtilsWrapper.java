package com.sadgames.sysutils.platforms.android;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.sadgames.sysutils.common.BitmapWrapperInterface;
import com.sadgames.sysutils.common.CommonUtils;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXTURE_RESOLUTION_SCALE;
import static com.sadgames.sysutils.common.CommonUtils.getSettingsManager;

public class AndroidSysUtilsWrapper {

    protected Context context;

    protected AndroidSysUtilsWrapper(Context context) {
        this.context = context;
    }

    /** Bitmap sysutils ----------------------------------------------------------------------------*/
    @NonNull
    private static BitmapFactory.Options getiBitmapOptions() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inScaled = false;
        return options;
    }

    private BitmapWrapperInterface decodeImage(byte[] bitmapArray) {
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

            return null;
        }
        else
            return null;
    }
    /** ------------------------------------------------------------------------------------------*/

}
