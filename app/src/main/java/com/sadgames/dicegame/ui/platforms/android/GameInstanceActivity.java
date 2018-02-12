package com.sadgames.dicegame.ui.platforms.android;

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

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.game_logic.items.DiceObject;
import com.sadgames.dicegame.rest_api.RestApiService;
import com.sadgames.dicegame.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameInstanceEntity;
import com.sadgames.dicegame.rest_api.model.entities.players.InstancePlayer;
import com.sadgames.dicegame.ui.platforms.android.framework.BaseItemDetailsActivity;
import com.sadgames.dicegame.ui.platforms.android.framework.DBColumnInfo;
import com.sadgames.dicegame.ui.platforms.android.framework.DBTableFragment;
import com.sadgames.dicegame.ui.platforms.android.framework.MapFragment;
import com.sadgames.gl3d_engine.gl_render.scene.GLAnimation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.vecmath.Vector3f;

import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_ACTION_SHOW_TURN_INFO;
import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_FINISH_GAME_INSTANCE_RESPONSE;
import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_MOOVE_GAME_INSTANCE_RESPONSE;
import static com.sadgames.dicegame.rest_api.RestApiService.ACTION_RESTART_GAME_INSTANCE_RESPONSE;
import static com.sadgames.dicegame.rest_api.RestApiService.EXTRA_DICE_VALUE;
import static com.sadgames.dicegame.rest_api.RestApiService.EXTRA_ENTITY_OBJECT;
import static com.sadgames.dicegame.rest_api.RestApiService.startActionFinishGameInstance;
import static com.sadgames.dicegame.rest_api.RestApiService.startActionMooveGameInstance;
import static com.sadgames.dicegame.rest_api.RestApiService.startActionRestartGameInstance;
import static com.sadgames.dicegame.ui.platforms.android.framework.BaseListActivity.NAME_FIELD_NAME;
import static com.sadgames.gl3d_engine.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;
import static com.sadgames.gl3d_engine.gl_render.scene.GLScene.CAMERA_ZOOM_ANIMATION_DURATION;

public class GameInstanceActivity extends BaseItemDetailsActivity<GameInstanceEntity> implements BaseItemDetailsActivity.WebErrorHandler {

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
        mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        playersFragment = (DBTableFragment) getSupportFragmentManager().findFragmentById(R.id.players_fragment);
        playersFragment.getView().setBackgroundColor(0x40000000);

        if(getItem() != null && getItem().getId() != null){
            //showProgress();
            mMapFragment.InitMap(getItem(), this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(State: " + getItem().getState()  + ")");

        fpsCounterTimer = new Timer();
        fpsCounterTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (mMapFragment.glRenderer.getScene().getFrameTime() > 0)
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
    }

    @Override
    protected void onDestroy() {
        getSysUtilsWrapper().iStopSound();
        fpsCounterTimer.cancel();

        super.onDestroy();
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
        intentFilter.addAction(ACTION_ACTION_SHOW_TURN_INFO);

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
                getItem().setState(GameInstanceEntity.State.FINISHED);
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
                final GameInstanceEntity instance = intent.getParcelableExtra(RestApiService.EXTRA_ENTITY_OBJECT);
                updateGame(instance);

                if (!GameInstanceEntity.State.WAIT.equals(instance.getState())
                    && !GameInstanceEntity.State.FINISHED.equals(instance.getState())
                   )
                    mMapFragment.movingChipAnimation(animationListener);

                else {
                    //toggleActionBarProgress(false);
                    if (getItem().getPlayers().get(prev_player_index).isSkipped())
                        showAnimatedText("Skip\nnext turn");
                }
            }
            else {
                showError(error);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_ACTION_SHOW_TURN_INFO)) {
            final int diceValue = intent.getIntExtra(EXTRA_DICE_VALUE, 0);
            showAnimatedText(String.format("%d\nSteps\nto GO", diceValue));

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    private final Handler mHandler = new Handler();
    MapFragment.ChipAnimadedDelegate animationListener = new MapFragment.ChipAnimadedDelegate() {
        @Override
        public void onAnimationEnd() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {startActionMooveGameInstance(GameInstanceActivity.this, getItem());}
            }, 1000);
        }
    };

    private void updateTitle () {
        long fps = 1000 / mMapFragment.glRenderer.getScene().getFrameTime();
        setTitle(getItem().getName() + "(State: " + getItem().getState()  + ") FPS: " + fps);
        supportInvalidateOptionsMenu();
    }

    private void resetGame() {
        getItem().setState(GameInstanceEntity.State.WAIT);
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

        /*mMapFragment.savedPlayers.clear();
        mMapFragment.savedPlayers = new ArrayList<>(getItem().getPlayers());*/

        mMapFragment.updateMap();

        //mMapFragment.scrollMap();
    }

    private void updateGame(GameInstanceEntity instance) {
        setItem(instance);
        setItemChanged(true);

        updateTitle ();

        playersFragment.selecItem(getItem().getCurrentPlayer());
        playersFragment.setItems(getItem().getPlayers());

        mMapFragment.setGameInstanceEntity(getItem());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_move).setVisible(true);
        menu.findItem(R.id.action_move).setEnabled(!GameInstanceEntity.State.FINISHED.equals(getItem().getState()));

        menu.findItem(R.id.action_finish).setVisible(true);
        menu.findItem(R.id.action_finish).setEnabled(!GameInstanceEntity.State.FINISHED.equals(getItem().getState()));

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

    //TODO: disable buttons while animation in progress
    private void playTurn() {

        mMapFragment.glRenderer.getScene().setZoomCameraAnimation(new GLAnimation(1 / 2f, CAMERA_ZOOM_ANIMATION_DURATION));
        mMapFragment.glRenderer.getScene().getZoomCameraAnimation().startAnimation(null, new GLAnimation.AnimationCallBack() {
            @Override
            public void onAnimationEnd() {
                rollDice();
            }
        });
    }

    private void rollDice() {
        //mMapFragment.glRenderer.getScene().setSimulation_time(System.currentTimeMillis());

        prev_player_index = getItem().getCurrentPlayer();

        DiceObject dice_1 = (DiceObject)mMapFragment.glRenderer.getScene().getObject(DICE_MESH_OBJECT_1);
        dice_1.createRigidBody();
        ///Transform tr = new Transform(new Matrix4f(dice_1.getModelMatrix()));
        ///dice_1.get_body().setWorldTransformMatrix(tr);
        Random rnd = new Random(System.currentTimeMillis());
        int direction = rnd.nextInt(2);
        float fy = 2f + rnd.nextInt(3) * 1f;
        float fxz = fy * 2f / 3f;
        fxz = direction == 1 && (rnd.nextInt(2) > 0) ? -1*fxz : fxz;
        dice_1.get_body().setLinearVelocity(direction == 0 ? new Vector3f(0f,fy,fxz) : new Vector3f(fxz,fy,0f));
        mMapFragment.glRenderer.getPhysicalWorldObject().addRigidBody(dice_1.get_body());
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
