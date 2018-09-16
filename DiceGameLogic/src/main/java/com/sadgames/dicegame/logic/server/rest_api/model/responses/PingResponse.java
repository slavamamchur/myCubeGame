package com.sadgames.dicegame.logic.server.rest_api.model.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PingResponse extends BasicResponse {

    private String name;

    public PingResponse() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
