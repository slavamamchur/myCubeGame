package com.cubegames.slava.cubegame.platforms.android.ui.colorpicker;

/**
 * Interface with callback methods for AmbilWarna dialog.
 */
public interface OnAmbilWarnaListener {
    void onCancel(AmbilWarnaDialogFragment dialogFragment);

    void onOk(AmbilWarnaDialogFragment dialogFragment, int color, String name);
}