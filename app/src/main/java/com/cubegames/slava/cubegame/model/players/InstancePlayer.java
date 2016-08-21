package com.cubegames.slava.cubegame.model.players;

import android.os.Parcel;
import android.os.Parcelable;

public class InstancePlayer implements Parcelable{

    private String name;
    private int color;
    private int currentPoint;
    private boolean finished;
    private boolean skipped;

    public InstancePlayer() {}

    protected InstancePlayer(Parcel in) {
        name = in.readString();
        color = in.readInt();
        currentPoint = in.readInt();
        finished = in.readByte() != 0;
        skipped = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(color);
        dest.writeInt(currentPoint);
        dest.writeByte((byte) (finished ? 1 : 0));
        dest.writeByte((byte) (skipped ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<InstancePlayer> CREATOR = new Creator<InstancePlayer>() {
        @Override
        public InstancePlayer createFromParcel(Parcel in) {
            return new InstancePlayer(in);
        }

        @Override
        public InstancePlayer[] newArray(int size) {
            return new InstancePlayer[size];
        }
    };

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    public int getCurrentPoint() {
        return currentPoint;
    }
    public void setCurrentPoint(int currentPoint) {
        this.currentPoint = currentPoint;
    }

    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isSkipped() {
        return skipped;
    }
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }
}