package com.sadgames.sysutils.platforms.android.restapi;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.DEFAULT_BASE_URL_VALUE;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.NET_CONNECT_TIMEOUT_MILLIS;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.NET_READ_TIMEOUT_MILLIS;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_DELETE;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_FIND;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_LIST;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_NEW;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_UPDATE;

public abstract class AbstractHttpRequest<T extends BasicEntity> {

    private String mUrl;
    protected Class<T> mResponseType;
    protected final HttpMethod mHttpMethod;
    private SysUtilsWrapperInterface sysUtilsWrapper;

    protected AbstractHttpRequest(final String url, Class<T> responseType, HttpMethod httpMethod, SysUtilsWrapperInterface sysUtilsWrapper) {
        this.sysUtilsWrapper = sysUtilsWrapper;
        mUrl = getBaseUrl() + url;
        this.mResponseType = responseType;
        this.mHttpMethod = httpMethod;
    }

    public final String getAuthToken() {
        return sysUtilsWrapper.iGetSettingsManager().getAuthToken();
    }

    public final String getBaseUrl() {
        return sysUtilsWrapper.iGetSettingsManager().getWebServiceUrl(DEFAULT_BASE_URL_VALUE);
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
    public SysUtilsWrapperInterface getSysUtilsWrapper() {
        return sysUtilsWrapper;
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

    public void sendPostRequest(String action, Object entity)  throws WebServiceException {
        sendRequestWithParams(action, HttpMethod.POST, entity);
    }

    public T getResponse(String action, Object... args)  throws WebServiceException {
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

    public T update(BasicNamedDbEntity entity)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();

        T res = restTemplate.exchange(mUrl + (entity.getId() != null ? URL_UPDATE : URL_NEW),
                HttpMethod.POST, getHttpEntity(entity), mResponseType).getBody();
        return res;
    }

    public void delete(BasicNamedDbEntity entity)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(mUrl + URL_DELETE, HttpMethod.DELETE, getHttpEntity(), ErrorEntity.class, entity.getId());
    }

    public Collection getResponseList()  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<GenericCollectionResponse> responseEntity =
                restTemplate.exchange(mUrl + URL_LIST, HttpMethod.GET, getHttpEntity(), GenericCollectionResponse.class);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public byte[] getBinaryData(BasicNamedDbEntity entity, String dataUrl, String mediaType)  throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.valueOf(mediaType));
        requestHeaders.setUserAgent("ANDROID");
        requestHeaders.setAll(params);

        return restTemplate.exchange(mUrl + dataUrl, HttpMethod.GET, new HttpEntity<>(requestHeaders), byte[].class, entity.getId()).getBody();
    }

    public byte[] getBinaryData(BasicNamedDbEntity entity, String dataUrl)  throws WebServiceException {
        return getBinaryData(entity, dataUrl, MediaType.IMAGE_JPEG_VALUE);
    }

    public String uploadFile(BasicNamedDbEntity entity, String keyParamName, String uploadActionNAme, String fileName) throws WebServiceException {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(NET_READ_TIMEOUT_MILLIS);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("token", getAuthToken());
        formData.add(keyParamName, entity.getId());
        formData.add("file", new FileSystemResource(fileName));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, requestHeaders);

        return restTemplate.exchange(mUrl + uploadActionNAme, HttpMethod.POST, requestEntity, String.class).getBody();
    }

    protected RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(NET_READ_TIMEOUT_MILLIS);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

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

    public void addChild(String id, String childName, Object child){
        String actionURL = mUrl + "/{id}" + childName;

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(actionURL, HttpMethod.POST, getHeaderAndObjectParamsHttpEntity(params, child), ErrorEntity.class, id);
    }

    public void removeChild(String id, String childName, int index) throws WebServiceException{
        String actionURL = mUrl + "/{id}" + childName + "/{index}";

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        RestTemplate restTemplate = getRestTemplate();
        restTemplate.exchange(actionURL, HttpMethod.GET, getHeaderAndObjectParamsHttpEntity(params, null), ErrorEntity.class, id, index);
    }
}
