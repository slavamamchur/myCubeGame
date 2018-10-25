package com.sadgames.sysutils.platforms.android.restapi;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity;

import org.json.JSONObject;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static com.sadgames.sysutils.common.CommonUtils.convertStreamToString;

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

}
