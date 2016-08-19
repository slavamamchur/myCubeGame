package com.cubegames.slava.cubegame.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.cubegames.slava.cubegame.model.players.InstancePlayer;

import java.util.List;

public class StartNewGameRequest implements Parcelable{

    private String gameId;
    private String name;
    private List<InstancePlayer> players;
    private List<String> playersId;

    protected StartNewGameRequest(Parcel in) {
        gameId = in.readString();
        name = in.readString();
        players = in.createTypedArrayList(InstancePlayer.CREATOR);
        playersId = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gameId);
        dest.writeString(name);
        dest.writeTypedList(players);
        dest.writeStringList(playersId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<StartNewGameRequest> CREATOR = new Creator<StartNewGameRequest>() {
        @Override
        public StartNewGameRequest createFromParcel(Parcel in) {
            return new StartNewGameRequest(in);
        }

        @Override
        public StartNewGameRequest[] newArray(int size) {
            return new StartNewGameRequest[size];
        }
    };

    public List<InstancePlayer> getPlayers() {
        return players;
    }
    public void setPlayers(List<InstancePlayer> players) {
        this.players = players;
    }

    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPlayersId() {
        return playersId;
    }
    public void setPlayersId(List<String> playersId) {
        this.playersId = playersId;
    }
}
