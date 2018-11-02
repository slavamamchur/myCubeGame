package com.cubegames.vaa.client;

import com.cubegames.engine.consts.RestCommonConsts;
import com.cubegames.engine.domain.entities.DbPlayer;
import com.cubegames.engine.domain.entities.Game;
import com.cubegames.engine.domain.entities.GameInstance;
import com.cubegames.engine.domain.entities.GameMap;
import com.cubegames.engine.domain.entities.players.InstancePlayer;
import com.cubegames.engine.domain.rest.PaginationInfo;
import com.cubegames.engine.domain.rest.requests.RegisterRequest;
import com.cubegames.engine.domain.rest.requests.StartNewGameRequest;
import com.cubegames.engine.domain.rest.responses.BasicResponse;
import com.cubegames.engine.domain.rest.responses.CollectionResponse;
import com.cubegames.engine.domain.rest.responses.GameInstanceResponse;
import com.cubegames.engine.domain.rest.responses.GameInstanceStartedResponse;
import com.cubegames.engine.domain.rest.responses.IdResponse;
import com.cubegames.vaa.client.exceptions.EmptyTokenException;
import com.cubegames.vaa.client.responces.CollectionResponseDbPlayer;
import com.cubegames.vaa.client.responces.CollectionResponseGame;
import com.cubegames.vaa.client.responces.CollectionResponseGameMap;
import com.cubegames.vaa.client.responces.CollectionResponseInstance;
import com.google.common.base.Strings;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static com.cubegames.vaa.client.Consts.INSTANCE_START_URL;
import static com.cubegames.vaa.client.Consts.MAP_IMAGE_URL;
import static com.cubegames.vaa.client.Consts.MAP_RELIEF_URL;
import static com.cubegames.vaa.client.Consts.MAP_URL;
import static com.cubegames.vaa.client.Consts.PARAM_USER_TOKEN;
import static com.cubegames.vaa.utils.UtilsUI.checkServiceError;

public class RestClient {

  private static final Logger log = Logger.getLogger(RestClient.class.getName());

  private String baseUrl = Consts.BASE_URL;
  private String token;

  public RestClient(String baseUrl) {
    if (!Strings.isNullOrEmpty(baseUrl)) {
      this.baseUrl = baseUrl;
    }
    log.info("Real Base URL: " + this.baseUrl);
  }


//  public void setBaseUrl(String baseUrl) {
//    this.baseUrl = baseUrl;
//  }


//  public String getAccessToken() {
//    return token;
//  }


  private String loginInternal(String username, String password) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.set(Consts.HEADER_USER_NAME, username);
    headers.set(Consts.HEADER_USER_PASS, password);

    HttpEntity<String> request = new HttpEntity<>("", headers);

    ResponseEntity<IdResponse> resp = restTemplate.exchange(getUrl(Consts.LOGIN_URL), HttpMethod.GET, request, IdResponse.class);
    if (resp == null) {
      return null;
    }

