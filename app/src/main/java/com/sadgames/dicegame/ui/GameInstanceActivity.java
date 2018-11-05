package com.sadgames.dicegame.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.cubegames.engine.domain.entities.players.InstancePlayer;
import com.sadgames.dicegame.R;
import com.sadgames.dicegame.ui.framework.BaseItemDetailsActivity;
import com.sadgames.dicegame.ui.framework.DBColumnInfo;
import com.sadgames.dicegame.ui.framework.DBTableFragment;
import com.sadgames.dicegame.ui.framework.MapFragment;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.sadgames.dicegame.ui.framework.BaseListActivity.NAME_FIELD_NAME;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_FINISH_GAME_INSTANCE_RESPONSE;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_LIST;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_MOOVE_GAME_INSTANCE_RESPONSE;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_REMOVE_LOADING_SPLASH;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_RESTART_GAME_INSTANCE_RESPONSE;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_SHOW_TURN_INFO;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.EXTRA_DICE_VALUE;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.EXTRA_ENTITY_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.EXTRA_ERROR_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.GameState;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PLAYER_NEXT_MOVE__EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ON_PLAY_TURN_EVENT_HANDLER;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.UserActionType;

public class GameInstanceActivity extends BaseItemDetailsActivity<GameInstanceEntity> implements  AndroidFragmentApplication.Callbacks {

    public static final String FINISHED_FIELD_NAME = "finished";
    public static final String SKIPPED_FIELD_NAME = "skipped";

