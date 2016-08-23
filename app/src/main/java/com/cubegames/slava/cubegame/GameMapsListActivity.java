package com.cubegames.slava.cubegame;

import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_GAME_MAP_LIST_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_GET_GAME_MAP_LIST;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_LIST;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_OBJECT;

public class GameMapsListActivity extends BaseListActivity<GameMap> {

    @Override
    protected String getListAction() {
        return ACTION_GET_GAME_MAP_LIST;
    }
    @Override
    protected String getListResponseAction() {
        return ACTION_GAME_MAP_LIST_RESPONSE;
    }
    @Override
    protected String getListResponseExtra() {
        return EXTRA_GAME_MAP_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return EXTRA_GAME_MAP_OBJECT;
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
}
