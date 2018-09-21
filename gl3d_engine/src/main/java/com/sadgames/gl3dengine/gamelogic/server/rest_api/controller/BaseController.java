package com.sadgames.gl3dengine.gamelogic.server.rest_api.controller;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.WebServiceException;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_LIST;

public class BaseController<T extends BasicNamedDbEntity, C extends GenericCollectionResponse>
       extends AbstractHttpRequest<T> {

    private Class<C> listType;
    private Map<String, String> params;

    public BaseController(String action,
                          Class<T> responseType,
                          Class<C> listType,
                          Map<String, String> params,
                          SysUtilsWrapperInterface sysUtilsWrapper) {
        super(action, responseType, HttpMethod.GET, sysUtilsWrapper);

        this.listType = listType;
        this.params = params;
    }

    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        if (params == null)
            params = new HashMap<>();

        if (!params.containsKey(PARAM_HEADER_AUTH_TOKEN))
            params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        //params.put(PAGE_OFFSET_HEADER, "1");
        //params.put(PAGE_LIMIT_HEADER, "2");
        //params.put(FILTER_BY_NAME, "test_");
        //params.put(PAGE_SORT_BY_HEADER, "lastUsedDate");
        //params.put(PAGE_SORT_HEADER, "asc");

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    @Override
    public Collection getResponseList() throws WebServiceException {
        ResponseEntity<C> responseEntity =
                getRestTemplate().exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), listType);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public BasicEntity getResponseWithParams(String action, HttpMethod method, Object entity, Class<?> responseType, Object ... args)  throws WebServiceException {
        return (BasicEntity) getRestTemplate().exchange(getmUrl() + action, method, getHttpEntity(entity), responseType, args).getBody();
    }

    public BasicEntity getResponseWithGetParams(String action, Object entity, Class<?> responseType, Object ... args) {
        return getResponseWithParams(action, HttpMethod.GET, entity, responseType, args);
    }

    public BasicEntity getResponseWithPostParams(String action, Object entity, Class<?> responseType, Object ... args) {
        return getResponseWithParams(action, HttpMethod.POST, entity, responseType, args);
    }
}
