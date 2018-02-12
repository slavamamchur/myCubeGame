package com.sadgames.dicegame.rest_api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.sadgames.dicegame.rest_api.model.entities.AuthTokenEntity;
import com.sadgames.dicegame.rest_api.model.entities.BasicEntity;
import com.sadgames.dicegame.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.dicegame.rest_api.model.entities.DbPlayerEntity;
import com.sadgames.dicegame.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameMapEntity;
import com.sadgames.dicegame.rest_api.model.entities.UserEntity;
import com.sadgames.dicegame.rest_api.model.responses.GameInstanceStartedResponse;
import com.sadgames.gl3d_engine.SysUtilsWrapperInterface;
import com.sadgames.sysutils.platforms.android.AndroidDiceGameUtilsWrapper;

import java.util.ArrayList;

public class RestApiService extends IntentService {
    @SuppressWarnings("unused")
    private final String TAG = "RestApiService";

    public static final String ACTION_LOGIN = "com.sadgames.dicegame.api.action.LOGIN";
    public static final String ACTION_RELOGIN = "com.sadgames.dicegame.api.action.RELOGIN";
    public static final String ACTION_LOGIN_RESPONSE = "com.sadgames.dicegame.api.action.LOGIN_RESPONSE";
    public static final String ACTION_RELOGIN_RESPONSE = "com.sadgames.dicegame.api.action.RELOGIN_RESPONSE";
    public static final String ACTION_REGISTRATION = "com.sadgames.dicegame.api.action.REGISTRATION";
    public static final String ACTION_REGISTRATION_RESPONSE = "com.sadgames.dicegame.api.action.REGISTRATION_RESPONSE";
    public static final String ACTION_PING = "com.sadgames.dicegame.api.action.PING";
    public static final String ACTION_PING_RESPONSE = "com.sadgames.dicegame.api.action.PING_RESPONSE";
    public static final String ACTION_GET_GAME_MAP_LIST = "com.sadgames.dicegame.api.action.GET_MAP_LIST";
    public static final String ACTION_LIST_RESPONSE = "com.sadgames.dicegame.api.action.LIST_RESPONSE";
    public static final String ACTION_GET_GAME_LIST = "com.sadgames.dicegame.api.action.GET_GAME_LIST";
    public static final String ACTION_GET_GAME_INSTANCE_LIST = "com.sadgames.dicegame.api.action.GET_GAME_INSTANCE_LIST";
    public static final String ACTION_GET_PLAYER_LIST = "com.sadgames.dicegame.api.action.GET_PLAYER_LIST";
    public static final String ACTION_PLAYER_LIST_RESPONSE = "com.sadgames.dicegame.api.action.PLAYER_LIST_RESPONSE";
    public static final String ACTION_GET_MAP_IMAGE = "com.sadgames.dicegame.api.action.GET_MAP_IMAGE";
    public static final String ACTION_MAP_IMAGE_RESPONSE = "com.sadgames.dicegame.api.action.MAP_IMAGE_RESPONSE";
    public static final String ACTION_UPLOAD_MAP_IMAGE = "com.sadgames.dicegame.api.action.UPLOAD_MAP_IMAGE";
    public static final String ACTION_UPLOAD_IMAGE_RESPONSE = "com.sadgames.dicegame.api.action.UPLOAD_IMAGE_RESPONSE";
    public static final String ACTION_DELETE_ENTITY = "com.sadgames.dicegame.api.action.DELETE_ENTITY";
    public static final String ACTION_DELETE_ENTITY_RESPONSE = "com.sadgames.dicegame.api.action.DELETE_ENTITY_RESPONSE";
    public static final String ACTION_SAVE_ENTITY = "com.sadgames.dicegame.api.action.SAVE_ENTITY";
    public static final String ACTION_SAVE_ENTITY_RESPONSE = "com.sadgames.dicegame.api.action.SAVE_ENTITY_RESPONSE";
    public static final String ACTION_FINISH_GAME_INSTANCE = "com.sadgames.dicegame.api.action.FINISH_GAME_INSTANCE";
    public static final String ACTION_FINISH_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.FINISH_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_START_GAME_INSTANCE = "com.sadgames.dicegame.api.action.START_GAME_INSTANCE";
    public static final String ACTION_START_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.START_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_RESTART_GAME_INSTANCE = "com.sadgames.dicegame.api.action.RESTART_GAME_INSTANCE";
    public static final String ACTION_RESTART_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.RESTART_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_MOOVE_GAME_INSTANCE = "com.sadgames.dicegame.api.action.MOOVE_GAME_INSTANCE";
    public static final String ACTION_MOOVE_GAME_INSTANCE_RESPONSE = "com.sadgames.dicegame.api.action.MOOVE_GAME_INSTANCE_RESPONSE";
    public static final String ACTION_REMOVE_CHILD = "com.sadgames.dicegame.api.action.ACTION_REMOVE_CHILD";
    public static final String ACTION_ACTION_REMOVE_CHILD_RESPONSE = "com.sadgames.dicegame.api.action.ACTION_REMOVE_CHILD_RESPONSE";
    public static final String ACTION_ADD_CHILD = "com.sadgames.dicegame.api.action.ACTION_ADD_CHILD";
    public static final String ACTION_ACTION_ADD_CHILD_RESPONSE = "com.sadgames.dicegame.api.action.ACTION_ADD_CHILD_RESPONSE";
    public static final String ACTION_ACTION_SHOW_TURN_INFO = "com.sadgames.dicegame.api.action.ACTION_SHOW_TURN_INFO";

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
    public static final String EXTRA_DICE_VALUE = "DICE_VALUE";

