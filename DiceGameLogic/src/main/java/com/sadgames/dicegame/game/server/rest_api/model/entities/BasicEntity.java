package com.sadgames.dicegame.game.server.rest_api.model.entities;

import android.os.Parcel;

public abstract class BasicEntity{

    protected abstract void save2Parcel(Parcel dest);
    protected abstract void loadFromParcel(Parcel in);
}
