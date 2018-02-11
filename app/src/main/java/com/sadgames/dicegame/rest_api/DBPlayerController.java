package com.sadgames.dicegame.rest_api;

import com.sadgames.dicegame.rest_api.model.CollectionResponseDBPlayer;
import com.sadgames.dicegame.rest_api.model.DbPlayer;
import com.sadgames.sysutils.ISysUtilsWrapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.dicegame.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.dicegame.rest_api.RestConst.URL_LIST;

public class DBPlayerController extends AbstractHttpRequest<DbPlayer> {

    public DBPlayerController(ISysUtilsWrapper sysUtilsWrapper) {
        super(DbPlayer.ACTION_NAME, DbPlayer.class, HttpMethod.GET, sysUtilsWrapper);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    @Override
    public Collection getResponseList() throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<CollectionResponseDBPlayer> responseEntity =
                restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), CollectionResponseDBPlayer.class);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }
}
