package com.sadgames.dicegame.rest_api.model;

public class IdResponse extends BasicResponse {

    protected String id;

    public IdResponse() {
        // constructor for serializer
    }
    public IdResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

}
