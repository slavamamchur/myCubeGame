package com.sadgames.dicegame.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.dicegame.logic.client.GameConst;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.GameEntity;
import com.sadgames.dicegame.logic.server.rest_api.model.entities.points.AbstractGamePoint;
import com.sadgames.dicegame.ui.framework.BaseItemDetailsActivity;
import com.sadgames.dicegame.ui.framework.DBColumnInfo;
import com.sadgames.dicegame.ui.framework.DBTableFragment;
import com.sadgames.dicegame.ui.framework.MapFragment;
import com.sadgames.sysutils.common.DateTimeUtils;

import java.util.ArrayList;

import static com.sadgames.dicegame.logic.client.GameConst.ACTION_LIST;
import static com.sadgames.dicegame.logic.client.GameConst.ACTION_REMOVE_LOADING_SPLASH;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_ENTITY_OBJECT;
import static com.sadgames.dicegame.logic.client.GameConst.EXTRA_ERROR_OBJECT;
import static com.sadgames.dicegame.ui.framework.BaseListActivity.EDIT_ENTITY_TAG;
import static com.sadgames.dicegame.ui.framework.DBTableFragment.DELETE_ENTITY_TAG;

public class GameActivity extends BaseItemDetailsActivity<GameEntity> {

    private static final String X_POS_FIELD_NAME = "xPos";
    private static final String Y_POS_FIELD_NAME = "yPos";
    private static final String TYPE_FIELD_NAME = "type";

    private static final ArrayList<DBColumnInfo> GAME_POINTS_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("X pos", 20, DBColumnInfo.ColumnType.COLUMN_TEXT, AbstractGamePoint.class.getDeclaredField(X_POS_FIELD_NAME), null));
            add(new DBColumnInfo("Y pos", 20, DBColumnInfo.ColumnType.COLUMN_TEXT, AbstractGamePoint.class.getDeclaredField(Y_POS_FIELD_NAME), null));
            add(new DBColumnInfo("Type", 50, DBColumnInfo.ColumnType.COLUMN_TEXT, AbstractGamePoint.class.getDeclaredField(TYPE_FIELD_NAME), null));
            //add(new DBColumnInfo("", 10, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, DELETE_ENTITY_TAG, android.R.drawable.ic_delete, null));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    private MapFragment mMapFragment;
    private DBTableFragment tableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        tableFragment = (DBTableFragment) getSupportFragmentManager().findFragmentById(R.id.game_points_list_fragment);
        //TODO: camera navigation fragment and loading splash fragment

        if(getItem() != null && getItem().getId() != null){
            //showProgress();
            mMapFragment.InitMap(getItem());
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(ID: " + getItem().getId() + ", created: " +
                DateTimeUtils.formatDateTime(getItem().getCreatedDate()) + ")");

        DBTableFragment.OnItemClickDelegate onItemClickDelegate = new DBTableFragment.OnItemClickDelegate() {
            @Override
            public void onClick(String tag, Object item) {
                if (EDIT_ENTITY_TAG.equals(tag)) {
                    performEditAction((AbstractGamePoint)item);
                }
                else if (DELETE_ENTITY_TAG.equals(tag)) {
                    int pos = getItem().getGamePoints().indexOf(item);
                    showProgress();
                    RestApiService.startActionRemoveChild(GameActivity.this,
                            getItem().getId(), AbstractGamePoint.urlForActionName(), pos);
                }
            }

            @Override
            public boolean isEnabled(String tag, Object item) {
                return true;
            }
        };

        tableFragment.initTable(GAME_POINTS_LIST_COLUMN_INFO, onItemClickDelegate);
        tableFragment.setItems(getItem().getGamePoints());

        showProgress();
    }

    protected void performEditAction(AbstractGamePoint item) {
        //setItemChanged(true);
        showError("Not implemented yet");
    }

    @Override
    protected String getItemExtra() {
        return EXTRA_ENTITY_OBJECT;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_new).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();

        intentFilter.addAction(ACTION_REMOVE_LOADING_SPLASH);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);
        if (error != null) {
            showError(error);
            return true;
        }

        GameConst.UserActionType actionType = GameConst.UserActionType.values()[ACTION_LIST.indexOf(intent.getAction())];
        if (GameConst.UserActionType.REMOVE_LOADING_SPLASH.equals(actionType)) {
            //TODO: remove splash view
            hideProgress();

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }
}
