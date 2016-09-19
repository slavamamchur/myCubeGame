package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_GET_GAME_MAP_LIST;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_LIST_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_LIST;

public class GameMapsListActivity extends BaseListActivity<GameMap> {
    private static final String ACTION_CREATE_NEW_GAME_RESPONSE = "com.cubegames.slava.cubegame.api.action.ACTION_CREATE_NEW_GAME_RESPONSE";

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
    protected int getListItemViewID() {
        return R.layout.map_list_item;
    }
    @Override
    protected int getListItemTextID() {
        return R.id.map_name_text;
    }
    @Override
    protected int getListItemDeleteBtnID() {
        return R.id.delete_btn;
    }
    @Override
    protected int getListItemUserActionBtnID() {
        return R.id.new_game_btn;
    }

    @Override
    protected int getListHeaderID() {
        return R.layout.map_list_header;
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
    protected ListItemHolder createHolder() {
        return new MapItemHolder();
    }
    @Override
    protected void initHolder(View row, ListItemHolder holder, GameMap item) {
        super.initHolder(row, holder, item);

        ((MapItemHolder) holder).textCreated = (TextView) row.findViewById(R.id.map_created_text);
    }

    @Override
    protected void fillHolder(ListItemHolder holder, GameMap item) {
        super.fillHolder(holder, item);

        ((MapItemHolder) holder).textCreated.setText(Utils.formatDateTime(item.getCreatedDate()));
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
    protected void doUserAction(final GameMap item) {
        InputNameDialogFragment dialog = new InputNameDialogFragment();
        dialog.setDelegate(new InputNameDelegate() {
            @Override
            public void doAction(String name) {
                Game game = new Game();
                game.setName(name);
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
