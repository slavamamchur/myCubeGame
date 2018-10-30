package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.points;

import com.cubegames.engine.domain.entities.points.PointType;
import com.cubegames.engine.domain.rest.requests.NewPointRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst;

import java.io.Serializable;

import javax.vecmath.Vector2f;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractGamePoint implements Serializable {

    private static final long serialVersionUID = 593713937939910727L;

    public int xPos;
    public int yPos;
    public PointType type;
    public int nextPointIndex;

    public AbstractGamePoint() {}

    public AbstractGamePoint(NewPointRequest other) {
        this.xPos = other.getxPos();
        this.yPos = other.getyPos();
        this.type = other.getType();
        this.nextPointIndex = other.getNextIndex();
    }

    public int getxPos() {
        return xPos;
    }
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public PointType getType() {
        return type;
    }
    public void setType(PointType type) {
        this.type = type;
    }

    public int getNextPointIndex() {
        return nextPointIndex;
    }
    public void setNextPointIndex(int nextPointIndex) {
        this.nextPointIndex = nextPointIndex;
    }

    public Vector2f asVector2f() {
        return new Vector2f(xPos, yPos);
    }
    public Vector2f asVector2fLua(float scaleFactor) {
        return new Vector2f(xPos * scaleFactor, yPos * scaleFactor);
    }

    public static String urlForActionName(){
        return RestConst.URL_GAME_POINT;
    }
}
