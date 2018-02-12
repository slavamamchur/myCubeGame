package com.sadgames.dicegame.rest_api;

import com.sadgames.dicegame.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.rest_api.model.responses.GameInstanceCollectionResponse;
import com.sadgames.dicegame.rest_api.model.responses.GameInstanceResponse;
import com.sadgames.dicegame.rest_api.model.responses.GameInstanceStartedResponse;
import com.sadgames.dicegame.rest_api.model.responses.IdResponse;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.dicegame.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.dicegame.rest_api.RestConst.URL_LIST;

public class GameInstanceController extends AbstractHttpRequest<GameInstanceEntity>{

    public GameInstanceController(SysUtilsWrapperInterface sysUtilsWrapper) {
        super(GameInstanceEntity.ACTION_NAME, GameInstanceEntity.class, HttpMethod.GET, sysUtilsWrapper);
    }

    @Override
    protected HttpEntity<?> getHttpEntity(Object entity) {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_HEADER_AUTH_TOKEN, getAuthToken());
        //params.put(PAGE_OFFSET_HEADER, "1");
        //params.put(PAGE_LIMIT_HEADER, "2");
        //params.put(PAGE_SORT_BY_HEADER, "lastUsedDate");
        //params.put(PAGE_SORT_HEADER, "asc");

        return getHeaderAndObjectParamsHttpEntity(params, entity);
    }

    @Override
    public Collection getResponseList() throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        ResponseEntity<GameInstanceCollectionResponse> responseEntity =
                    restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), GameInstanceCollectionResponse.class);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public GameInstanceResponse finishInstance(GameInstanceEntity instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_FINISH,
                HttpMethod.GET, getHttpEntity(null), GameInstanceResponse.class, instance.getId()).getBody();
    }

    public IdResponse restartInstance(GameInstanceEntity instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_RESTART,
                HttpMethod.GET, getHttpEntity(null), IdResponse.class, instance.getId()).getBody();
    }

    public GameInstanceStartedResponse startNewInstance(StartNewGameRequestParam request) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_START,
                HttpMethod.POST, getHttpEntity(request), GameInstanceStartedResponse.class).getBody();
    }

    public GameInstanceEntity makeTurn(GameInstanceEntity instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_MOVE,
                HttpMethod.GET, getHttpEntity(null), GameInstanceEntity.class, instance.getId(), instance.getStepsToGo()).getBody();
    }

}
