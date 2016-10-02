package com.cubegames.slava.cubegame;

import java.lang.reflect.Field;

public class DBColumnInfo {

    private String caption;
    private float weight;
    private ColumnType type;
    private Field dataField;
    private String TAG;

    public DBColumnInfo(String caption, float weight, ColumnType type, Field dataField, String tag) {
        this.caption = caption;
        this.weight = weight;
        this.type = type;
        this.dataField = dataField;
        TAG = tag;
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

    public enum ColumnType { COLUMN_TEXT, COLUMN_REFERENCE, COLUMN_BUTTON, COLUMN_COLOR_BOX }
}
