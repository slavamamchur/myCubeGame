package com.cubegames.slava.cubegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.cubegames.slava.cubegame.model.DbPlayer;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.BaseListActivity.NAME_FIELD_NAME;
import static com.cubegames.slava.cubegame.DBPlayersListActivity.ACTION_DICTIONARY;
import static com.cubegames.slava.cubegame.DBPlayersListActivity.COLOR_FIELD_NAME;
import static com.cubegames.slava.cubegame.DBPlayersListActivity.EXTRA_STARTED_AS_DICTIONARY;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.cubegames.slava.cubegame.model.GameInstance.State.WAIT;

public class NewGameInstanceActivity extends BaseActivityWithMenu {

    private static final ArrayList<DBColumnInfo> PLAYERS_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Name", 50, DBColumnInfo.ColumnType.COLUMN_TEXT, InstancePlayer.class.getDeclaredField(NAME_FIELD_NAME), null));
            add(new DBColumnInfo("Color", 10, DBColumnInfo.ColumnType.COLUMN_COLOR_BOX, InstancePlayer.class.getDeclaredField(COLOR_FIELD_NAME), null));
            add(new DBColumnInfo("", 40, DBColumnInfo.ColumnType.COLUMN_TEXT, null, null));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    private Game gameEntity;
    private GameInstance instance = new GameInstance();
    private EditText editName;
    private DBTableFragment tableFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start_instance_layout);

        editName = (EditText) findViewById(R.id.edit_name);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tableFragment = (DBTableFragment) getSupportFragmentManager().findFragmentById(R.id.players_list_fragment);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        gameEntity = getIntent().getParcelableExtra(EXTRA_ENTITY_OBJECT);

        setTitle(getString(R.string.start_new_instance_title) + " (Game ID: " + gameEntity.getId() + ")");

        instance.setGame(gameEntity);
        instance.setPlayers( new ArrayList<InstancePlayer>());
        instance.setState(WAIT);

        tableFragment.initTable(PLAYERS_LIST_COLUMN_INFO, null);
        tableFragment.setItems(instance.getPlayers());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_new).setVisible(true);
        menu.findItem(R.id.action_new).setTitle(R.string.add_new_player_caption);
        menu.findItem(R.id.action_start_instance).setVisible(true);
        menu.findItem(R.id.action_remove_all_players).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();

            return true;
        }
        else if (id == R.id.action_new) {
            Intent mIntent = new Intent(getApplicationContext(), DBPlayersListActivity.class);
            mIntent.putExtra(EXTRA_STARTED_AS_DICTIONARY, true);
            startActivityForResult(mIntent, ACTION_DICTIONARY);

            return true;
        }
        else if (id == R.id.action_start_instance) {
            instance.setName(editName.getText().toString());
            if (instance.getPlayers().size() == 0)
                showError(getString(R.string.players_list_empty_error));
            else if (TextUtils.isEmpty(editName.getText().toString())){
                editName.setError(getString(R.string.blank_name_error));
            }
            else
                startInstance(instance);

            return true;
        }
        else if(id == R.id.action_remove_all_players) {
            instance.getPlayers().clear();
            tableFragment.setItems(instance.getPlayers());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startInstance(GameInstance instance){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ENTITY_OBJECT, instance);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_DICTIONARY && resultCode == RESULT_OK && data != null) {
            DbPlayer newPlayer = data.getParcelableExtra(EXTRA_ENTITY_OBJECT);

            if (newPlayer != null) {
                InstancePlayer iplayer = new InstancePlayer();
                iplayer.setColor(newPlayer.getColor());
                iplayer.setName(newPlayer.getName());

                if (!instance.getPlayers().contains(iplayer)) {
                    instance.getPlayers().add(iplayer);
                    tableFragment.setItems(instance.getPlayers());
                }
                else {
                    showError(getString(R.string.player_exists_error));
                }
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
