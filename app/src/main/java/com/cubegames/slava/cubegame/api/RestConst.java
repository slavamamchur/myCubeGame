package com.cubegames.slava.cubegame.api;

/**
 * Created by Slava Mamchur on 15.07.2016.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class RestConst {
    // main controller
    public static final String URL_PING = "/ping";
    public static final String URL_LOGIN = "/login";
    public static final String URL_REGISTER = "/register";

    // game controller
    public static final String URL_GAME = "/game";
    public static final String URL_GAME_NEW_POINT = "/{gameId}/point/{xPos}/{yPos}/{type}/{nextIndex}/{flyIndex}";
    public static final String URL_GAME_REMOVE_POINT = "/{gameId}/point/{index}";

    // instance controller
    public static final String URL_GAME_INSTANCE = "/instance";
    public static final String URL_GAME_INSTANCE_START = "/start";
    public static final String URL_GAME_INSTANCE_RESTART = "/restart/{instanceId}";
    public static final String URL_GAME_INSTANCE_MOVE = "/move/{instanceId}/{steps}";
    public static final String URL_GAME_INSTANCE_FINISH = "/finish/{instanceId}";

    // map controller
    public static final String URL_GAME_MAP = "/map";
    public static final String URL_GAME_MAP_IMAGE = "/image/{mapId}";
    public static final String URL_GAME_MAP_IMAGE_SIMPLE = "/upload";

    // player controller
    public static final String URL_PLAYER = "/player";

    // common URLs
    public static final String URL_COUNT = "/count";
    public static final String URL_CREATE = "/create";
    public static final String URL_FIND = "/find/{itemId}";
    public static final String URL_DELETE = "/delete/{itemId}";
    public static final String URL_UPDATE = "/update";
    public static final String URL_LIST = "/list";

    //Parameters
    public static final String PARAM_LOGIN_USER_NAME = "user-name";
    public static final String PARAM_LOGIN_USER_PASS = "user-pass";
    public static final String PARAM_HEADER_AUTH_TOKEN = "user-token";

    /**
     * Network read timeout, in milliseconds.
     */
    public static final int NET_READ_TIMEOUT_MILLIS = 60000;  // 15 seconds
    /**
     * Network connection timeout, in milliseconds.
     */
    public static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    //TODO - from stored prop
    public static final String getBaseUrl() {
        return "http://10.0.2.2:8080/engine";
    }

}
