package com.sadgames.sysutils.platforms.android.restapi;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

public class WebServiceException extends HttpServerErrorException {

    private ErrorEntity errorObject;

    public WebServiceException(HttpStatus statusCode, String statusText) {
        this(statusCode, statusText, null);
    }

    public WebServiceException(HttpStatus statusCode, String statusText, ErrorEntity error) {
        super(statusCode, statusText);

        this.errorObject = error;
    }

    public ErrorEntity getErrorObject() {
        return errorObject;
    }
}
