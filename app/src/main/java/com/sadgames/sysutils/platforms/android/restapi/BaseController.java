package com.sadgames.sysutils.platforms.android.restapi;

import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.responses.GenericCollectionResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PAGE_SORT_BY_HEADER;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PAGE_SORT_HEADER;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.gl3dengine.gamelogic.server.rest_api.RestConst.URL_LIST;

public class BaseController<T extends BasicEntity, C extends GenericCollectionResponse>
       extends AbstractHttpRequest<T> {

    protected static final int HTTP_STATUS_NOT_FOUND = HttpStatus.NOT_FOUND.ordinal();

    private Class<C> listType;
    private Map<String, String> params;

    public BaseController(String action,
                          Class<T> responseType,
                          Class<C> listType,
                          Map<String, String> params) {
        super(action, responseType, HttpMethod.GET);

        this.listType = listType;
        this.params = params;
    }

    public BaseController(String action,
                          Class<T> responseType,
                          Class<C> listType,
                          Map<String, String> params,
                          int method) {
        super(action, responseType, HttpMethod.values()[method]);

        this.listType = listType;
        this.params = params;
    }

    public Map<String, String> getParams() {
        return params;
    }
    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    protected HttpEntity<?> getHttpEntity(Object entity, Map<String, String> params) {
        this.params = params;

        return getHttpEntity(entity);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        if (params == null) {
            params = new HashMap<>();
            params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());
        }

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    @Override
    public Collection getResponseList() throws WebServiceException {
        Map<String, String> old_params = params;

        params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());
        params.put(PAGE_SORT_BY_HEADER, "lastUsedDate");
        params.put(PAGE_SORT_HEADER, "desc");
        //params.put(PAGE_OFFSET_HEADER, "1");
        //params.put(PAGE_LIMIT_HEADER, "2");

        ResponseEntity<C> responseEntity =
                getRestTemplate().exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), listType);

        params.clear();
        params = old_params;

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public BasicEntity getResponseWithParams(String action, int method, Object entity, Class<?> responseType, Object ... args)  throws WebServiceException {
        return getResponseWithParams(action, HttpMethod.values()[method], entity, responseType, args);
    }
    public BasicEntity getResponseWithParams(String action, HttpMethod method, Object entity, Class<?> responseType, Object ... args)  throws WebServiceException {
        return (BasicEntity) getRestTemplate().exchange(getmUrl() + action, method, getHttpEntity(entity), responseType, args).getBody();
    }

}
