package com.cubegames.slava.cubegame.api;

/**
 * Created by Slava on 15.07.2016.
 */
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

    public static String getBaseUrl() {
        return "http://192.168.1.105:8080/engine";
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
