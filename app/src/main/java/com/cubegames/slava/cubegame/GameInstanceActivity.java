package com.cubegames.slava.cubegame;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cubegames.slava.cubegame.model.GameInstance;

import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;

public class GameInstanceActivity extends BaseItemDetailsActivity<GameInstance> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_map);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(ID: " + getItem().getId()  + ")");
    }

    @Override
    protected String getItemExtra() {
        return EXTRA_ENTITY_OBJECT;
    }
}
