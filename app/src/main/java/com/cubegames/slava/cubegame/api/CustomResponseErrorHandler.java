package com.cubegames.slava.cubegame.api;

import com.cubegames.slava.cubegame.model.ErrorEntity;

import org.json.JSONObject;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    private ErrorEntity fetchErrorEntity(ClientHttpResponse response){
        ErrorEntity error;

        try {
             String sresp = convertStreamToString(response.getBody());
             JSONObject jresp = new JSONObject(sresp);
             error = new ErrorEntity();
             error.setError(jresp.getString("error"));
             error.setErrorCode(jresp.getInt("errorCode"));
             error.setAuth(jresp.getBoolean("auth"));
        }
        catch (Exception e){
            error = null;
        }

        return error;
    }

    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        throw new WebServiceException(
                response.getStatusCode(), response.getStatusCode().getReasonPhrase(), fetchErrorEntity(response));
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
