package com.cubegames.slava.cubegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.cubegames.slava.cubegame.model.DbPlayer;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.DBPlayersListActivity.ACTION_DICTIONARY;
import static com.cubegames.slava.cubegame.DBPlayersListActivity.EXTRA_STARTED_AS_DICTIONARY;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.cubegames.slava.cubegame.model.GameInstance.State.WAIT;

public class NewGameInstanceActivity extends BaseActivityWithMenu {

    private Game gameEntity;
    private GameInstance instance = new GameInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        gameEntity = getIntent().getParcelableExtra(EXTRA_ENTITY_OBJECT);
        setTitle(getString(R.string.start_new_instance_title) + " (Game ID: " + gameEntity.getId() + ")");

        instance.setGame(gameEntity);
        instance.setPlayers( new ArrayList<InstancePlayer>());
        instance.setState(WAIT);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_new).setVisible(true);
        menu.findItem(R.id.action_new).setTitle(R.string.add_new_player_caption);
        menu.findItem(R.id.action_start_instance).setVisible(true);

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
            instance.setName("111");
            if (instance.getPlayers().size() == 0)
                showError(getString(R.string.players_list_empty_error));
            else
                startInstance(instance);
            //todo: check name

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

                if (!instance.getPlayers().contains(iplayer))
                    instance.getPlayers().add(iplayer);
                else {
                    showError(getString(R.string.player_exists_error));
                }

                //todo: update list
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
