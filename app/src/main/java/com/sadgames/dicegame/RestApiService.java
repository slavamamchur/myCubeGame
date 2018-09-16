package com.sadgames.dicegame;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.sadgames.dicegame.logic.server.rest_api.WebServiceException;
import com.sadgames.dicegame.logic.server.rest_api.controller.DBPlayerController;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameController;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameInstanceController;
import com.sadgames.dicegame.logic.server.rest_api.controller.GameMapController;
import com.sadgames.dicegame.logic.server.rest_api.controller.LoginRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.PingRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.RegistrationRequest;
import com.sadgames.dicegame.logic.server.rest_api.controller.params.StartNewGameRequestParam;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.AuthTokenEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.BasicEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.DbPlayerEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameMapEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.UserEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.responses.GameInstanceStartedResponse;
import com.sadgames.sysutils.common.SysUtilsWrapperInterface;
import com.sadgames.sysutils.platforms.android.AndroidDiceGameUtilsWrapper;

import java.io.Serializable;
import java.util.ArrayList;

import static com.sadgames.dicegame.logic.client.GameConst.ACTION_ADD_CHILD;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_ADD_CHILD_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_DELETE_ENTITY;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_DELETE_ENTITY_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_FINISH_GAME_INSTANCE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_FINISH_GAME_INSTANCE_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_GET_MAP_IMAGE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LIST_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LOGIN;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LOGIN_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_MAP_IMAGE_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_MOOVE_GAME_INSTANCE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_MOOVE_GAME_INSTANCE_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_PING;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_PING_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_REGISTRATION;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_REGISTRATION_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_RELOGIN;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_RELOGIN_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_REMOVE_CHILD;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_REMOVE_CHILD_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_RESTART_GAME_INSTANCE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_RESTART_GAME_INSTANCE_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_SAVE_ENTITY;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_START_GAME_INSTANCE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_START_GAME_INSTANCE_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_UPLOAD_IMAGE_RESPONSE;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_UPLOAD_MAP_IMAGE;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_BOOLEAN_RESULT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_CHILD_INDEX;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_CHILD_NAME;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_ENTITY_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_ERROR_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_GAME_INSTANCE_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_GAME_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_GAME_MAP_FILE;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_GAME_MAP_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_GAME_MAP_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_LOGIN_RESPONSE_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_PARENT_ID;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_PLAYER_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_REGISTRATION_PARAMS_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_REGISTRATION_RESPONSE_TEXT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_RESPONSE_ACTION;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_USER_NAME;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_USER_PASS;
import static com.sadgames.dicegame.logic.client.GameConst.UserActionType;

public class RestApiService extends IntentService {

    private SysUtilsWrapperInterface sysUtilsWrapper;

    public RestApiService() {
        super(RestApiService.class.getSimpleName());
        sysUtilsWrapper = AndroidDiceGameUtilsWrapper.getInstance(this);
    }


