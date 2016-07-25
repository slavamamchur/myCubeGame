package com.cubegames.slava.cubegame.api;

import com.cubegames.slava.cubegame.model.ErrorEntity;

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
