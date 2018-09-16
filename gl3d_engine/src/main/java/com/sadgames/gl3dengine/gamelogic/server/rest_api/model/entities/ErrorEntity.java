package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.BasicResponse;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorEntity extends BasicResponse implements Serializable {

    private static final long serialVersionUID = -1641701625692691201L;

    public ErrorEntity() {}

    public ErrorEntity(String error, int errorCode) {
        setError(error);
        setErrorCode(errorCode);
    }

}
