package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;

//todo: implement functionality
public class GameActivity extends BaseItemDetailsActivity<Game> implements BaseItemDetailsActivity.WebErrorHandler {

    private MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(ID: " + getItem().getId() + ", created: " +
                Utils.formatDateTime(getItem().getCreatedDate()) + ")");

        mMapFragment.setWebErrorHandler(this);

        if(getItem() != null && getItem().getId() != null){
            mMapFragment.setGameEntity(getItem());

            showProgress();

            GameMap map = new GameMap();
            map.setId(getItem().getMapId());
            mMapFragment.loadMapImage(map);
        }
    }

    @Override
    protected String getItemExtra() {
        return EXTRA_ENTITY_OBJECT;
    }

    @Override
    public void onError(ErrorEntity error) {
        showError(error);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        mMapFragment.setIntentFilters(intentFilter);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (mMapFragment.handleWebServiceResponseAction(intent))
            return  true;
        else
            return super.handleWebServiceResponseAction(context, intent);
    }


}
