package com.sadgames.dicegame.rest_api.model.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.dicegame.rest_api.model.entities.GameInstanceEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInstanceStartedResponse {

    private GameInstanceEntity instance;

    private String error;

    public GameInstanceStartedResponse() {
        // for serializer
    }


    public GameInstanceStartedResponse(GameInstanceEntity instance) {
        this.instance = instance;
    }


    public GameInstanceEntity getInstance() {
        return instance;
    }

    public void setInstance(GameInstanceEntity instance) {
        this.instance = instance;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

