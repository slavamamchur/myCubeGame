package com.cubegames.slava.cubegame.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cubegames.slava.cubegame.SettingsManager;
import com.cubegames.slava.cubegame.model.BasicDbEntity;
import com.cubegames.slava.cubegame.model.BasicEntity;
import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.NET_CONNECT_TIMEOUT_MILLIS;
import static com.cubegames.slava.cubegame.api.RestConst.NET_READ_TIMEOUT_MILLIS;
import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_CREATE;
import static com.cubegames.slava.cubegame.api.RestConst.URL_DELETE;
import static com.cubegames.slava.cubegame.api.RestConst.URL_FIND;
import static com.cubegames.slava.cubegame.api.RestConst.URL_LIST;
import static com.cubegames.slava.cubegame.api.RestConst.URL_UPDATE;

public abstract class AbstractHttpRequest<T extends BasicEntity>{

    private String mUrl;
    protected Class<T> mResponseType;
    private final HttpMethod mHttpMethod;
    protected final Context ctx;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ResponsesList<T> {
        @JsonProperty(required = false)
        private ArrayList<T> collection;

        public void setCollection(ArrayList<T> collection) {
            this.collection = collection;
        }
        public ArrayList<T> getCollection() {
            return this.collection;
        }
    }

    protected AbstractHttpRequest(final String url, Class<T> responseType, HttpMethod httpMethod, Context ctx) {
        this.ctx = ctx;
        mUrl = getBaseUrl() + url;
        this.mResponseType = responseType;
        this.mHttpMethod = httpMethod;
    }

    public final String getAuthToken() {
        return SettingsManager.getInstance(ctx).getAuthToken();
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
        sendRequestWithParams(action, mHttpMethod, null, args);
    }

    protected void sendRequestWithParams(String action, HttpMethod method, Object entity, Object ... args)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(mUrl + action, method, getHttpEntity(entity), mResponseType, args);
    }

    protected void sendRequest(String action, Object entity)  throws WebServiceException {
        sendRequestWithParams(action, mHttpMethod, entity);
    }

    protected void sendPostRequest(String action, Object entity)  throws WebServiceException {
        sendRequestWithParams(action, HttpMethod.POST, entity);
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

    public ArrayList<T> getResponseList()  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<ResponsesList> responseEntity;
        try {
            responseEntity =
                    restTemplate.exchange(mUrl + URL_LIST, mHttpMethod, getHttpEntity(), ResponsesList.class);
        }
        catch (Exception e){
            throw new WebServiceException(HttpStatus.OK,"");
        }

        return responseEntity.getBody().getCollection();
    }

    /*public byte[] getBinaryData(BasicDbEntity entity)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<ResponsesList> responseEntity = restTemplate.
                //restTemplate.exchange(mUrl + URL_LIST, mHttpMethod, getHttpEntity(), ResponsesList.class, entity.getId());

        return responseEntity.getBody().getResponseData().getCollection();
    }*/

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

    protected HttpEntity<?> getHeaderAndObjectParamsHttpEntity(Map<String, String> params){
        return getHeaderAndObjectParamsHttpEntity(params, null);
    }
    protected HttpEntity<?> getHeaderAndObjectParamsHttpEntity(Map<String, String> params, Object entity) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setUserAgent("ANDROID");
        if (params != null)
            requestHeaders.setAll(params);

        if (entity != null) {
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new HttpEntity<>(entity, requestHeaders);
        }
        else
            return new HttpEntity<>(requestHeaders);
    }

    public void addChild(BasicNamedDbEntity child){
        //TODO: add child
    }

    public void removeChild(String id, String childName, int index) throws WebServiceException{
        String actionURL = mUrl + "/{id}" + childName + "/{index}";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(actionURL, HttpMethod.GET, getHeaderAndObjectParamsHttpEntity(params, null), ErrorEntity.class, id, index);
    }
}
