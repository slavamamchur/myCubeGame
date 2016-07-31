package com.cubegames.slava.cubegame.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cubegames.slava.cubegame.model.AuthToken;
import com.cubegames.slava.cubegame.model.UserEntity;

public class RestApiService extends IntentService {
    @SuppressWarnings("unused")
    private final String TAG = "RestApiService";

    private static final String ACTION_LOGIN = "com.cubegames.slava.cubegame.api.action.LOGIN";
    public static final String ACTION_LOGIN_RESPONSE = "com.cubegames.slava.cubegame.api.action.LOGIN_RESPONSE";
    private static final String ACTION_REGISTRATION = "com.cubegames.slava.cubegame.api.action.REGISTRATION";
    public static final String ACTION_REGISTRATION_RESPONSE = "com.cubegames.slava.cubegame.api.action.REGISTRATION_RESPONSE";
    private static final String ACTION_PING = "com.cubegames.slava.cubegame.api.action.PING";
    public static final String ACTION_PING_RESPONSE = "com.cubegames.slava.cubegame.api.action.PING_RESPONSE";

    public static final String EXTRA_USER_NAME = "USER_NAME";
    public static final String EXTRA_USER_PASS = "USER_PASS";
    public static final String EXTRA_LOGIN_RESPONSE_OBJECT = "LOGIN_RESPONSE_OBJECT";
    public static final String EXTRA_REGISTRATION_PARAMS_OBJECT = "LOGIN_RESPONSE_OBJECT";
    public static final String EXTRA_REGISTRATION_RESPONSE_TEXT = "REGISTRATION_RESPONSE_TEXT";
    public static final String EXTRA_AUTH_TOKEN = "AUTH_TOKEN";
    public static final String EXTRA_BOOLEAN_RESULT = "BOOLEAN_RESULT";

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

    public static void startActionPing(Context context, String authToken) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_PING);
        intent.putExtra(EXTRA_AUTH_TOKEN, authToken);
        context.startService(intent);
    }

    public static void startActionRegistration(Context context, UserEntity regParams) {
        Intent intent = new Intent(context, RestApiService.class);
        intent.setAction(ACTION_REGISTRATION);
        intent.putExtra(EXTRA_REGISTRATION_PARAMS_OBJECT, regParams);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOGIN.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_USER_NAME);
                final String param2 = intent.getStringExtra(EXTRA_USER_PASS);
                handleActionLogin(param1, param2);
            }
            else if (ACTION_REGISTRATION.equals(action)) {
                final UserEntity params = intent.getParcelableExtra(EXTRA_REGISTRATION_PARAMS_OBJECT);
                handleActionRegistration(params);
            }
            else if (ACTION_PING.equals(action)) {
                final String authToken = intent.getStringExtra(EXTRA_AUTH_TOKEN);
                handleActionPing(authToken);
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
        try {
            response = new LoginRequest(userName, userPass, this).doLogin();
        }
        catch (WebServiceException e) {
            //TODO: return error object
            response = new AuthToken((String)null);
        }

        Bundle params = new Bundle();
        params.putParcelable(EXTRA_LOGIN_RESPONSE_OBJECT, response);
        sendResponseIntent(ACTION_LOGIN_RESPONSE, params);
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

    private void handleActionPing(String authToken) {
        Bundle params = new Bundle();
        params.putBoolean(EXTRA_BOOLEAN_RESULT, new PingRequest(authToken, this).doPing());
        sendResponseIntent(ACTION_PING_RESPONSE, params);
    }
}
