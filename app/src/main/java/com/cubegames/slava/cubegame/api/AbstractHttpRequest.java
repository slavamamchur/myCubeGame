package com.cubegames.slava.cubegame.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cubegames.slava.cubegame.SettingsManager;
import com.cubegames.slava.cubegame.model.BasicDbEntity;
import com.cubegames.slava.cubegame.model.BasicEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.NET_CONNECT_TIMEOUT_MILLIS;
import static com.cubegames.slava.cubegame.api.RestConst.NET_READ_TIMEOUT_MILLIS;
import static com.cubegames.slava.cubegame.api.RestConst.URL_CREATE;
import static com.cubegames.slava.cubegame.api.RestConst.URL_DELETE;
import static com.cubegames.slava.cubegame.api.RestConst.URL_FIND;
import static com.cubegames.slava.cubegame.api.RestConst.URL_LIST;
import static com.cubegames.slava.cubegame.api.RestConst.URL_UPDATE;

public abstract class AbstractHttpRequest<T extends BasicEntity>{

    private String mUrl;
    protected Class<T> mResponseType;
    private final HttpMethod mHttpMethod;
    private final Context ctx;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ResponseList {
        @JsonProperty(required = false)
        private ResponseData responseData;

        public void setResponseData(ResponseData responseData) {
            this.responseData = responseData;
        }
        public ResponseData getResponseData() {
            return this.responseData;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        private class ResponseData<T> {

            private List<T> collection;

            public void setCollection(List<T> collection) {
                this.collection = collection;
            }
            public List<T> getCollection() {
                return this.collection;
            }
        }
    }

    protected AbstractHttpRequest(final String url, Class<T> responseType, HttpMethod httpMethod, Context ctx) {
        this.ctx = ctx;
        mUrl = getBaseUrl() + url;
        this.mResponseType = responseType;
        this.mHttpMethod = httpMethod;
    }

    public final String getBaseUrl() {
        return SettingsManager.getInstance(ctx).getWebServiceUrl();
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

    protected void sendRequestWithParams(String action, Object ... args)  throws WebServiceException {
        sendRequestWithParams(action, mHttpMethod, args);
    }

    protected void sendRequestWithParams(String action, HttpMethod method, Object ... args)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(mUrl + action, method, getHttpEntity(), mResponseType, args);
    }

    protected void sendRequest(String action, Object entity)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(mUrl + action, mHttpMethod, getHttpEntity(entity), mResponseType);
    }

    protected void sendPostRequest(String action, Object entity)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(mUrl + action, HttpMethod.POST, getHttpEntity(entity), mResponseType);
    }

    protected T getResponse(String action, Object ... args)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<T> responseEntity = restTemplate.exchange(mUrl + action, mHttpMethod, getHttpEntity(), mResponseType, args);

        return responseEntity.getBody();
    }

    protected T getResponse(Object ... args)  throws WebServiceException {
        return getResponse("", args);
    }

    public T find(String id)  throws WebServiceException {
        return getResponse(URL_FIND, id);
    }

    public void create(BasicDbEntity entity)  throws WebServiceException {
        sendPostRequest(URL_CREATE, entity);
    }

    public void update(BasicDbEntity entity)  throws WebServiceException {
        sendPostRequest(URL_UPDATE, entity);
    }

    public void delete(BasicDbEntity entity)  throws WebServiceException {
        sendRequestWithParams(URL_DELETE, HttpMethod.DELETE, entity.getId());
    }

    public List<T> getResponseList()  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<ResponseList> responseEntity =
                restTemplate.exchange(mUrl + URL_LIST, mHttpMethod, getHttpEntity(), ResponseList.class);

        return responseEntity.getBody().getResponseData().getCollection();
    }

    @NonNull
    protected RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(NET_READ_TIMEOUT_MILLIS);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        return restTemplate;
    }

    private HttpEntity<?> getHttpEntity(){
        return getHttpEntity(null);
    }

    protected abstract HttpEntity<?> getHttpEntity(Object entity);

    protected HttpEntity<?> getHeaderParamsHttpEntity(Map<String, String> params) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setUserAgent("ANDROID");
        requestHeaders.setAll(params);

        return new HttpEntity<>(requestHeaders);
    }

    protected HttpEntity<?> getJsonObjectParamsHttpEntity(Object params) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setUserAgent("ANDROID");

        return new HttpEntity<>(params, requestHeaders);
    }

    protected HttpEntity<?> getHeaderAndObjectParamsHttpEntity(Map<String, String> hparams, Object params) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setUserAgent("ANDROID");
        requestHeaders.setAll(hparams);

        return new HttpEntity<>(params, requestHeaders);
    }
}
