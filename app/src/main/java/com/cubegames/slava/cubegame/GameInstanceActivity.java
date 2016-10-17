package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;

import java.util.ArrayList;
import java.util.Random;

import static com.cubegames.slava.cubegame.BaseListActivity.NAME_FIELD_NAME;
import static com.cubegames.slava.cubegame.DBPlayersListActivity.COLOR_FIELD_NAME;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_FINISH_GAME_INSTANCE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_MOOVE_GAME_INSTANCE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_RESTART_GAME_INSTANCE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionFinishGameInstance;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionMooveGameInstance;
import static com.cubegames.slava.cubegame.api.RestApiService.startActionRestartGameInstance;

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

    private int prev_player_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        //getWindow().requestFeature(Window.FEATURE_PROGRESS);

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

        intentFilter.addAction(ACTION_FINISH_GAME_INSTANCE_RESPONSE);
        intentFilter.addAction(ACTION_RESTART_GAME_INSTANCE_RESPONSE);
        intentFilter.addAction(ACTION_MOOVE_GAME_INSTANCE_RESPONSE);

        mMapFragment.setIntentFilters(intentFilter);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (mMapFragment.handleWebServiceResponseAction(intent))
            return  true;
        else if (intent.getAction().equals(ACTION_FINISH_GAME_INSTANCE_RESPONSE)) {
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);

            if (error == null){
                getItem().setState(GameInstance.State.FINISHED);
                setItemChanged(true);
                updateTitle ();
            }
            else {
                showError(error);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_RESTART_GAME_INSTANCE_RESPONSE)) {
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);

            if (error == null){
                resetGame();
            }
            else {
                showError(error);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_MOOVE_GAME_INSTANCE_RESPONSE)) {
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);

            if (error == null){
                GameInstance instance = intent.getParcelableExtra(RestApiService.EXTRA_ENTITY_OBJECT);
                updateGame(instance);
                if (!GameInstance.State.WAIT.equals(instance.getState())
                    && !GameInstance.State.FINISHED.equals(instance.getState())
                   ) {
                    mMapFragment.scrollMap();
                    startActionMooveGameInstance(this, getItem());
                }
                else {
                    toggleActionBarProgress(false);
                    if (getItem().getPlayers().get(prev_player_index).isSkipped())
                        showAnimatedText("Skip\nnext turn");

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {}
                    mMapFragment.scrollMap();
                }
            }
            else {
                showError(error);
            }

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    private void updateTitle () {
        setTitle(getItem().getName() + "(State: " + getItem().getState()  + ")");
        supportInvalidateOptionsMenu();
    }

    private void resetGame() {
        getItem().setState(GameInstance.State.WAIT);
        getItem().setCurrentPlayer(0);
        getItem().setStepsToGo(0);
        for (InstancePlayer player : getItem().getPlayers()) {
            player.setCurrentPoint(0);
            player.setFinished(false);
            player.setSkipped(false);
        }
        setItemChanged(true);

        updateTitle ();

        playersFragment.selecItem(getItem().getCurrentPlayer());
        playersFragment.setItems(getItem().getPlayers());

        mMapFragment.updateMap();
        mMapFragment.scrollMap();
    }

    private void updateGame(GameInstance instance) {
        setItem(instance);
        setItemChanged(true);

        updateTitle ();

        playersFragment.selecItem(getItem().getCurrentPlayer());
        playersFragment.setItems(getItem().getPlayers());

        mMapFragment.setGameInstanceEntity(getItem());
        mMapFragment.updateMap();
        //mMapFragment.scrollMap();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_move).setVisible(true);
        menu.findItem(R.id.action_move).setEnabled(!GameInstance.State.FINISHED.equals(getItem().getState()));

        menu.findItem(R.id.action_finish).setVisible(true);
        menu.findItem(R.id.action_finish).setEnabled(!GameInstance.State.FINISHED.equals(getItem().getState()));

        menu.findItem(R.id.action_restart).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_move:
                playTurn();
                return true;
            case R.id.action_restart:
                restartGame();
                return true;
            case R.id.action_finish:
                finishGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void playTurn() {
        prev_player_index = getItem().getCurrentPlayer();

        Random rnd = new Random(System.currentTimeMillis());
        int steps2Go = rnd.nextInt(5) + 1;
        showAnimatedText(String.format("%d\nSteps\nto GO", steps2Go));

        toggleActionBarProgress(true);
        getItem().setStepsToGo(steps2Go);
        startActionMooveGameInstance(this, getItem());
    }

    private void showAnimatedText(String text) {
        TextView steps = (TextView) findViewById(R.id.tv_steps_to_go);
        steps.setText(text);
        steps.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.view_growing);
        steps.setAnimation(animation);
        steps.animate();
        animation.start();
        steps.setVisibility(View.INVISIBLE);
    }

    private void finishGame() {
        showProgress();
        startActionFinishGameInstance(this, getItem());
    }

    private void restartGame() {
        showProgress();
        startActionRestartGameInstance(this, getItem());
    }
}
