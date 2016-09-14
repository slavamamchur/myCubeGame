package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.GameInstance;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_FINISH_GAME_INSTANCE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_LIST_RESPONSE;

public class GameInstanceListActivity extends BaseListActivity<GameInstance> {
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
    protected int getListItemViewID() {
        return R.layout.game_instance_list_item;
    }
    @Override
    protected int getListItemTextID() {
        return R.id.game_instance_name_text;
    }
    @Override
    protected int getListItemUserActionBtnID() {
        return R.id.finish_game_btn;
    }

    @Override
    protected int getListHeaderID() {
        return R.layout.game_instance_list_header;
    }

    @Override
    protected ListItemHolder createHolder() {
        return new GameInstanceItemHolder();
    }

    @Override
    protected void initHolder(View row, ListItemHolder holder, GameInstance item) {
        super.initHolder(row, holder, item);

        ((GameInstanceItemHolder) holder).textStarted = (TextView) row.findViewById(R.id.game_started_text);
        ((GameInstanceItemHolder) holder).textLastUsed = (TextView) row.findViewById(R.id.game_last_used_text);
        ((GameInstanceItemHolder) holder).textState = (TextView) row.findViewById(R.id.game_state_text);
        ((GameInstanceItemHolder) holder).textPlayersCount = (TextView) row.findViewById(R.id.game_players_count_text);
    }

    @Override
    protected void fillHolder(ListItemHolder holder, final GameInstance item) {
        super.fillHolder(holder, item);

        ((GameInstanceItemHolder) holder).textStarted.setText(Utils.formatDateTime(item.getStartedDate()));
        ((GameInstanceItemHolder) holder).textLastUsed.setText(Utils.formatDateTime(item.getLastUsedDate()));
        ((GameInstanceItemHolder) holder).textState.setText(item.getState().name());
        ((GameInstanceItemHolder) holder).textPlayersCount.setText(
                String.format("%d", item.getPlayers().size()));

        holder.btnUserAction.setEnabled(!item.getState().equals(GameInstance.State.FINISHED));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_maps_list).setVisible(true);
        menu.findItem(R.id.action_games_list).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void doUserAction(GameInstance item) {
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
