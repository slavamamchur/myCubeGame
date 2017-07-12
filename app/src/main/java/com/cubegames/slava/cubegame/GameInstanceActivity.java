package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.gl_render.GLAnimation;
import com.cubegames.slava.cubegame.gl_render.GLRenderConsts;
import com.cubegames.slava.cubegame.gl_render.scene.objects.GLSceneObject;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.players.InstancePlayer;

import java.util.ArrayList;
import java.util.Arrays;
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
import static com.cubegames.slava.cubegame.gl_render.GLAnimation.ROTATE_BY_X;
import static com.cubegames.slava.cubegame.gl_render.GLAnimation.ROTATE_BY_Z;
import static com.cubegames.slava.cubegame.gl_render.GLRenderConsts.DICE_MESH_OBJECT_1;

public class GameInstanceActivity extends BaseItemDetailsActivity<GameInstance> implements BaseItemDetailsActivity.WebErrorHandler {

    public static final String FINISHED_FIELD_NAME = "finished";
    public static final String SKIPPED_FIELD_NAME = "skipped";

    private static final ArrayList<DBColumnInfo> PLAYERS_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Player", 45,
                    DBColumnInfo.ColumnType.COLUMN_TEXT,
                    InstancePlayer.class.getDeclaredField(NAME_FIELD_NAME), null, -1,
                    InstancePlayer.class.getDeclaredField(COLOR_FIELD_NAME)));

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        ///requestWindowFeature(Window.FEATURE_PROGRESS);

        //getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_game_instance);

        mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        playersFragment = (DBTableFragment) getSupportFragmentManager().findFragmentById(R.id.players_fragment);
        playersFragment.getView().setBackgroundColor(0x40000000);

        //OGL
        if(getItem() != null && getItem().getId() != null){
            //showProgress();
            mMapFragment.InitMap(getItem(), this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(State: " + getItem().getState()  + ")");

        if(getItem() != null && getItem().getId() != null){

            /*//OGL
            showProgress();
            mMapFragment.InitMap(getItem(), this);*/

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
                final GameInstance instance = intent.getParcelableExtra(RestApiService.EXTRA_ENTITY_OBJECT);
                updateGame(instance);

                if (!GameInstance.State.WAIT.equals(instance.getState())
                    && !GameInstance.State.FINISHED.equals(instance.getState())
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
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    MapFragment.ChipAnimadedDelegate animationListener = new MapFragment.ChipAnimadedDelegate() {
        @Override
        public void onAnimationEnd() {
            try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}

            startActionMooveGameInstance(GameInstanceActivity.this, getItem());
        }
    };

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

        /*mMapFragment.savedPlayers.clear();
        mMapFragment.savedPlayers = new ArrayList<>(getItem().getPlayers());*/

        mMapFragment.updateMap();

        //mMapFragment.scrollMap();
    }

    private void updateGame(GameInstance instance) {
        setItem(instance);
        setItemChanged(true);

        updateTitle ();

        playersFragment.selecItem(getItem().getCurrentPlayer());
        playersFragment.setItems(getItem().getPlayers());

        mMapFragment.setGameInstanceEntity(getItem());
        mMapFragment.glRenderer.setGameInstanceEntity(getItem());
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

    //TODO: disable buttons while animation in progress
    private void playTurn() {
        Random rnd = new Random(System.currentTimeMillis());

        final int rotationAngle = 45 - rnd.nextInt(90);
        final short flyRotationCount = (short) rnd.nextInt(10);
        //System.out.println(flyRotationCount);
        final int direction = 1; //Math.round(rnd.nextFloat());
        final short directionAxe = direction == 0 ? ROTATE_BY_X : ROTATE_BY_Z;

        final int steps2Go = 1; //rnd.nextInt(5) + 1;
        final short[][] dices_values = {{3, 2, 4, 5},
                                        {3, 1, 4, 6}};

        final GLSceneObject dice_1 = mMapFragment.glRenderer.getmScene().getObject(DICE_MESH_OBJECT_1);
        Matrix.setIdentityM(dice_1.getModelMatrix(), 0);
        Matrix.scaleM(dice_1.getModelMatrix(), 0, 0.1f, 0.1f, 0.1f);
        //Matrix.rotateM(dice_1.getModelMatrix(), 0, rotationAngle, 0, 1, 0);

        GLAnimation animation;
        animation = new GLAnimation(GLRenderConsts.GLAnimationType.TRANSLATE_ANIMATION,
                0, 0,
                5f, 0.5f,
                0, 0,
                500
        );
        animation.setBaseMatrix(Arrays.copyOf(dice_1.getModelMatrix(), 16));
        dice_1.setAnimation(animation);
        animation.startAnimation(new GLAnimation.AnimationCallBack() {
            @Override
            public void onAnimationEnd() {
                if (direction == 0)
                    Matrix.rotateM(dice_1.getModelMatrix(), 0, -flyRotationCount * 90, 1, 0, 0);
                else
                    Matrix.rotateM(dice_1.getModelMatrix(), 0, flyRotationCount * 90, 0, 0, 1);

                GLAnimation animation = new GLAnimation(GLRenderConsts.GLAnimationType.ROTATE_ANIMATION, 90f, directionAxe, 2000);
                animation.setBaseMatrix(Arrays.copyOf(dice_1.getModelMatrix(), 16));
                short rCnt = (short) steps2Go;
                animation.setRepeatCount(rCnt);
                dice_1.setAnimation(animation);

                animation.startAnimation(null);
            }
        });

        prev_player_index = getItem().getCurrentPlayer();

        int dice_1_Value = dices_values[direction][(flyRotationCount + steps2Go) % 4];
        showAnimatedText(String.format("%d\nSteps\nto GO", dice_1_Value));

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
