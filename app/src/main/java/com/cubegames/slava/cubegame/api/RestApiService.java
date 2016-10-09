package com.cubegames.slava.cubegame.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.cubegames.slava.cubegame.model.AuthToken;
import com.cubegames.slava.cubegame.model.BasicEntity;
import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;
import com.cubegames.slava.cubegame.model.DbPlayer;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.GameInstanceStartedResponse;
import com.cubegames.slava.cubegame.model.GameMap;
import com.cubegames.slava.cubegame.model.StartNewGameRequest;
import com.cubegames.slava.cubegame.model.UserEntity;

import java.util.ArrayList;

public class RestApiService extends IntentService {
    @SuppressWarnings("unused")
    private final String TAG = "RestApiService";

    public static final String ACTION_LOGIN = "com.cubegames.slava.cubegame.api.action.LOGIN";
    public static final String ACTION_RELOGIN = "com.cubegames.slava.cubegame.api.action.RELOGIN";
    public static final String ACTION_LOGIN_RESPONSE = "com.cubegames.slava.cubegame.api.action.LOGIN_RESPONSE";
    public static final String ACTION_RELOGIN_RESPONSE = "com.cubegames.slava.cubegame.api.action.RELOGIN_RESPONSE";
    public static final String ACTION_REGISTRATION = "com.cubegames.slava.cubegame.api.action.REGISTRATION";
    public static final String ACTION_REGISTRATION_RESPONSE = "com.cubegames.slava.cubegame.api.action.REGISTRATION_RESPONSE";
    public static final String ACTION_PING = "com.cubegames.slava.cubegame.api.action.PING";
    public static final String ACTION_PING_RESPONSE = "com.cubegames.slava.cubegame.api.action.PING_RESPONSE";
    public static final String ACTION_GET_GAME_MAP_LIST = "com.cubegames.slava.cubegame.api.action.GET_MAP_LIST";
    public static final String ACTION_LIST_RESPONSE = "com.cubegames.slava.cubegame.api.action.LIST_RESPONSE";
    public static final String ACTION_GET_GAME_LIST = "com.cubegames.slava.cubegame.api.action.GET_GAME_LIST";
    public static final String ACTION_GET_GAME_INSTANCE_LIST = "com.cubegames.slava.cubegame.api.action.GET_GAME_INSTANCE_LIST";
    public static final String ACTION_GET_PLAYER_LIST = "com.cubegames.slava.cubegame.api.action.GET_PLAYER_LIST";
    public static final String ACTION_PLAYER_LIST_RESPONSE = "com.cubegames.slava.cubegame.api.action.PLAYER_LIST_RESPONSE";
    public static final String ACTION_GET_MAP_IMAGE = "com.cubegames.slava.cubegame.api.action.GET_MAP_IMAGE";
    public static final String ACTION_MAP_IMAGE_RESPONSE = "com.cubegames.slava.cubegame.api.action.MAP_IMAGE_RESPONSE";
    public static final String ACTION_UPLOAD_MAP_IMAGE = "com.cubegames.slava.cubegame.api.action.UPLOAD_MAP_IMAGE";
    public static final String ACTION_UPLOAD_IMAGE_RESPONSE = "com.cubegames.slava.cubegame.api.action.UPLOAD_IMAGE_RESPONSE";
    public static final String ACTION_DELETE_ENTITY = "com.cubegames.slava.cubegame.api.action.DELETE_ENTITY";
    public static final String ACTION_DELETE_ENTITY_RESPONSE = "com.cubegames.slava.cubegame.api.action.DELETE_ENTITY_RESPONSE";
    public static final String ACTION_SAVE_ENTITY = "com.cubegames.slava.cubegame.api.action.SAVE_ENTITY";
    public static final String ACTION_SAVE_ENTITY_RESPONSE = "com.cubegames.slava.cubegame.api.action.SAVE_ENTITY_RESPONSE";
    public static final String ACTION_FINISH_GAME_INSTANCE = "com.cubegames.slava.cubegame.api.action.FINISH_GAME_INSTANCE";
    public static final String ACTION_FINISH_GAME_INSTANCE_RESPONSE = "com.cubegames.slava.cubegame.api.action.FINISH_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_START_GAME_INSTANCE = "com.cubegames.slava.cubegame.api.action.START_GAME_INSTANCE";
    public static final String ACTION_START_GAME_INSTANCE_RESPONSE = "com.cubegames.slava.cubegame.api.action.START_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_REMOVE_CHILD = "com.cubegames.slava.cubegame.api.action.ACTION_REMOVE_CHILD";
    public static final String ACTION_ACTION_REMOVE_CHILD_RESPONSE = "com.cubegames.slava.cubegame.api.action.ACTION_REMOVE_CHILD_RESPONSE";
    public static final String ACTION_ADD_CHILD = "com.cubegames.slava.cubegame.api.action.ACTION_ADD_CHILD";
    public static final String ACTION_ACTION_ADD_CHILD_RESPONSE = "com.cubegames.slava.cubegame.api.action.ACTION_ADD_CHILD_RESPONSE";