    protected static Intent getIntent(Context context, String action) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(action);
        return intent;
    }

    public static void startActionLogin(Context context, String userName, String userPass) {
        Intent intent = getIntent(context, ACTION_LOGIN);
        intent.putExtra(EXTRA_USER_NAME, userName);
        intent.putExtra(EXTRA_USER_PASS, userPass);
        context.startService(intent);
    }

    public static void startActionRelogin(Context context, String userName, String userPass) {
        Intent intent = getIntent(context, ACTION_RELOGIN);
        intent.putExtra(EXTRA_USER_NAME, userName);
        intent.putExtra(EXTRA_USER_PASS, userPass);
        context.startService(intent);
    }

    public static void startActionPing(Context context) {
        Intent intent = getIntent(context, ACTION_PING);
        context.startService(intent);
    }

    public static void startActionRegistration(Context context, UserEntity regParams) {
        Intent intent = getIntent(context, ACTION_REGISTRATION);
        intent.putExtra(EXTRA_REGISTRATION_PARAMS_OBJECT, regParams);

        context.startService(intent);
    }

    public static void startActionGetList(Context context, String action) {
        Intent intent = getIntent(context, action);
        context.startService(intent);
    }

    public static void startActionGetMapImage(Context context, GameMapEntity map) {
        Intent intent = getIntent(context, ACTION_GET_MAP_IMAGE);
        intent.putExtra(EXTRA_GAME_MAP_OBJECT, map);

        context.startService(intent);
    }

    public static void startActionUploadMapImage(Context context, GameMapEntity map, String fileName) {
        Intent intent = getIntent(context, ACTION_UPLOAD_MAP_IMAGE);
        intent.putExtra(EXTRA_GAME_MAP_OBJECT, map);
        intent.putExtra(EXTRA_GAME_MAP_FILE, fileName);

        context.startService(intent);
    }

    public static void startActionDeleteEntity(Context context, BasicNamedDbEntity item) {
        Intent intent = getIntent(context, ACTION_DELETE_ENTITY);
        intent.putExtra(EXTRA_ENTITY_OBJECT, item);

        context.startService(intent);
    }

    public static void startActionSaveEntity(Context context, BasicNamedDbEntity item, String responseAction) {
        Intent intent = getIntent(context, ACTION_SAVE_ENTITY);
        intent.putExtra(EXTRA_ENTITY_OBJECT, item);
        intent.putExtra(EXTRA_RESPONSE_ACTION, responseAction);

        context.startService(intent);
    }

    public static void startActionFinishGameInstance(Context context, GameInstanceEntity instance) {
        Intent intent = getIntent(context, ACTION_FINISH_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);

        context.startService(intent);
    }

    public static void startActionStartGameInstance(Context context, StartNewGameRequestParam request) {
        Intent intent = getIntent(context, ACTION_START_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, request);

        context.startService(intent);
    }

    public static void startActionRestartGameInstance(Context context, GameInstanceEntity instance) {
        Intent intent = getIntent(context, ACTION_RESTART_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);

        context.startService(intent);
    }

    public static void startActionMoveGameInstance(Context context, GameInstanceEntity instance) {
        Intent intent = getIntent(context, ACTION_MOOVE_GAME_INSTANCE);
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);

        context.startService(intent);
    }

    public static void startActionRemoveChild(Context context, String parentId, String childName, int childIndex) {
        Intent intent = getIntent(context, ACTION_REMOVE_CHILD);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        intent.putExtra(EXTRA_CHILD_NAME, childName);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        context.startService(intent);
    }

    public static void startActionAddChild(Context context, String parentId, String childName, Parcelable childEntity) {
        Intent intent = getIntent(context, ACTION_ADD_CHILD);
        intent.putExtra(EXTRA_PARENT_ID, parentId);
        intent.putExtra(EXTRA_CHILD_NAME, childName);
        intent.putExtra(EXTRA_ENTITY_OBJECT, childEntity);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
            if (intent == null) return;

            UserActionType actionType = UserActionType.values()[ACTION_LIST.indexOf(intent.getAction())];
            switch (actionType) {
                case LOGIN:
                    handleActionLogin(intent.getStringExtra(EXTRA_USER_NAME), intent.getStringExtra(EXTRA_USER_PASS));
                    break;
                case RELOGIN:
                    handleActionRelogin(intent.getStringExtra(EXTRA_USER_NAME), intent.getStringExtra(EXTRA_USER_PASS));
                    break;
                case REGISTRATION:
                    final UserEntity params = (UserEntity) intent.getSerializableExtra(EXTRA_REGISTRATION_PARAMS_OBJECT);
                    handleActionRegistration(params);
                    break;
                case PING:
                    handleActionPing();
                    break;
                case GET_GAME_MAP_LIST:
                    handleActionGetMapList();
                    break;
                case GET_GAME_LIST:
                    handleActionGetGameList();
                    break;
                case GET_GAME_INSTANCE_LIST:
                    handleActionGetGameInstanceList();
                    break;
                case GET_PLAYER_LIST:
                    handleActionGetPlayerList();
                    break;
                case GET_MAP_IMAGE:
                    handleActionGetMapImage((GameMapEntity) intent.getSerializableExtra(EXTRA_GAME_MAP_OBJECT));
                    break;
                case UPLOAD_MAP_IMAGE:
                    handleActionUploadMapImage((GameMapEntity) intent.getSerializableExtra(EXTRA_GAME_MAP_OBJECT), intent.getStringExtra(EXTRA_GAME_MAP_FILE));
                    break;
                case DELETE_ENTITY:
                    handleActionDeleteEntity((BasicNamedDbEntity) intent.getSerializableExtra(EXTRA_ENTITY_OBJECT));
                    break;
                case SAVE_ENTITY:
                    handleActionSaveEntity((BasicNamedDbEntity) intent.getSerializableExtra(EXTRA_ENTITY_OBJECT), intent.getStringExtra(EXTRA_RESPONSE_ACTION));
                    break;
                case FINISH_GAME_INSTANCE:
                    handleActionFinishGameInstance((GameInstanceEntity) intent.getSerializableExtra(EXTRA_ENTITY_OBJECT));
                    break;
                case MOOVE_GAME_INSTANCE:
                    handleActionMooveGameInstance((GameInstanceEntity) intent.getSerializableExtra(EXTRA_ENTITY_OBJECT));
                    break;
                case START_GAME_INSTANCE:
                    handleActionStartGameInstance((StartNewGameRequestParam) intent.getSerializableExtra(EXTRA_ENTITY_OBJECT));
                    break;
                case RESTART_GAME_INSTANCE:
                    handleActionReStartGameInstance((GameInstanceEntity) intent.getSerializableExtra(EXTRA_ENTITY_OBJECT));
                    break;
                case REMOVE_CHILD:
                    handleActionRemoveChild(
                            intent.getStringExtra(EXTRA_PARENT_ID), intent.getStringExtra(EXTRA_CHILD_NAME), intent.getIntExtra(EXTRA_CHILD_INDEX, -1));
                    break;
                case ADD_CHILD:
                    handleActionAddChild(
                            intent.getStringExtra(EXTRA_PARENT_ID), intent.getStringExtra(EXTRA_CHILD_NAME), intent.getSerializableExtra(EXTRA_ENTITY_OBJECT));
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
            response = new AuthTokenEntity(null);
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), 404);
        }
        catch (Exception e) {
            response = new AuthTokenEntity(null);
            error = new ErrorEntity(e.getMessage(), 404);
        }

        Bundle params = new Bundle();
        params.putSerializable(EXTRA_ERROR_OBJECT, error);
        params.putSerializable(EXTRA_LOGIN_RESPONSE_OBJECT, response);
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
            response = new AuthTokenEntity(null);
            message = e.getErrorObject() != null ? e.getErrorObject().getError() : e.getStatusText();
        }
        catch (Exception e) {
            response = new AuthTokenEntity(null);
            message = e.getMessage();
        }

        Bundle params = new Bundle();
        params.putSerializable(EXTRA_LOGIN_RESPONSE_OBJECT, response);
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
        params.putSerializable(EXTRA_GAME_MAP_LIST, mapList);

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
        params.putSerializable(EXTRA_GAME_LIST, mapList);

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
        params.putSerializable(EXTRA_GAME_INSTANCE_LIST, mapList);

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
        params.putSerializable(EXTRA_PLAYER_LIST, mapList);

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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);
        params.putSerializable(EXTRA_GAME_MAP_OBJECT, map);

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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);

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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);

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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);
        else
            params.putSerializable(EXTRA_ENTITY_OBJECT, (BasicNamedDbEntity)result);

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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);

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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);

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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);
        else {
            params.putSerializable(EXTRA_ENTITY_OBJECT, result.getInstance());
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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);
        else {
            params.putSerializable(EXTRA_ENTITY_OBJECT, result);
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
            params.putSerializable(EXTRA_ERROR_OBJECT, error);
        else
            params.putInt(EXTRA_CHILD_INDEX, childIndex);

        sendResponseIntent(ACTION_REMOVE_CHILD_RESPONSE, params);
    }

    private void handleActionAddChild(String parentId, String childName, Serializable childEntity) {
        ErrorEntity error = null;

        try {
            new GameController(sysUtilsWrapper).addChild(parentId, childName, childEntity);
        }
        catch (WebServiceException e) {
            error = e.getErrorObject() != null ? e.getErrorObject() : new ErrorEntity(e.getStatusText(), e.getStatusCode().value());
        }

        Bundle params = new Bundle();
        if(error != null)
            params.putSerializable(EXTRA_ERROR_OBJECT, error);
        else
            params.putSerializable(EXTRA_ENTITY_OBJECT, childEntity);

        sendResponseIntent(ACTION_ADD_CHILD_RESPONSE, params);
    }

}
