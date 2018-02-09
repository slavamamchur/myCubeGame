package com.cubegames.slava.cubegame.rest_api;

import com.cubegames.slava.cubegame.gl3d_engine.utils.ISysUtilsWrapper;
import com.cubegames.slava.cubegame.rest_api.model.BasicNamedDbEntity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;

public class GetEntityController<T extends BasicNamedDbEntity> extends AbstractHttpRequest<T> {

    protected GetEntityController(String action, Class<T> responseType, ISysUtilsWrapper sysUtilsWrapper) {
        super(action, responseType, HttpMethod.GET, sysUtilsWrapper);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }
}
