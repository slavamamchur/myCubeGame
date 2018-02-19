package com.sadgames.dicegame.logic.server.rest_api.model.entities;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.dicegame.logic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameInstanceController;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import java.util.List;

import static com.sadgames.dicegame.logic.server.rest_api.RestConst.URL_GAME_INSTANCE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInstanceEntity extends BasicNamedDbEntity {
    public static String ACTION_NAME = URL_GAME_INSTANCE;

    private GameEntity game;  // exactly GameEntity, not GameEntity's Id
    public List<InstancePlayer> players;
    protected int currentPlayer;
    protected int stepsToGo;
    public State state;
    public long startedDate;
    public long lastUsedDate;

    public GameInstanceEntity() {}
    public GameInstanceEntity(Parcel in) {
        super(in);
    }
    public static final Creator<GameInstanceEntity> CREATOR = new Creator<GameInstanceEntity>() {
        @Override
        public GameInstanceEntity createFromParcel(Parcel in) {
            return new GameInstanceEntity(in);
        }

        @Override
        public GameInstanceEntity[] newArray(int size) {
            return new GameInstanceEntity[size];
        }
    };

    @Override
    protected void save2Parcel(Parcel dest) {
        super.save2Parcel(dest);

        dest.writeParcelable(game, 0);
        dest.writeTypedList(players);
        dest.writeInt(currentPlayer);
        dest.writeInt(stepsToGo);
        dest.writeInt(state.ordinal());
        dest.writeLong(startedDate);
        dest.writeLong(lastUsedDate);
    }

    @Override
    protected void loadFromParcel(Parcel in) {
        super.loadFromParcel(in);

        game = in.readParcelable(GameEntity.class.getClassLoader());
        players = in.createTypedArrayList(InstancePlayer.CREATOR);
        currentPlayer = in.readInt();
        stepsToGo = in.readInt();
        state = State.values()[in.readInt()];
        startedDate = in.readLong();
        lastUsedDate = in.readLong();
    }

    public List<InstancePlayer> getPlayers() {
        return players;
    }
    public void setPlayers(List<InstancePlayer> players) {
        this.players = players;
    }

    public GameEntity getGame() {
        return game;
    }
    public void setGame(GameEntity game) {
        this.game = game;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getStepsToGo() {
        return stepsToGo;
    }
    public void setStepsToGo(int stepsToGo) {
        this.stepsToGo = stepsToGo;
    }

    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }

    public long getStartedDate() {
        return startedDate;
    }
    public void setStartedDate(long startedDate) {
        this.startedDate = startedDate;
    }

    public long getLastUsedDate() {
        return lastUsedDate;
    }
    public void setLastUsedDate(long lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    @Override
    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper) {
        return new GameInstanceController(sysUtilsWrapper);
    }

    public enum State { WAIT, MOVING, FINISHED }

}
