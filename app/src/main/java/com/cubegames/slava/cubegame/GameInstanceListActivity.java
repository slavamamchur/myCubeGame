package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.GameInstance;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_FINISH_GAME_INSTANCE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_LIST_RESPONSE;

public class GameInstanceListActivity extends BaseListActivity<GameInstance> {

    private static final String STATE_FIELD_NAME = "state";
    private static final String PLAYERS_FIELD_NAME = "players";
    private static final String STARTED_FIELD_NAME = "startedDate";
    private static final String LAST_USED_FIELD_NAME = "lastUsedDate";

    private static final String FINISH_GAME_TAG = "FINISH_GAME";

    private static final ArrayList<DBColumnInfo> GAME_INSTANCE_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Name", 48, DBColumnInfo.ColumnType.COLUMN_REFERENCE, GameInstance.class.getField(NAME_FIELD_NAME), EDIT_ENTITY_TAG));
            add(new DBColumnInfo("State", 8, DBColumnInfo.ColumnType.COLUMN_TEXT, GameInstance.class.getDeclaredField(STATE_FIELD_NAME), null));
            add(new DBColumnInfo("# of Players", 11, DBColumnInfo.ColumnType.COLUMN_TEXT, GameInstance.class.getDeclaredField(PLAYERS_FIELD_NAME), null));
            add(new DBColumnInfo("Started", 13, DBColumnInfo.ColumnType.COLUMN_TEXT, GameInstance.class.getDeclaredField(STARTED_FIELD_NAME), null));
            add(new DBColumnInfo("Last used", 13, DBColumnInfo.ColumnType.COLUMN_TEXT, GameInstance.class.getDeclaredField(LAST_USED_FIELD_NAME), null));
            add(new DBColumnInfo("Finish", 7, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, FINISH_GAME_TAG));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    @Override
    protected ArrayList<DBColumnInfo> getColumnInfo() {
        return GAME_INSTANCE_LIST_COLUMN_INFO;
    }
    @Override
    protected String getListAction() {
        return RestApiService.ACTION_GET_GAME_INSTANCE_LIST;
    }
    @Override
    protected String getListResponseAction() {
        return ACTION_LIST_RESPONSE;
    }
    @Override
    protected String getListResponseExtra() {
        return RestApiService.EXTRA_GAME_INSTANCE_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return RestApiService.EXTRA_ENTITY_OBJECT;
    }
    @Override
    protected Class<?> getDetailsActivityClass() {
        return GameInstanceActivity.class;
    }
    @Override
    protected int getCaptionResource() {
        return R.string.game_instance_list_title;
    }

    @Override
    protected GameInstance getNewItem() {
        return new GameInstance();
    }
    @Override
    protected String getNewItemActionName() {
        return null;
    }

    @Override
    protected boolean isUserButtonEnabled(String tag, BasicNamedDbEntity item) {
        return !((GameInstance)item).getState().equals(GameInstance.State.FINISHED);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_maps_list).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void doUserAction(GameInstance item, String tag) {
        showProgress();

        RestApiService.startActionFinishGameInstance(getApplicationContext(), item);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_FINISH_GAME_INSTANCE_RESPONSE);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_FINISH_GAME_INSTANCE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null){

                showProgress();
                getData();
            }
            else {
                showError(error);
            }

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }
}