    private static final ArrayList<DBColumnInfo> PLAYERS_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Player", 45,
                    DBColumnInfo.ColumnType.COLUMN_TEXT,
                    InstancePlayer.class.getDeclaredField(NAME_FIELD_NAME), null, -1,
                    InstancePlayer.class.getDeclaredField(DBPlayersListActivity.COLOR_FIELD_NAME)));

            add(new DBColumnInfo("Finished", 30, DBColumnInfo.ColumnType.COLUMN_CHECK_BOX, InstancePlayer.class.getDeclaredField(FINISHED_FIELD_NAME), null));
            add(new DBColumnInfo("Skipped", 25, DBColumnInfo.ColumnType.COLUMN_CHECK_BOX, InstancePlayer.class.getDeclaredField(SKIPPED_FIELD_NAME), null));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    private MapFragment mMapFragment;
    private DBTableFragment playersFragment;
    private int prev_player_index;
    private Timer fpsCounterTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        ///requestWindowFeature(Window.FEATURE_PROGRESS);
        //getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_game_instance);

        //TODO: camera navigation fragment
        mMapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().
                add(R.id.map_fragment, mMapFragment).
                commit();

        playersFragment = (DBTableFragment) getSupportFragmentManager().findFragmentById(R.id.players_fragment);
        playersFragment.getView().setBackgroundColor(0x40000000);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(getItem() != null && getItem().getId() != null){
            mMapFragment.InitMap(getItem());
        }

        setTitle(getItem().getName() + "(GameState: " + getItem().getState()  + ")");

        fpsCounterTimer = new Timer();
        fpsCounterTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mMapFragment.getScene().getFrameTime() > 0)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTitle();
                            }
                        });
                }
                catch (Exception e) {}
            }
        }, 500, 1000);

        if(getItem() != null && getItem().getId() != null){
            playersFragment.setCaptionColor(Color.WHITE);
            playersFragment.selecItem(getItem().getCurrentPlayer());
            playersFragment.initTable(PLAYERS_LIST_COLUMN_INFO, null);
            playersFragment.setItems(getItem().getPlayers());
        }

        showProgress();
    }

    @Override
    protected void onDestroy() {
        //getSysUtilsWrapper().iStopSound();
        fpsCounterTimer.cancel();

        super.onDestroy();
    }

    @Override
    protected String getItemExtra() {
        return EXTRA_ENTITY_OBJECT;
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();

        intentFilter.addAction(ACTION_FINISH_GAME_INSTANCE_RESPONSE);
        intentFilter.addAction(ACTION_RESTART_GAME_INSTANCE_RESPONSE);
        intentFilter.addAction(ACTION_MOOVE_GAME_INSTANCE_RESPONSE);
        intentFilter.addAction(ACTION_SHOW_TURN_INFO);
        intentFilter.addAction(ACTION_REMOVE_LOADING_SPLASH);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        ErrorEntity error = (ErrorEntity) intent.getSerializableExtra(EXTRA_ERROR_OBJECT);
        if (error != null) {
            showError(error);
            return true;
        }

        UserActionType actionType = UserActionType.values()[ACTION_LIST.indexOf(intent.getAction())];
        switch (actionType) {
            case FINISH_GAME_INSTANCE_RESPONSE:
                mMapFragment.getGameLogic().onGameFinished();
                setItemChanged(true);

                return true;
            case RESTART_GAME_INSTANCE_RESPONSE:
                resetGame();

                return true;
            case MOOVE_GAME_INSTANCE_RESPONSE: //TODO: enable buttons
                final GameInstanceEntity instance = (GameInstanceEntity) intent.getSerializableExtra(EXTRA_ENTITY_OBJECT);
                updateGame(instance);

                if (!GameState.WAIT.equals(instance.getState())
                        && !GameState.FINISHED.equals(instance.getState())
                        )
                    mMapFragment.getGameLogic().onPlayerMakeTurn(animationListener);

                else {
                    //toggleActionBarProgress(false);
                    if (getItem().getPlayers().get(prev_player_index).isSkipped())
                        showAnimatedText("Skip\nnext turn");
                }

                return true;
            case SHOW_TURN_INFO:
                final int diceValue = intent.getIntExtra(EXTRA_DICE_VALUE, 0);
                showAnimatedText(String.format("%d\nSteps\nto GO", diceValue));

                return true;
            case REMOVE_LOADING_SPLASH:
                hideProgress();

                return true;
            default:
                return super.handleWebServiceResponseAction(context, intent);
        }
    }

    private final Handler mHandler = new Handler();
    GLAnimation.AnimationCallBack animationListener = new GLAnimation.AnimationCallBack() {
        @Override
        public void onAnimationEnd() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMapFragment.getGameLogic().getLuaEngine().get(ON_PLAYER_NEXT_MOVE__EVENT_HANDLER).call(CoerceJavaToLua.coerce(getItem()));
                }
            }, 1000);
        }
    };

    private void updateTitle () {
        long fps = 1000 / mMapFragment.getScene().getFrameTime();
        setTitle(getItem().getName() + "(GameState: " + getItem().getState()  + ") FPS: " + fps);
        supportInvalidateOptionsMenu();
    }

    private void resetGame() {
        mMapFragment.getGameLogic().onGameRestarted();

        setItemChanged(true);

        playersFragment.selecItem(getItem().getCurrentPlayer());
        playersFragment.setItems(getItem().getPlayers());
    }

    private void updateGame(GameInstanceEntity instance) {
        setItem(instance);
        setItemChanged(true);

        playersFragment.selecItem(getItem().getCurrentPlayer());
        playersFragment.setItems(getItem().getPlayers());

        mMapFragment.getGameLogic().setGameInstanceEntity(getItem());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_move).setVisible(true);
        menu.findItem(R.id.action_move).setEnabled(!GameState.FINISHED.equals(getItem().getState()));

        menu.findItem(R.id.action_finish).setVisible(true);
        menu.findItem(R.id.action_finish).setEnabled(!GameState.FINISHED.equals(getItem().getState()));

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

    //TODO: disable buttons
    private void playTurn() {
        prev_player_index = getItem().getCurrentPlayer();
        mMapFragment.getGameLogic().onPerformUserAction(ON_PLAY_TURN_EVENT_HANDLER, new LuaValue[]{});
    }

    private void showAnimatedText(String text) {
        TextView steps = findViewById(R.id.tv_steps_to_go);
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
        mMapFragment.getGameLogic().requestFinishGame();
    }

    private void restartGame() {
        showProgress();
        mMapFragment.getGameLogic().requestRestartGame();
    }

    @Override
    public void exit() {

    }
}
