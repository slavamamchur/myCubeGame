package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.StartNewGameRequest;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.DBTableFragment.DELETE_ENTITY_TAG;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_GET_GAME_LIST;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_LIST_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_START_GAME_INSTANCE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_LIST;

public class GameListActivity extends BaseListActivity<Game> {

    public static final int START_GAME_INSTANCE_ACTION = 3;
    private static final String START_GAME_INSTANCE_TAG = "START_GAME_INSTANCE";
    private static final String MAP_ID_FIELD_NAME = "mapId";

    private static final ArrayList<DBColumnInfo> GAME_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Name", 30, DBColumnInfo.ColumnType.COLUMN_REFERENCE, Game.class.getField(NAME_FIELD_NAME), EDIT_ENTITY_TAG));
            add(new DBColumnInfo("Map ID", 25, DBColumnInfo.ColumnType.COLUMN_TEXT, Game.class.getDeclaredField(MAP_ID_FIELD_NAME), null));
            add(new DBColumnInfo("Created", 28, DBColumnInfo.ColumnType.COLUMN_TEXT, Game.class.getDeclaredField(CREATED_DATE_FIELD_NAME), null));
            add(new DBColumnInfo("", 5, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, DELETE_ENTITY_TAG, android.R.drawable.ic_delete, null));
            add(new DBColumnInfo("Start", 12, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, START_GAME_INSTANCE_TAG));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    @Override
    protected String getListAction() {
        return ACTION_GET_GAME_LIST;
    }
    @Override
    protected String getListResponseAction() {
        return ACTION_LIST_RESPONSE;
    }
    @Override
    protected String getListResponseExtra() {
        return EXTRA_GAME_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return EXTRA_ENTITY_OBJECT;
    }
    @Override
    protected Class<?> getDetailsActivityClass() {
        return GameActivity.class;
    }
    @Override
    protected Game getNewItem() {
        return new Game();
    }
    @Override
    protected String getNewItemActionName() {
        return null;
    }
    @Override
    protected int getCaptionResource() {
        return R.string.game_list_title;
    }
    @Override
    protected ArrayList<DBColumnInfo> getColumnInfo() {
        return  GAME_LIST_COLUMN_INFO;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_maps_list).setVisible(true);
        menu.findItem(R.id.action_game_instances_list).setVisible(true);
        menu.findItem(R.id.action_dbplayers_list).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_START_GAME_INSTANCE_RESPONSE);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_START_GAME_INSTANCE_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null){

                Intent mIntent = new Intent(getApplicationContext(), GameInstanceActivity.class);
                mIntent.putExtra(getEntityExtra(), intent.getParcelableExtra(EXTRA_ENTITY_OBJECT));
                startActivity(mIntent);
            }
            else {
                showError(error);
            }

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    @Override
    protected void doUserAction(Game item, String tag) {
        Intent mIntent = new Intent(getApplicationContext(), NewGameInstanceActivity.class);
        mIntent.putExtra(getEntityExtra(), item);
        startActivityForResult(mIntent, START_GAME_INSTANCE_ACTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_GAME_INSTANCE_ACTION && resultCode == RESULT_OK && null != data) {
            GameInstance instance = data.getParcelableExtra(getEntityExtra());
            if (instance != null)
                startGameInstance(instance);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void startGameInstance(GameInstance instance){
        showProgress();

        StartNewGameRequest request = new StartNewGameRequest();
        request.setName(instance.getName());
        request.setPlayers(instance.getPlayers());
        request.setGameId(instance.getGame().getId());

        RestApiService.startActionStartGameInstance(getApplicationContext(), request);
    }
}
