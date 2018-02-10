package com.sadgames.dicegame.rest_api;

import com.sadgames.dicegame.rest_api.model.CollectionResponseGameInstance;
import com.sadgames.dicegame.rest_api.model.GameInstance;
import com.sadgames.dicegame.rest_api.model.GameInstanceResponse;
import com.sadgames.dicegame.rest_api.model.GameInstanceStartedResponse;
import com.sadgames.dicegame.rest_api.model.IdResponse;
import com.sadgames.dicegame.rest_api.model.StartNewGameRequest;
import com.sadgames.gl3d_engine.utils.ISysUtilsWrapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.sadgames.dicegame.rest_api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.sadgames.dicegame.rest_api.RestConst.URL_LIST;

public class GameInstanceController extends AbstractHttpRequest<GameInstance>{

    public GameInstanceController(ISysUtilsWrapper sysUtilsWrapper) {
        super(GameInstance.ACTION_NAME, GameInstance.class, HttpMethod.GET, sysUtilsWrapper);
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

        ResponseEntity<CollectionResponseGameInstance> responseEntity =
                    restTemplate.exchange(getmUrl() + URL_LIST, HttpMethod.GET, getHttpEntity(null), CollectionResponseGameInstance.class);

        return responseEntity.getBody() == null ? null : responseEntity.getBody().getCollection();
    }

    public GameInstanceResponse finishInstance(GameInstance instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_FINISH,
                HttpMethod.GET, getHttpEntity(null), GameInstanceResponse.class, instance.getId()).getBody();
    }

    public IdResponse restartInstance(GameInstance instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_RESTART,
                HttpMethod.GET, getHttpEntity(null), IdResponse.class, instance.getId()).getBody();
    }

    public GameInstanceStartedResponse startNewInstance(StartNewGameRequest request) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_START,
                HttpMethod.POST, getHttpEntity(request), GameInstanceStartedResponse.class).getBody();
    }

    public GameInstance makeTurn(GameInstance instance) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_MOVE,
                HttpMethod.GET, getHttpEntity(null), GameInstance.class, instance.getId(), instance.getStepsToGo()).getBody();
    }

}
