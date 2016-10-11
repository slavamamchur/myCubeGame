package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.BaseListActivity.NAME_FIELD_NAME;
import static com.cubegames.slava.cubegame.DBPlayersListActivity.COLOR_FIELD_NAME;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;

public class GameInstanceActivity extends BaseItemDetailsActivity<GameInstance> implements BaseItemDetailsActivity.WebErrorHandler {

    public static final String FINISHED_FIELD_NAME = "finished";

    private static final ArrayList<DBColumnInfo> PLAYERS_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Player", 70,
                    DBColumnInfo.ColumnType.COLUMN_TEXT,
                    InstancePlayer.class.getDeclaredField(NAME_FIELD_NAME), null, -1,
                    InstancePlayer.class.getDeclaredField(COLOR_FIELD_NAME)));

            add(new DBColumnInfo("Finished", 30, DBColumnInfo.ColumnType.COLUMN_CHECK_BOX, InstancePlayer.class.getDeclaredField(FINISHED_FIELD_NAME), null));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    private MapFragment mMapFragment;
    private DBTableFragment playersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_instance);

        mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        playersFragment = (DBTableFragment) getSupportFragmentManager().findFragmentById(R.id.players_fragment);
        playersFragment.getView().setBackgroundColor(0x40000000);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(State: " + getItem().getState()  + ")");

        if(getItem() != null && getItem().getId() != null){
            showProgress();
            mMapFragment.InitMap(getItem(), this);
            playersFragment.setCaptionColor(Color.WHITE);
            playersFragment.selecItem(getItem().getCurrentPlayer());
            playersFragment.initTable(PLAYERS_LIST_COLUMN_INFO, null);
            playersFragment.setItems(getItem().getPlayers());
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_move).setVisible(true);
        menu.findItem(R.id.action_finish).setVisible(true);
        menu.findItem(R.id.action_restart).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_move) {

            return true;
        }
        //TODO: menu

        return super.onOptionsItemSelected(item);
    }
}
