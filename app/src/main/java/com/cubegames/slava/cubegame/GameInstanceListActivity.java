package com.cubegames.slava.cubegame;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.GameInstance;

public class GameInstanceListActivity extends BaseListActivity<GameInstance> {
    @Override
    protected String getListAction() {
        return RestApiService.ACTION_GET_GAME_INSTANCE_LIST;
    }
    @Override
    protected String getListResponseAction() {
        return RestApiService.ACTION_GAME_INSTANCE_LIST_RESPONSE;
    }
    @Override
    protected String getListResponseExtra() {
        return RestApiService.EXTRA_GAME_INSTANCE_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return RestApiService.EXTRA_GAME_INSTANCE;
    }
    @Override
    protected Class<?> getDetailsActivityClass() {
        return GameMapActivity.class;
    }
    @Override
    protected int getCaptionResource() {
        return R.string.game_map_list_title;
    }
}
