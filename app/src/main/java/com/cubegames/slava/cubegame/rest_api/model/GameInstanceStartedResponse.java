package com.cubegames.slava.cubegame.rest_api.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInstanceStartedResponse {

    private GameInstance instance;

    private String error;

    public GameInstanceStartedResponse() {
        // for serializer
    }


    public GameInstanceStartedResponse(GameInstance instance) {
        this.instance = instance;
    }


    public GameInstance getInstance() {
        return instance;
    }

    public void setInstance(GameInstance instance) {
        this.instance = instance;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

