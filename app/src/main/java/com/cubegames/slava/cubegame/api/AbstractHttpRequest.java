package com.cubegames.slava.cubegame.api;

import android.support.annotation.NonNull;

import com.cubegames.slava.cubegame.model.AbstractResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.NET_CONNECT_TIMEOUT_MILLIS;
import static com.cubegames.slava.cubegame.api.RestConst.NET_READ_TIMEOUT_MILLIS;
import static com.cubegames.slava.cubegame.api.RestConst.URL_LIST;
import static com.cubegames.slava.cubegame.api.RestConst.getBaseUrl;

public abstract class AbstractHttpRequest<T extends AbstractResponse>{

    private String mUrl;
    protected Class<T> mResponseType;
    private final HttpMethod mHttpMethod;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ResponseList {
        @JsonProperty(required = false)
        private ResponseData responseData;

        @JsonProperty(required = false)
        private String error;

        public String getError() {
            return error;
        }
        public void setError(String error) {
            this.error = error;
        }

        public void setResponseData(ResponseData responseData) {
            this.responseData = responseData;
        }
        public ResponseData getResponseData() {
            return this.responseData;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        private class ResponseData<T> {

            private List<T> items;

            public void setItems(List<T> items) {
                this.items = items;
            }
            public List<T> getItems() {
                return this.items;
            }
        }
    }

    protected AbstractHttpRequest(final String url, Class<T> responseType, HttpMethod httpMethod) {
        mUrl = getBaseUrl() + url;
        this.mResponseType = responseType;
        this.mHttpMethod = httpMethod;
    }

    public String getmUrl() {
        return mUrl;
    }
    public Class<T> getmResponseType() {
        return mResponseType;
    }
    public void setmUrl(String mUrl) {
        this.mUrl = getBaseUrl() + mUrl;
    }
    public void setmResponseType(Class<T> mResponseType) {
        this.mResponseType = mResponseType;
    }

    public T getResponse()  throws RestClientException, WebServiceException {
        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<T> responseEntity = restTemplate.exchange(mUrl+"/hunya", mHttpMethod, getHttpEntity(), mResponseType);

        if(responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return responseEntity.getBody();
        }
        else {
            throw new WebServiceException(responseEntity.getStatusCode());
        }
    }

    public List<T> getResponseList() {
        RestTemplate restTemplate = getRestTemplate();

        try {
            ResponseEntity<ResponseList> responseEntity = restTemplate.exchange(mUrl + URL_LIST, mHttpMethod, getHttpEntity(), ResponseList.class);
            //TODO Error code in entity and Error exception rise
            //responseEntity.getStatusCode().getReasonPhrase();
            return responseEntity.getBody().getResponseData().getItems();
        } catch (Exception e) {
            String error = e.getLocalizedMessage();
            return null;
        }
    }

    @NonNull
    private RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(NET_READ_TIMEOUT_MILLIS);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    protected abstract HttpEntity<?> getHttpEntity();

    protected HttpEntity<?> getHeaderParamsHttpEntity(Map<String, String> params) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAll(params);

        return new HttpEntity<>(requestHeaders);
    }

    protected HttpEntity<?> getJsonObjectParamsHttpEntity(Object params) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(params, requestHeaders);
    }
}
