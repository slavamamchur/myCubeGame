package com.cubegames.slava.cubegame.api;

import org.springframework.http.HttpStatus;

public class WebServiceException extends Exception {
    private HttpStatus errorCode;

    public WebServiceException(HttpStatus errorCode) {
        this.errorCode = errorCode;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(HttpStatus errorCode) {
        this.errorCode = errorCode;
    }
}
