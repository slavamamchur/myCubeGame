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
    protected String getResponseAction() {
        return ACTION_GAME_MAP_LIST_RESPONSE;
    }
    @Override
    protected String getResponseExtra() {
        return EXTRA_GAME_MAP_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return EXTRA_GAME_MAP_OBJECT;
    }
    @Override
    protected Class<?> getActivityClass() {
        return GameMapActivity.class;
    }
    @Override
    protected int getCaptionResource() {
        return R.string.game_map_list_title;
    }
}
