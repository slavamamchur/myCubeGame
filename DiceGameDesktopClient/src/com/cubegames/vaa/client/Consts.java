package com.cubegames.vaa.client;

public class Consts {

  public static final String HEADER_USER_NAME = "user-name";
  public static final String HEADER_USER_PASS = "user-pass";
  public static final String HEADER_USER_TOKEN = "user-token";

  public static final String PARAM_USER_TOKEN = "?" + HEADER_USER_TOKEN + "=";

  public static final String BASE_URL = "http://localhost:8080/engine";

  public static final String LOGIN_URL = "/login";
  public static final String REGISTER_URL = "/register";
  public static final String LIST_URL = "/list";
  public static final String DELETE_URL = "/delete/";
  public static final String FIND_URL = "/find/";
  public static final String CREATE_URL = "/create";
  public static final String UPDATE_URL = "/update";

  public static final String MAP_URL = "/map";
  public static final String GAME_URL = "/game";
  public static final String INSTANCE_URL = "/instance";
  public static final String PLAYER_URL = "/player";

  public static final String MAP_LIST_URL = MAP_URL + LIST_URL;
  public static final String MAP_DELETE_URL = MAP_URL + DELETE_URL;
  public static final String MAP_IMAGE_URL = "/image/";
  public static final String MAP_RELIEF_URL = "/relief/";
  public static final String MAP_UPDATE_URL = MAP_URL + UPDATE_URL;
  public static final String MAP_CREATE_URL = MAP_URL + CREATE_URL;
  //public static final String MAP_UPLOAD_IMAGE_URL = MAP_URL + "/upload";
  public static final String MAP_UPLOAD_IMAGE_JSON_URL = MAP_URL + "/file/image";
  public static final String MAP_UPLOAD_RELIEF_JSON_URL = MAP_URL + "/file/relief";

  public static final String INSTANCE_LIST_URL = INSTANCE_URL + LIST_URL;
  public static final String INSTANCE_DELETE_URL = INSTANCE_URL + DELETE_URL;
  public static final String INSTANCE_FINISH_URL = INSTANCE_URL + "/finish/";
  public static final String INSTANCE_GET_URL = INSTANCE_URL + FIND_URL;
  public static final String INSTANCE_RESTART_URL = INSTANCE_URL + "/restart/";
  public static final String INSTANCE_MOVE_URL = INSTANCE_URL + "/move/";
  public static final String INSTANCE_START_URL = INSTANCE_URL + "/start";

  public static final String GAME_LIST_URL = GAME_URL + LIST_URL;
  public static final String GAME_DELETE_URL = GAME_URL + DELETE_URL;
  public static final String GAME_CREATE_URL = GAME_URL + CREATE_URL;
  public static final String GAME_UPDATE_URL = GAME_URL + UPDATE_URL;

  public static final String PLAYER_LIST_URL = PLAYER_URL + LIST_URL;
  public static final String PLAYER_DELETE_URL = PLAYER_URL + DELETE_URL;
  public static final String PLAYER_CREATE_URL = PLAYER_URL + CREATE_URL;
  public static final String PLAYER_UPDATE_URL = PLAYER_URL + UPDATE_URL;

}
