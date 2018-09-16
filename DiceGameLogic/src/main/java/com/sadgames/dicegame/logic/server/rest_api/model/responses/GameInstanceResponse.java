package com.sadgames.dicegame.logic.server.rest_api.model.responses;

import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.players.InstancePlayer;

import static com.sadgames.dicegame.logic.client.GameConst.GameState;

public class GameInstanceResponse extends IdResponse {

    private int currentPlayer;
    private int stepsToGo;
    private GameState state;
    private long lastUsedDate;
    private boolean usersAreFinished;

    public GameInstanceResponse() {}

    public GameInstanceResponse(GameInstanceEntity instance) {
        super(instance.getId());
        this.currentPlayer = instance.getCurrentPlayer();
        this.stepsToGo = instance.getStepsToGo();
        this.state = instance.getState();
        this.lastUsedDate = instance.getLastUsedDate();

        this.usersAreFinished = true;
        for (InstancePlayer player : instance.getPlayers()) {
            if (!player.isFinished()) {
                this.usersAreFinished = false;
                break;
            }
        }
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

    public GameState getState() {
        return state;
    }
    public void setState(GameState state) {
        this.state = state;
    }

    public long getLastUsedDate() {
        return lastUsedDate;
    }
    public void setLastUsedDate(long lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    public boolean isUsersAreFinished() {
        return usersAreFinished;
    }
    public void setUsersAreFinished(boolean usersAreFinished) {
        this.usersAreFinished = usersAreFinished;
    }
}
