package com.cubegames.slava.cubegame.model;

import android.os.Parcel;

public abstract class BasicEntity{

    protected abstract void save2Parcel(Parcel dest);
    protected abstract void loadFromParcel(Parcel in);
}
