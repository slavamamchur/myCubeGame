package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.colorpicker.AmbilWarnaDialogFragment;
import com.cubegames.slava.cubegame.colorpicker.OnAmbilWarnaListener;
import com.cubegames.slava.cubegame.model.DbPlayer;
import com.cubegames.slava.cubegame.model.ErrorEntity;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_GET_PLAYER_LIST;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_LIST_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_SAVE_ENTITY_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_PLAYER_LIST;

public class DBPlayersListActivity extends BaseListActivity<DbPlayer> {

    @Override
    protected String getListAction() {
        return ACTION_GET_PLAYER_LIST;
    }
    @Override
    protected String getListResponseAction() {
        return ACTION_LIST_RESPONSE;
    }
    @Override
    protected String getListResponseExtra() {
        return EXTRA_PLAYER_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return EXTRA_ENTITY_OBJECT;
    }

    @Override
    protected Class<?> getDetailsActivityClass() {
        return null; //todo:
    }

    @Override
    protected DbPlayer getNewItem() {
        return new DbPlayer();
    }
    @Override
    protected String getNewItemActionName() {
        return getString(R.string.add_new_player_caption);
    }
    @Override
    protected int getCaptionResource() {
        return R.string.players_list_title;
    }

    @Override
    protected int getListItemViewID() {
        return R.layout.dbplayer_list_item;
    }
    @Override
    protected int getListItemTextID() {
        return R.id.player_name_text;
    }
    @Override
    protected int getListItemDeleteBtnID() {
        return R.id.delete_btn;
    }
    @Override
    protected int getListHeaderID() {
        return R.layout.dbplayer_list_header;
    }

    @Override
    protected ListItemHolder createHolder() {
        return new DBPlayerItemHolder();
    }
    @Override
    protected void initHolder(View row, ListItemHolder holder, DbPlayer item) {
        super.initHolder(row, holder, item);

        ((DBPlayerItemHolder) holder).textColor = (TextView) row.findViewById(R.id.player_color_text);
    }

    @Override
    protected void fillHolder(ListItemHolder holder, DbPlayer item) {
        super.fillHolder(holder, item);

        ((DBPlayerItemHolder) holder).textColor.setBackgroundColor(0xFF000000 | item.getColor());
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SAVE_ENTITY_RESPONSE)){
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_maps_list).setVisible(true);
        menu.findItem(R.id.action_game_instances_list).setVisible(true);
        menu.findItem(R.id.action_games_list).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void handleActionNew() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(0xFF000000);
        fragment.setOnAmbilWarnaListener(new OnAmbilWarnaListener() {
                                             @Override
                                             public void onCancel(AmbilWarnaDialogFragment dialogFragment) {}

                                             @Override
                                             public void onOk(AmbilWarnaDialogFragment dialogFragment, int color, String name) {
                                                 DbPlayer newItem = getNewItem();
                                                 newItem.setName(name);
                                                 newItem.setColor(color);

                                                 createEntity(newItem);
                                             }
                                         }
        );

        fragment.show(ft, "color_picker_dialog");
    }
}