    public static final String EXTRA_USER_NAME = "USER_NAME";
    public static final String EXTRA_USER_PASS = "USER_PASS";
    public static final String EXTRA_LOGIN_RESPONSE_OBJECT = "LOGIN_RESPONSE_OBJECT";
    public static final String EXTRA_REGISTRATION_PARAMS_OBJECT = "LOGIN_RESPONSE_OBJECT";
    public static final String EXTRA_REGISTRATION_RESPONSE_TEXT = "REGISTRATION_RESPONSE_TEXT";
    public static final String EXTRA_BOOLEAN_RESULT = "BOOLEAN_RESULT";
    public static final String EXTRA_GAME_MAP_OBJECT = "GAME_MAP_OBJECT";
    public static final String EXTRA_GAME_MAP_FILE = "GAME_MAP_FILE";
    public static final String EXTRA_GAME_MAP_LIST = "GAME_MAP_LIST";
    public static final String EXTRA_GAME_INSTANCE_LIST = "GAME_INSTANCE_LIST";
    public static final String EXTRA_GAME_INSTANCE = "GAME_INSTANCE";
    public static final String EXTRA_GAME_LIST = "GAME_LIST";
    public static final String EXTRA_PLAYER_LIST = "PLAYER_LIST";
    public static final String EXTRA_ENTITY_OBJECT = "ENTITY_OBJECT";
    public static final String EXTRA_ERROR_OBJECT = "ERROR_OBJECT";
    public static final String EXTRA_RESPONSE_ACTION = "RESPONSE_ACTION";
    public static final String EXTRA_PARENT_ID = "PARENT_ID";
    public static final String EXTRA_CHILD_NAME = "CHILD_NAME";
    public static final String EXTRA_CHILD_INDEX = "CHILD_INDEX";

    public RestApiService() {
        super("RestApiService");
    }

