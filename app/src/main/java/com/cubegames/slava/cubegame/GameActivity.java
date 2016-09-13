package com.cubegames.slava.cubegame;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cubegames.slava.cubegame.model.Game;

import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;

//todo: implement functionality
public class GameActivity extends BaseItemDetailsActivity<Game> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_map);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(ID: " + getItem().getId() + ", created: " +
                Utils.formatDateTime(getItem().getCreatedDate()) + ")");
    }

    @Override
    protected String getItemExtra() {
        return EXTRA_ENTITY_OBJECT;
    }
}
