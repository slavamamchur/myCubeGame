package com.sadgames.dicegame.platforms.android.ui.framework;

import java.lang.reflect.Field;

public class DBColumnInfo {

    private String caption;
    private float weight;
    private ColumnType type;
    private Field dataField;
    private String TAG;
    private int iconResId;
    private Object colorRef;

    public DBColumnInfo(String caption, float weight, ColumnType type, Field dataField, String tag) {
        this(caption, weight, type, dataField, tag, -1, null);
    }

    public DBColumnInfo(String caption, float weight, ColumnType type, Field dataField, String tag, int iconResId, Object colorRef) {
        this.caption = caption;
        this.weight = weight;
        this.type = type;
        this.dataField = dataField;
        TAG = tag;
        this.iconResId = iconResId;
        this.colorRef = colorRef;
    }

    public String getCaption() {
        return caption;
    }
    public float getWeight() {
        return weight;
    }
    public ColumnType getType() {
        return type;
    }
    public Field getDataField() {
        return dataField;
    }
    public String getTAG() {
        return TAG;
    }
    public int getIconResId() {
        return iconResId;
    }
    public Object getColorRef() {
        return colorRef;
    }

    public enum ColumnType { COLUMN_TEXT, COLUMN_REFERENCE, COLUMN_BUTTON, COLUMN_COLOR_BOX, COLUMN_CHECK_BOX }
}
