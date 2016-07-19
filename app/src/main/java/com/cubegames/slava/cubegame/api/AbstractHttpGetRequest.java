package com.cubegames.slava.cubegame.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.NET_CONNECT_TIMEOUT_MILLIS;
import static com.cubegames.slava.cubegame.api.RestConst.NET_READ_TIMEOUT_MILLIS;

/**
 * Created by Slava Mamchur on 19.07.2016.
 */
@SuppressWarnings("DefaultFileTemplate")
public abstract class AbstractHttpGetRequest<T>{

    private final String mUrl;
    private Map<String, String> mParams;
    private final Class<T> mResponseType;

    protected AbstractHttpGetRequest(final String url, final Map<String, String> params, Class<T> responseType) {
        mUrl = url;
        mParams = params;
        this.mResponseType = responseType;
    }

    protected AbstractHttpGetRequest(final String url, Class<T> responseType) {
        this(url, new HashMap<String, String>(), responseType);
    }

    @SuppressWarnings("unused")
    public String getmUrl() {
        return mUrl;
    }
    public Map<String, String> getmParams() {
        return mParams;
    }
    @SuppressWarnings("unused")
    public Class<T> getmResponseType() {
        return mResponseType;
    }

    public T getResponse() throws Exception{
        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(acceptableMediaTypes);
        requestHeaders.setAll(mParams);

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(NET_READ_TIMEOUT_MILLIS);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ResponseEntity<T> responseEntity = restTemplate.exchange(mUrl, HttpMethod.GET, new HttpEntity<>(requestHeaders), mResponseType);

        return responseEntity.getBody();
    }

    //TODO OnError
}