    return resp.getBody().getId();
  }


  public void login(String username, String password) {
    token = loginInternal(username, password);
  }


  public void logout() {
    //checkToken();
    token = null;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public void register(String username, String password, String email, String language) {
    RegisterRequest request = new RegisterRequest();
    request.setUserName(username);
    request.setUserPass(password);
    request.setEmail(email);
    request.setLanguage(language);

    runRestWithoutTokenCheck(Consts.REGISTER_URL, HttpMethod.POST, Void.class, request, null);
  }


  public Collection<GameMap> getMapList() {
    PaginationInfo paging = createPaging(GameMap.FIELD_LAST_USED_DATE, Sort.Direction.DESC);
    CollectionResponse<GameMap> resp = runRestGet(Consts.MAP_LIST_URL, CollectionResponseGameMap.class, paging);
    if (resp == null) {
      return null;
    }
    return resp.getCollection();
  }


  public Collection<GameInstance> getInstanceList() {
    PaginationInfo paging = createPaging(GameInstance.FIELD_LAST_USED_DATE, Sort.Direction.DESC);
    CollectionResponse<GameInstance> resp = runRestGet(Consts.INSTANCE_LIST_URL, CollectionResponseInstance.class, paging);
    if (resp == null) {
      return null;
    }
    return resp.getCollection();
  }



  public Collection<Game> getGameList() {
    PaginationInfo paging = createPaging(Game.FIELD_CREATED_DATE, Sort.Direction.DESC);
    CollectionResponse<Game> resp = runRestGet(Consts.GAME_LIST_URL, CollectionResponseGame.class, paging);
    if (resp == null) {
      return null;
    }
    return resp.getCollection();
  }


  public Collection<DbPlayer> getDbPlayerList() {
    PaginationInfo paging = createPaging(DbPlayer.FIELD_NAME, Sort.Direction.ASC);
    CollectionResponse<DbPlayer> resp = runRestGet(Consts.PLAYER_LIST_URL, CollectionResponseDbPlayer.class, paging);
    if (resp == null) {
      return null;
    }
    return resp.getCollection();
  }


  public GameInstanceResponse finishInstance(String instanceId) {
    return runRestGet(Consts.INSTANCE_FINISH_URL + instanceId, GameInstanceResponse.class);
  }

  public void deleteInstance(String instanceId) {
    runRestDelete(Consts.INSTANCE_DELETE_URL + instanceId, Void.class);
  }


  public void deleteGame(String gameId) {
    runRestDelete(Consts.GAME_DELETE_URL + gameId, Void.class);
  }


  public void deleteMap(String mapId) {
    runRestDelete(Consts.MAP_DELETE_URL + mapId, Void.class);
  }


  public void deleteDbPlayer(String playerId) {
    runRestDelete(Consts.PLAYER_DELETE_URL + playerId, Void.class);
  }


  public GameInstanceEntity getInstance(String id) {
    return runRestGet(Consts.INSTANCE_GET_URL + id, GameInstanceEntity.class);
  }


  public IdResponse restartInstance(String id) {
    return runRestGet(Consts.INSTANCE_RESTART_URL + id, IdResponse.class);
  }


  public GameInstance moveInstance(String id, int steps) {
    return runRestGet(Consts.INSTANCE_MOVE_URL + id + "/" + steps, GameInstance.class);
  }


  public IdResponse createDbPlayer(String name, int color) {
    DbPlayer dbPlayer = new DbPlayer();
    dbPlayer.setName(name);
    dbPlayer.setColor(color);
    return runRestPost(Consts.PLAYER_CREATE_URL, IdResponse.class, dbPlayer);
  }


  public IdResponse updateDbPlayer(String id, String name, int color) {
    DbPlayer dbPlayer = new DbPlayer();
    dbPlayer.setId(id);
    dbPlayer.setName(name);
    dbPlayer.setColor(color);
    return runRestPut(Consts.PLAYER_UPDATE_URL, IdResponse.class, dbPlayer);
  }


  public IdResponse createMap(String name) {
    GameMap map = new GameMap();
    map.setName(name);
    return runRestPost(Consts.MAP_CREATE_URL, IdResponse.class, map);
  }


  public IdResponse updateMap(String id, String name) {
    GameMap map = new GameMap();
    map.setId(id);
    map.setName(name);
    return runRestPut(Consts.MAP_UPDATE_URL, IdResponse.class, map);
  }


  public void uploadMapImage(String id, byte[] bytes) {
    GameMap map = new GameMap();
    map.setId(id);
    map.setBinaryData(bytes);
    runRestPut(Consts.MAP_UPLOAD_IMAGE_JSON_URL, Void.class, map);
  }


  public void uploadMapRelief(String id, byte[] bytes) {
    GameMap map = new GameMap();
    map.setId(id);
    map.setBinaryDataRelief(bytes);
    runRestPut(Consts.MAP_UPLOAD_RELIEF_JSON_URL, Void.class, map);
  }


  public String getMapImageUrl(String mapId, boolean isImage) {
    return baseUrl +
        MAP_URL + (isImage ? MAP_IMAGE_URL : MAP_RELIEF_URL) + mapId +
        PARAM_USER_TOKEN + this.token;
  }


  public IdResponse createGame(Game game) {
    return runRestPost(Consts.GAME_CREATE_URL, IdResponse.class, game);
  }


  public IdResponse updateGame(Game game) {
    return runRestPut(Consts.GAME_UPDATE_URL, IdResponse.class, game);
  }


  public GameInstance startNewInstance(String instanceName, String gameId, List<InstancePlayer> players, List<String> playerIds) {
    StartNewGameRequest startRequest = new StartNewGameRequest();
    startRequest.setName(instanceName);
    startRequest.setGameId(gameId);
    startRequest.setPlayers(players);
    startRequest.setPlayersId(playerIds);

    GameInstanceStartedResponse resp = runRestPost(INSTANCE_START_URL, GameInstanceStartedResponse.class, startRequest);
    return resp.getInstance();
  }


  private <R> R runRestWithoutTokenCheck(String urlPostfix, HttpMethod method, Class<R> responseClass, Object body, PaginationInfo paging) {
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.set(Consts.HEADER_USER_TOKEN, token);
    if (paging != null) {
      String sortByField = paging.getSortBy();
      if (!Strings.isNullOrEmpty(sortByField)) {
        headers.set(RestCommonConsts.PAGE_SORT_BY_HEADER, sortByField);
        Sort sort = paging.getSort();
        if (sort != null) {
          Sort.Order order = paging.getSort().getOrderFor(sortByField);
          if (order != null) {
            Sort.Direction dir = order.getDirection();
            if (dir != null) {
              headers.set(RestCommonConsts.PAGE_SORT_HEADER, dir.toString());
            }
          }
        }
      }
      //TODO: paging (if needed?)
    }

    HttpEntity<Object> request = new HttpEntity<>(body, headers);

    ResponseEntity<R> responseEntity = restTemplate.exchange(getUrl(urlPostfix), method, request, responseClass);

    R resp = responseEntity.getBody();
    if (resp instanceof BasicResponse) {
      checkServiceError((BasicResponse) resp);
    }

    return resp;
  }

  private <R> R runRest(String urlPostfix, HttpMethod method, Class<R> responseClass, Object body, PaginationInfo paging) {
    checkToken();
    return runRestWithoutTokenCheck(urlPostfix, method, responseClass, body, paging);
  }

  private <R> R runRest(String urlPostfix, HttpMethod method, Class<R> responseClass, PaginationInfo paging) {
    return runRest(urlPostfix, method, responseClass, "", paging);
  }

  private <R> R runRestGet(String urlPostfix, Class<R> responseClass, PaginationInfo paging) {
    return runRest(urlPostfix, HttpMethod.GET, responseClass, paging);
  }

  private <R> R runRestGet(String urlPostfix, Class<R> responseClass) {
    return runRestGet(urlPostfix, responseClass, null);
  }

  private <R> R runRestDelete(String urlPostfix, Class<R> responseClass) {
    return runRest(urlPostfix, HttpMethod.DELETE, responseClass, null);
  }

  private <R> R runRestPost(String urlPostfix, Class<R> responseClass, Object body) {
    return runRest(urlPostfix, HttpMethod.POST, responseClass, body, null);
  }

  private <R> R runRestPut(String urlPostfix, Class<R> responseClass, Object body) {
    return runRest(urlPostfix, HttpMethod.PUT, responseClass, body, null);
  }


  private void checkToken() {
    if (Strings.isNullOrEmpty(token)) {
      throw new EmptyTokenException();
    }
  }

  private String getUrl(String postfix) {
    return baseUrl + postfix;
  }


  private PaginationInfo createPaging(String fieldName, Sort.Direction direction) {
    PaginationInfo paging = new PaginationInfo();
    paging.setSortBy(fieldName);
    paging.setSortOrder(direction);
    return paging;
  }


}
