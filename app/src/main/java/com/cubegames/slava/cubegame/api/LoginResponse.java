package com.cubegames.slava.cubegame.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Slava Mamchur on 16.07.2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("error")
    private String error;

    public String getId() {
        return id;
    }
    public String getError() {
        return error;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setError(String error) {
        this.error = error;
    }

}
