package com.sadgames.sysutils.platforms.android;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.WebServiceException;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.controller.AbstractHttpRequest;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_LIST;

public class BaseController<T extends BasicNamedDbEntity> extends AbstractHttpRequest<T> {

    private Class<GenericCollectionResponse<T>> listType;
    private Map<String, String> params;

    public BaseController(String action,
                          Class<T> responseType,
                          Class<GenericCollectionResponse<T>> listType,
                          Map<String, String> params,
                          SysUtilsWrapperInterface sysUtilsWrapper) {
        super(action, responseType, HttpMethod.GET, sysUtilsWrapper);

        this.listType = listType;
        this.params = params;
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        if (params == null)
            params = new HashMap<>();

        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    @Override
    public Collection getResponseList() throws WebServiceException {
        RestTemplate restTemplate = getRestTemplate();
        ResponseEntity<GenericCollectionResponse<T>> responseEntity =
                restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), listType);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }
}
