package com.cubegames.slava.cubegame.model;

import android.content.Context;
import android.os.Parcel;

import com.cubegames.slava.cubegame.api.AbstractHttpRequest;
import com.cubegames.slava.cubegame.api.GameInstanceController;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import static com.cubegames.slava.cubegame.api.RestConst.URL_GAME_INSTANCE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInstance extends BasicNamedDbEntity {
    public static String ACTION_NAME = URL_GAME_INSTANCE;

    private Game game;  // exactly Game, not Game's Id
    public List<InstancePlayer> players;
    protected int currentPlayer;
    protected int stepsToGo;
    public State state;
    public long startedDate;
    public long lastUsedDate;

    public GameInstance() {}
    public GameInstance(Parcel in) {
        super(in);
    }
    public static final Creator<GameInstance> CREATOR = new Creator<GameInstance>() {
        @Override
        public GameInstance createFromParcel(Parcel in) {
            return new GameInstance(in);
        }

        @Override
        public GameInstance[] newArray(int size) {
            return new GameInstance[size];
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

        game = in.readParcelable(Game.class.getClassLoader());
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

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
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
    public AbstractHttpRequest getController(Context ctx) {
        return new GameInstanceController(ctx);
    }

    public enum State { WAIT, MOVING, FINISHED }

}
