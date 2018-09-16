package com.sadgames.dicegame.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.dicegame.gamelogic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.dicegame.gamelogic.server.rest_api.controller.GameInstanceController;
import com.sadgames.dicegame.gamelogic.server.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.sysutils.common.LuaUtils;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.luaj.vm2.LuaTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sadgames.dicegame.gamelogic.client.GameConst.GameState;
import static com.sadgames.dicegame.gamelogic.server.rest_api.RestConst.URL_GAME_INSTANCE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInstanceEntity extends BasicNamedDbEntity implements Serializable {

    private static final long serialVersionUID = -8604963480687767704L;
    public static String ACTION_NAME = URL_GAME_INSTANCE;

    private GameEntity game;
    public List<InstancePlayer> players;
    protected int currentPlayer;
    protected int stepsToGo;
    public GameState state;
    public long startedDate;
    public long lastUsedDate;

    public GameInstanceEntity() {}

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
    @SuppressWarnings("unused") public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getStepsToGo() {
        return stepsToGo;
    }
    @SuppressWarnings("unused") public void setStepsToGo(int stepsToGo) {
        this.stepsToGo = stepsToGo;
    }

    public GameState getState() {
        return state;
    }
    public void setState(GameState state) {
        this.state = state;
    }
    @SuppressWarnings("unused") public void setStateLua(int state) {
        this.state = GameState.values()[state];
    }

    @SuppressWarnings("unused") public long getStartedDate() {
        return startedDate;
    }
    @SuppressWarnings("unused") public void setStartedDate(long startedDate) {
        this.startedDate = startedDate;
    }

    public long getLastUsedDate() {
        return lastUsedDate;
    }
    @SuppressWarnings("unused") public void setLastUsedDate(long lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    @Override
    public AbstractHttpRequest getController(SysUtilsWrapperInterface sysUtilsWrapper) {
        return new GameInstanceController(sysUtilsWrapper);
    }

    public List<InstancePlayer> createPlayersList() {
        return new ArrayList<>(players);
    }
    @SuppressWarnings("unused") public LuaTable createPlayersListLua() {
        return LuaUtils.javaList2LuaTable(new ArrayList<>(players));
    }
}
