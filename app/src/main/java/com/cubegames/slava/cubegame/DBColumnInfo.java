package com.cubegames.slava.cubegame;

public class DBColumnInfo {

    private String caption;
    private float weight;
    private ColumnType type;
    private String fieldName;
    private String TAG;

    public DBColumnInfo(String caption, float weight, ColumnType type, String fieldName, String tag) {
        this.caption = caption;
        this.weight = weight;
        this.type = type;
        this.fieldName = fieldName;
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
    public String getFieldName() {
        return fieldName;
    }
    public String getTAG() {
        return TAG;
    }

    public enum ColumnType { COLUMN_TEXT, COLUMN_REFERENCE, COLUMN_BUTTON, COLUMN_COLOR_BOX }
}