    public static void startActionLogin(Context context, String userName, String userPass) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(EXTRA_USER_NAME, userName);
        intent.putExtra(EXTRA_USER_PASS, userPass);
        context.startService(intent);
    }

    public static void startActionRelogin(Context context, String userName, String userPass) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_RELOGIN);
        intent.putExtra(EXTRA_USER_NAME, userName);
        intent.putExtra(EXTRA_USER_PASS, userPass);
        context.startService(intent);
    }

    public static void startActionPing(Context context) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_PING);
        context.startService(intent);
    }

    public static void startActionRegistration(Context context, UserEntity regParams) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_REGISTRATION);
        intent.putExtra(EXTRA_REGISTRATION_PARAMS_OBJECT, regParams);

        context.startService(intent);
    }

    public static void startActionGetList(Context context, String action) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    public static void startActionGetMapImage(Context context, GameMap map) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_GET_MAP_IMAGE);
        intent.putExtra(EXTRA_GAME_MAP_OBJECT, map);

        context.startService(intent);
    }

    public static void startActionUploadMapImage(Context context, GameMap map, String fileName) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_UPLOAD_MAP_IMAGE);
        intent.putExtra(EXTRA_GAME_MAP_OBJECT, map);
        intent.putExtra(EXTRA_GAME_MAP_FILE, fileName);

        context.startService(intent);
    }

    public static void startActionDeleteEntity(Context context, BasicNamedDbEntity item) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_DELETE_ENTITY);
        intent.putExtra(EXTRA_ENTITY_OBJECT, item);

        context.startService(intent);
    }

    public static void startActionSaveEntity(Context context, BasicNamedDbEntity item, String responseAction) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_SAVE_ENTITY);
        intent.putExtra(EXTRA_ENTITY_OBJECT, item);
        intent.putExtra(EXTRA_RESPONSE_ACTION, responseAction);

        context.startService(intent);
    }

    public static void startActionFinishGameInstance(Context context, GameInstance instance) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_FINISH_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);

        context.startService(intent);
    }

    public static void startActionStartGameInstance(Context context, StartNewGameRequest request) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_START_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, request);

        context.startService(intent);
    }

    public static void startActionRemoveChild(Context context, String parentId, String childName, int childIndex) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_REMOVE_CHILD);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        intent.putExtra(EXTRA_CHILD_NAME, childName);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        context.startService(intent);
    }

    public static void startActionAddChild(Context context, String parentId, String childName, Parcelable childEntity) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_ADD_CHILD);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        intent.putExtra(EXTRA_CHILD_NAME, childName);
        intent.putExtra(EXTRA_ENTITY_OBJECT, childEntity);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOGIN.equals(action)) {
                final String userName = intent.getStringExtra(EXTRA_USER_NAME);
                final String userPass = intent.getStringExtra(EXTRA_USER_PASS);
                handleActionLogin(userName, userPass);
            }
            if (ACTION_RELOGIN.equals(action)) {
                final String userName = intent.getStringExtra(EXTRA_USER_NAME);
                final String userPass = intent.getStringExtra(EXTRA_USER_PASS);
                handleActionRelogin(userName, userPass);
            }
            else if (ACTION_REGISTRATION.equals(action)) {
                final UserEntity params = intent.getParcelableExtra(EXTRA_REGISTRATION_PARAMS_OBJECT);
                handleActionRegistration(params);
            }
            else if (ACTION_PING.equals(action)) {
                handleActionPing();
            }
            else if (ACTION_GET_GAME_MAP_LIST.equals(action)) {
                handleActionGetMapList();
            }
            else if (ACTION_GET_GAME_LIST.equals(action)) {
                handleActionGetGameList();
            }
            else if (ACTION_GET_GAME_INSTANCE_LIST.equals(action)) {
                handleActionGetGameInstanceList();
            }
            else if (ACTION_GET_PLAYER_LIST.equals(action)) {
                handleActionGetPlayerList();
            }
            else if (ACTION_GET_MAP_IMAGE.equals(action)) {
                final GameMap map = intent.getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
                handleActionGetMapImage(map);
            }
            else if (ACTION_UPLOAD_MAP_IMAGE.equals(action)) {
                final GameMap map = intent.getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
                final String fileName = intent.getStringExtra(EXTRA_GAME_MAP_FILE);
                handleActionUploadMapImage(map, fileName);
            }
            else if (ACTION_DELETE_ENTITY.equals(action)) {
                final BasicNamedDbEntity item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionDeleteEntity(item);
            }
            else if (ACTION_SAVE_ENTITY.equals(action)) {
                final BasicNamedDbEntity item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                String responseAction = intent.getStringExtra(EXTRA_RESPONSE_ACTION);
                handleActionSaveEntity(item, responseAction);
            }
            else if (ACTION_FINISH_GAME_INSTANCE.equals(action)) {
                final GameInstance item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionFinishGameInstance(item);
            }
            else if (ACTION_START_GAME_INSTANCE.equals(action)) {
                final StartNewGameRequest item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionStartGameInstance(item);
            }
            else if (ACTION_REMOVE_CHILD.equals(action)) {
                final String parentId = intent.getStringExtra(EXTRA_PARENT_ID);
                final String childName = intent.getStringExtra(EXTRA_CHILD_NAME);
                final int childIndex = intent.getIntExtra(EXTRA_CHILD_INDEX, -1);
                handleActionRemoveChild(parentId, childName, childIndex);
            }
            else if (ACTION_ADD_CHILD.equals(action)) {
                final String parentId = intent.getStringExtra(EXTRA_PARENT_ID);
                final String childName = intent.getStringExtra(EXTRA_CHILD_NAME);
                final Parcelable childEntity = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionAddChild(parentId, childName, childEntity);
            }

        }
    }

    private void sendResponseIntent(String action, Bundle params){
        Intent responseIntent = new Intent();
        responseIntent.setAction(action);
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT);
        responseIntent.putExtras(params);
        sendBroadcast(responseIntent);
    }

    private void handleActionLogin(String userName, String userPass) {
        AuthToken response;
        String message = "";

        try {
            response = new LoginRequest(userName, userPass, this).doLogin();
        }
        catch (WebServiceException e) {
            //TODO: return error object
            response = new AuthToken((String)null);
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putParcelable(EXTRA_LOGIN_RESPONSE_OBJECT, response);
        sendResponseIntent(ACTION_LOGIN_RESPONSE, params);
    }

    private void handleActionRelogin(String userName, String userPass) {
        AuthToken response;
        String message = "";

        try {
            response = new LoginRequest(userName, userPass, this).doLogin();
        }
        catch (WebServiceException e) {
            //TODO: return error object
            response = new AuthToken((String)null);
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putParcelable(EXTRA_LOGIN_RESPONSE_OBJECT, response);
        sendResponseIntent(ACTION_RELOGIN_RESPONSE, params);
    }

    private void handleActionRegistration(UserEntity regParams) {
        String message = "";

        try {
            new RegistrationRequest(regParams, this).doRegister();
        } catch (WebServiceException e) {
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putString(EXTRA_REGISTRATION_RESPONSE_TEXT, message);
        sendResponseIntent(ACTION_REGISTRATION_RESPONSE, params);
    }

    private void handleActionPing() {
        Bundle params = new Bundle();
        params.putBoolean(EXTRA_BOOLEAN_RESULT, new PingRequest(this).doPing());
        sendResponseIntent(ACTION_PING_RESPONSE, params);
    }

    private void handleActionGetMapList(){
        String message = "";
        ArrayList<GameMap> mapList = null;
        try {
            mapList = new ArrayList<>(new GameMapController(this).getResponseList());
        }
        catch (WebServiceException e) {
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putParcelableArrayList(EXTRA_GAME_MAP_LIST, mapList);

        sendResponseIntent(ACTION_LIST_RESPONSE, params);
    }

    private void handleActionGetGameList(){
        String message = "";
        ArrayList<Game> mapList = null;

        try {
            mapList = new ArrayList<>(new GameController(this).getResponseList());
        }
        catch (WebServiceException e) {
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putParcelableArrayList(EXTRA_GAME_LIST, mapList);

        sendResponseIntent(ACTION_LIST_RESPONSE, params);
    }

    private void handleActionGetGameInstanceList(){
        String message = "";
        ArrayList<GameInstance> mapList = null;

        try {
            mapList = new ArrayList<>(new GameInstanceController(this).getResponseList());
        }
        catch (WebServiceException e) {
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putParcelableArrayList(EXTRA_GAME_INSTANCE_LIST, mapList);

        sendResponseIntent(ACTION_LIST_RESPONSE, params);
    }

    private void handleActionGetPlayerList(){
        String message = "";
        ArrayList<DbPlayer> mapList = null;

        try {
            mapList = new ArrayList<>(new DBPlayerController(this).getResponseList());
        }
        catch (WebServiceException e) {
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putParcelableArrayList(EXTRA_PLAYER_LIST, mapList);

        sendResponseIntent(ACTION_LIST_RESPONSE, params);
    }

    private void handleActionGetMapImage(GameMap map) {
        byte[] mapImage = null;
        ErrorEntity error = null;

        try {
            mapImage = new GameMapController(this).getMapImage(map);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        map.setBinaryData(mapImage);
        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);
        params.putParcelable(EXTRA_GAME_MAP_OBJECT, map);

        sendResponseIntent(ACTION_MAP_IMAGE_RESPONSE, params);
    }

    private void handleActionUploadMapImage(GameMap map, String fileName) {
        ErrorEntity error = null;

        try {
            new GameMapController(getApplicationContext()).uploadMapImage(map, fileName);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);

        sendResponseIntent(ACTION_UPLOAD_IMAGE_RESPONSE, params);
    }

    private void handleActionDeleteEntity(BasicNamedDbEntity item) {
        ErrorEntity error = null;

        try {
            item.getController(getApplicationContext()).delete(item);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);

        sendResponseIntent(ACTION_DELETE_ENTITY_RESPONSE, params);
    }

    private void handleActionSaveEntity(BasicNamedDbEntity item, String responseAction) {
        ErrorEntity error = null;
        BasicEntity result = null;
        try {
            result = item.getController(getApplicationContext()).update(item);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);
        else
            params.putParcelable(EXTRA_ENTITY_OBJECT, (BasicNamedDbEntity)result);

        sendResponseIntent(responseAction, params);
    }

    private void handleActionFinishGameInstance(GameInstance item) {
        ErrorEntity error = null;

        try {
            new GameInstanceController(getApplicationContext()).finishInstance(item);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);

        sendResponseIntent(ACTION_FINISH_GAME_INSTANCE_RESPONSE, params);
    }

    private void handleActionStartGameInstance(StartNewGameRequest item) {
        ErrorEntity error = null;
        GameInstanceStartedResponse result = null;

        try {
            result =
            new GameInstanceController(getApplicationContext()).startNewInstance(item);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);
        else {
            params.putParcelable(EXTRA_ENTITY_OBJECT, result.getInstance());
        }

        sendResponseIntent(ACTION_START_GAME_INSTANCE_RESPONSE, params);
    }

    private void handleActionRemoveChild(String parentId, String childName, int childIndex) {
        ErrorEntity error = null;

        try {
            new GameController(getApplicationContext()).removeChild(parentId, childName, childIndex);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);
        else
            params.putInt(EXTRA_CHILD_INDEX, childIndex);

        sendResponseIntent(ACTION_ACTION_REMOVE_CHILD_RESPONSE, params);
    }

    private void handleActionAddChild(String parentId, String childName, Parcelable childEntity) {
        ErrorEntity error = null;

        try {
            new GameController(getApplicationContext()).addChild(parentId, childName, childEntity);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);
        else
            params.putParcelable(EXTRA_ENTITY_OBJECT, childEntity);

        sendResponseIntent(ACTION_ACTION_ADD_CHILD_RESPONSE, params);
    }

}
