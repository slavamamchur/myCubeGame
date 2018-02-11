package com.sadgames.dicegame.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.rest_api.RestApiService;
import com.sadgames.dicegame.rest_api.model.ErrorEntity;
import com.sadgames.dicegame.rest_api.model.Game;
import com.sadgames.dicegame.rest_api.model.GameMap;
import com.sadgames.dicegame.ui.framework.BaseListActivity;
import com.sadgames.dicegame.ui.framework.DBColumnInfo;
import com.sadgames.dicegame.ui.framework.DialogOnClickDelegate;
import com.sadgames.dicegame.ui.framework.InputNameDialogFragment;

import java.util.ArrayList;

import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_GET_GAME_MAP_LIST;
import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_LIST_RESPONSE;
import static com.sadgames.dicegame.rest_api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.sadgames.dicegame.rest_api.RestApiService.EXTRA_GAME_MAP_LIST;
import static com.sadgames.dicegame.ui.framework.DBTableFragment.DELETE_ENTITY_TAG;

public class GameMapsListActivity extends BaseListActivity<GameMap> {
    private static final String ACTION_CREATE_NEW_GAME_RESPONSE = "com.sadgames.dicegame.api.action.ACTION_CREATE_NEW_GAME_RESPONSE";
    private static final String NEW_GAME_TAG = "NEW_GAME";

    private static final ArrayList<DBColumnInfo> MAP_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Name", 50, DBColumnInfo.ColumnType.COLUMN_REFERENCE, GameMap.class.getField(NAME_FIELD_NAME), EDIT_ENTITY_TAG));
            add(new DBColumnInfo("Created", 28, DBColumnInfo.ColumnType.COLUMN_TEXT, GameMap.class.getDeclaredField(CREATED_DATE_FIELD_NAME), null));
            add(new DBColumnInfo("Remove", 10, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, DELETE_ENTITY_TAG));
            add(new DBColumnInfo("New Game", 12, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, NEW_GAME_TAG));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    @Override
    protected ArrayList<DBColumnInfo> getColumnInfo() {
        return MAP_LIST_COLUMN_INFO;
    }
    @Override
    protected String getListAction() {
        return ACTION_GET_GAME_MAP_LIST;
    }
    @Override
    protected String getListResponseAction() {
        return ACTION_LIST_RESPONSE;
    }
    @Override
    protected String getListResponseExtra() {
        return EXTRA_GAME_MAP_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return EXTRA_ENTITY_OBJECT;
    }
    @Override
    protected Class<?> getDetailsActivityClass() {
        return GameMapActivity.class;
    }
    @Override
    protected int getCaptionResource() {
        return R.string.game_map_list_title;
    }

    @Override
    protected GameMap getNewItem() {
        return new GameMap();
    }
    @Override
    protected String getNewItemActionName() {
        return getString(R.string.add_new_map_caption);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_CREATE_NEW_GAME_RESPONSE);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_CREATE_NEW_GAME_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null){

                Intent mIntent = new Intent(getApplicationContext(), GameActivity.class);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_games_list).setVisible(true);
        menu.findItem(R.id.action_game_instances_list).setVisible(true);
        menu.findItem(R.id.action_dbplayers_list).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void doUserAction(final GameMap item, String tag) {
        InputNameDialogFragment dialog = new InputNameDialogFragment();
        dialog.setDelegate(new DialogOnClickDelegate() {
            @Override
            public void doAction(Object result) {
                Game game = new Game();
                game.setName((String)result);
                game.setMapId(item.getId());

                newGame(game);
            }
        });
        dialog.show(getSupportFragmentManager(), "new_game");
    }

    protected void newGame(Game game){
        showProgress();

        RestApiService.startActionSaveEntity(getApplicationContext(), game, ACTION_CREATE_NEW_GAME_RESPONSE);
    }
}
