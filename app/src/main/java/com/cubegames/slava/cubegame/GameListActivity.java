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
import com.cubegames.slava.cubegame.model.GameInstance;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_GET_GAME_LIST;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_LIST_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_LIST;

public class GameListActivity extends BaseListActivity<Game> {

    private static final String ACTION_CREATE_GAME_INSTANCE_RESPONSE = "com.cubegames.slava.cubegame.api.action.ACTION_CREATE_GAME_INSTANCE_RESPONSE";
    public static final int START_GAME_INSTANCE_ACTION = 3;

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
    protected int getListItemViewID() {
        return R.layout.games_list_item;
    }
    @Override
    protected int getListItemTextID() {
        return R.id.game_name_text;
    }
    @Override
    protected int getListItemDeleteBtnID() {
        return R.id.delete_btn;
    }
    @Override
    protected int getListItemUserActionBtnID() {
        return R.id.start_game_btn;
    }

    @Override
    protected int getListHeaderID() {
        return R.layout.game_list_header;
    }

    @Override
    protected ListItemHolder createHolder() {
        return new GameItemHolder();
    }

    @Override
    protected void initHolder(View row, ListItemHolder holder, Game item) {
        super.initHolder(row, holder, item);

        ((GameItemHolder) holder).textCreated = (TextView) row.findViewById(R.id.game_created_text);
        ((GameItemHolder) holder).textMapID = (TextView) row.findViewById(R.id.map_id_text);
    }

    @Override
    protected void fillHolder(ListItemHolder holder, final Game item) {
        super.fillHolder(holder, item);

        ((GameItemHolder) holder).textCreated.setText(Utils.formatDateTime(item.getCreatedDate()));
        ((GameItemHolder) holder).textMapID.setText(item.getMapId());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_maps_list).setVisible(true);
        menu.findItem(R.id.action_game_instances_list).setVisible(true);
        menu.findItem(R.id.action_dbplayers_list).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_CREATE_GAME_INSTANCE_RESPONSE);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_CREATE_GAME_INSTANCE_RESPONSE)){
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
    protected void doUserAction(Game item) {
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

        RestApiService.startActionSaveEntity(getApplicationContext(), instance, ACTION_CREATE_GAME_INSTANCE_RESPONSE);
    }
}