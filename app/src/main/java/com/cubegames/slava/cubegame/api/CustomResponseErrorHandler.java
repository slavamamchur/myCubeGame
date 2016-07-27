package com.cubegames.slava.cubegame.api;

import android.text.TextUtils;

import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.support.Base64;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    private ErrorEntity fetchErrorEntity(ClientHttpResponse response){
        ErrorEntity error;

        try {
             String sresp = convertStreamToString(response.getBody());
             JSONObject jresp = new JSONObject(sresp);
             error = new ErrorEntity();
             error.setError(jresp.getString("error"));
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
