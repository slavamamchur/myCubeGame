package com.cubegames.engine.domain.entities.points;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract game point
 */

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "_class")
@JsonSubTypes({
    @JsonSubTypes.Type(value = GamePointFinish.class, name = "com.cubegames.engine.domain.entities.points.GamePointFinish"),
    @JsonSubTypes.Type(value = GamePointStart.class, name = "com.cubegames.engine.domain.entities.points.GamePointStart"),
    @JsonSubTypes.Type(value = GamePointFlyBack.class, name = "com.cubegames.engine.domain.entities.points.GamePointFlyBack"),
    @JsonSubTypes.Type(value = GamePointFlyForward.class, name = "com.cubegames.engine.domain.entities.points.GamePointFlyForward"),
    @JsonSubTypes.Type(value = GamePointMoveMore.class, name = "com.cubegames.engine.domain.entities.points.GamePointMoveMore"),
    @JsonSubTypes.Type(value = GamePointMoveSkip.class, name = "com.cubegames.engine.domain.entities.points.GamePointMoveSkip"),
    @JsonSubTypes.Type(value = GamePointRegular.class, name = "com.cubegames.engine.domain.entities.points.GamePointRegular")
})
public abstract class AbstractGamePoint {

  protected int xPos;
  protected int yPos;
  protected PointType type;
  protected int nextPointIndex;

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

}