    private SysUtilsWrapperInterface sysUtilsWrapper;

    public RestApiService() {
        super("RestApiService");

        sysUtilsWrapper = new AndroidDiceGameUtilsWrapper(this);
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

    public static void startActionGetMapImage(Context context, GameMapEntity map) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_GET_MAP_IMAGE);
        intent.putExtra(EXTRA_GAME_MAP_OBJECT, map);

        context.startService(intent);
    }

    public static void startActionUploadMapImage(Context context, GameMapEntity map, String fileName) {
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

    public static void startActionFinishGameInstance(Context context, GameInstanceEntity instance) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_FINISH_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);

        context.startService(intent);
    }

    public static void startActionStartGameInstance(Context context, StartNewGameRequestParam request) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_START_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, request);

        context.startService(intent);
    }

    public static void startActionRestartGameInstance(Context context, GameInstanceEntity instance) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_RESTART_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);

        context.startService(intent);
    }

    public static void startActionMooveGameInstance(Context context, GameInstanceEntity instance) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_MOOVE_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);

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
                final GameMapEntity map = intent.getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
                handleActionGetMapImage(map);
            }
            else if (ACTION_UPLOAD_MAP_IMAGE.equals(action)) {
                final GameMapEntity map = intent.getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
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
                final GameInstanceEntity item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionFinishGameInstance(item);
            }
            else if (ACTION_MOOVE_GAME_INSTANCE.equals(action)) {
                final GameInstanceEntity item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionMooveGameInstance(item);
            }
            else if (ACTION_START_GAME_INSTANCE.equals(action)) {
                final StartNewGameRequestParam item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionStartGameInstance(item);
            }
            else if (ACTION_RESTART_GAME_INSTANCE.equals(action)) {
                final GameInstanceEntity item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                handleActionReStartGameInstance(item);
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
        AuthTokenEntity response;
        ErrorEntity error = null;

        try {
            response = new LoginRequest(userName, userPass, sysUtilsWrapper).doLogin();
        }
        catch (WebServiceException e) {
            response = new AuthTokenEntity((String)null);
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), 404);
        }
        catch (Exception e) {
            response = new AuthTokenEntity((String)null);
            error = new ErrorEntity(e.getMessage(), 404);
        }

        Bundle params = new Bundle();
        params.putParcelable(EXTRA_ERROR_OBJECT, error);
        params.putParcelable(EXTRA_LOGIN_RESPONSE_OBJECT, response);
        sendResponseIntent(ACTION_LOGIN_RESPONSE, params);
    }

    private void handleActionRelogin(String userName, String userPass) {
        AuthTokenEntity response;
        String message = "";

        try {
            response = new LoginRequest(userName, userPass, sysUtilsWrapper).doLogin();
        }
        catch (WebServiceException e) {
            //TODO: return error object
            response = new AuthTokenEntity((String)null);
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }
        catch (Exception e) {
            response = new AuthTokenEntity((String)null);
            message = e.getMessage();
        }

        Bundle params = new Bundle();
        params.putParcelable(EXTRA_LOGIN_RESPONSE_OBJECT, response);
        sendResponseIntent(ACTION_RELOGIN_RESPONSE, params);
    }

    private void handleActionRegistration(UserEntity regParams) {
        String message = "";

        try {
            new RegistrationRequest(regParams, sysUtilsWrapper).doRegister();
        } catch (WebServiceException e) {
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }
        catch (Exception e) {
            message = e.getMessage();
        }

        Bundle params = new Bundle();
        params.putString(EXTRA_REGISTRATION_RESPONSE_TEXT, message);
        sendResponseIntent(ACTION_REGISTRATION_RESPONSE, params);
    }

    private void handleActionPing() {
        boolean result = false;

        try {
            result = new PingRequest(sysUtilsWrapper).doPing();
        }
        catch (Exception e) {
            result = false;
        }

        Bundle params = new Bundle();
        params.putBoolean(EXTRA_BOOLEAN_RESULT, result);
        sendResponseIntent(ACTION_PING_RESPONSE, params);
    }

    private void handleActionGetMapList(){
        String message = "";
        ArrayList<GameMapEntity> mapList = null;
        try {
            mapList = new ArrayList<>(new GameMapController(sysUtilsWrapper).getResponseList());
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
        ArrayList<GameEntity> mapList = null;

        try {
            mapList = new ArrayList<>(new GameController(sysUtilsWrapper).getResponseList());
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
        ArrayList<GameInstanceEntity> mapList = null;

        try {
            mapList = new ArrayList<>(new GameInstanceController(sysUtilsWrapper).getResponseList());
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
        ArrayList<DbPlayerEntity> mapList = null;

        try {
            mapList = new ArrayList<>(new DBPlayerController(sysUtilsWrapper).getResponseList());
        }
        catch (WebServiceException e) {
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }

        Bundle params = new Bundle();
        params.putParcelableArrayList(EXTRA_PLAYER_LIST, mapList);

        sendResponseIntent(ACTION_LIST_RESPONSE, params);
    }

    private void handleActionGetMapImage(GameMapEntity map) {
        ErrorEntity error = null;

        try {
            new GameMapController(sysUtilsWrapper).saveMapImage(map);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);
        params.putParcelable(EXTRA_GAME_MAP_OBJECT, map);

        sendResponseIntent(ACTION_MAP_IMAGE_RESPONSE, params);
    }

    private void handleActionUploadMapImage(GameMapEntity map, String fileName) {
        ErrorEntity error = null;

        try {
            new GameMapController(sysUtilsWrapper).uploadMapImage(map, fileName);
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
            item.getController(sysUtilsWrapper).delete(item);
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
            result = item.getController(sysUtilsWrapper).update(item);
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

    private void handleActionFinishGameInstance(GameInstanceEntity item) {
        ErrorEntity error = null;

        try {
            new GameInstanceController(sysUtilsWrapper).finishInstance(item);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);

        sendResponseIntent(ACTION_FINISH_GAME_INSTANCE_RESPONSE, params);
    }

    private void handleActionReStartGameInstance(GameInstanceEntity item) {
        ErrorEntity error = null;

        try {
            new GameInstanceController(sysUtilsWrapper).restartInstance(item);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);

        sendResponseIntent(ACTION_RESTART_GAME_INSTANCE_RESPONSE, params);
    }

    private void handleActionStartGameInstance(StartNewGameRequestParam item) {
        ErrorEntity error = null;
        GameInstanceStartedResponse result = null;

        try {
            result = new GameInstanceController(sysUtilsWrapper).startNewInstance(item);
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

    private void handleActionMooveGameInstance(GameInstanceEntity item) {
        ErrorEntity error = null;
        GameInstanceEntity result = null;

        try {
            result = new GameInstanceController(sysUtilsWrapper).makeTurn(item);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putParcelable(EXTRA_ERROR_OBJECT, error);
        else {
            params.putParcelable(EXTRA_ENTITY_OBJECT, result);
        }

        sendResponseIntent(ACTION_MOOVE_GAME_INSTANCE_RESPONSE, params);
    }

    private void handleActionRemoveChild(String parentId, String childName, int childIndex) {
        ErrorEntity error = null;

        try {
            new GameController(sysUtilsWrapper).removeChild(parentId, childName, childIndex);
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
            new GameController(sysUtilsWrapper).addChild(parentId, childName, childEntity);
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
