package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.players;

import java.io.Serializable;

public class InstancePlayer implements Serializable {
    private static final long serialVersionUID = -6815839058727920425L;

    public String name;
    public int color;
    protected int currentPoint;
    public boolean finished;
    public boolean skipped;

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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InstancePlayer))
            return false;
        else
            return name != null && name.equals(((InstancePlayer) obj).getName());
    }
}
