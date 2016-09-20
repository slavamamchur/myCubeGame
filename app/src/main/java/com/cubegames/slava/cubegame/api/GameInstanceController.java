package com.cubegames.slava.cubegame.api;

import android.content.Context;

import com.cubegames.slava.cubegame.model.CollectionResponseGameInstance;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.GameInstanceResponse;
import com.cubegames.slava.cubegame.model.IdResponse;
import com.cubegames.slava.cubegame.model.StartNewGameRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.cubegames.slava.cubegame.api.RestConst.PARAM_HEADER_AUTH_TOKEN;
import static com.cubegames.slava.cubegame.api.RestConst.URL_LIST;

public class GameInstanceController extends AbstractHttpRequest<GameInstance>{

    public GameInstanceController(Context ctx) {
        super(GameInstance.ACTION_NAME, GameInstance.class, HttpMethod.GET, ctx);
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

    public IdResponse startNewInstance(StartNewGameRequest request) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_START,
                HttpMethod.POST, getHttpEntity(request), IdResponse.class).getBody();
    }

    public GameInstanceResponse makeTurn(GameInstance instance, int steps) throws WebServiceException {

        RestTemplate restTemplate = getRestTemplate();

        return restTemplate.exchange(getmUrl() + RestConst.URL_GAME_INSTANCE_MOVE,
                HttpMethod.GET, getHttpEntity(null), GameInstanceResponse.class, instance.getId(), steps).getBody();
    }

}
