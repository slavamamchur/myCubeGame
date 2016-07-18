package com.cubegames.slava.cubegame.api;

/**
 * Created by Slava on 16.07.2016.
 */
public class LoginResponse {

    private final String id;
    private final String error;

    public LoginResponse(String id, String error) {
        this.id = id;
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return String.format("(id: %s, error: %s)", id, error);
    }

}
