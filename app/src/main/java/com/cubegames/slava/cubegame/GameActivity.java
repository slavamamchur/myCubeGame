package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.ErrorEntity;
import com.cubegames.slava.cubegame.model.Game;
import com.cubegames.slava.cubegame.model.points.AbstractGamePoint;
import com.cubegames.slava.cubegame.model.points.NewPointRequest;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.BaseListActivity.EDIT_ENTITY_TAG;
import static com.cubegames.slava.cubegame.DBTableFragment.DELETE_ENTITY_TAG;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_ACTION_ADD_CHILD_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_ACTION_REMOVE_CHILD_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_CHILD_INDEX;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;

public class GameActivity extends BaseItemDetailsActivity<Game> implements BaseItemDetailsActivity.WebErrorHandler {

    private static final String X_POS_FIELD_NAME = "xPos";
    private static final String Y_POS_FIELD_NAME = "yPos";
    private static final String TYPE_FIELD_NAME = "type";

    private static final ArrayList<DBColumnInfo> GAME_POINTS_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("X pos", 20, DBColumnInfo.ColumnType.COLUMN_TEXT, AbstractGamePoint.class.getDeclaredField(X_POS_FIELD_NAME), null));
            add(new DBColumnInfo("Y pos", 20, DBColumnInfo.ColumnType.COLUMN_TEXT, AbstractGamePoint.class.getDeclaredField(Y_POS_FIELD_NAME), null));
            add(new DBColumnInfo("Type", 50, DBColumnInfo.ColumnType.COLUMN_REFERENCE, AbstractGamePoint.class.getDeclaredField(TYPE_FIELD_NAME), EDIT_ENTITY_TAG));
            add(new DBColumnInfo("", 10, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, DELETE_ENTITY_TAG, android.R.drawable.ic_delete));
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
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(ID: " + getItem().getId() + ", created: " +
                Utils.formatDateTime(getItem().getCreatedDate()) + ")");

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

        if(getItem() != null && getItem().getId() != null){
            showProgress();
            mMapFragment.InitMap(getItem(), this);
        }
    }

    protected void performEditAction(AbstractGamePoint item) {
        //TODO: edit point dialog
        //setItemChanged(true);
        showError("Not implemented yet");
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
        intentFilter.addAction(ACTION_ACTION_ADD_CHILD_RESPONSE);
        intentFilter.addAction(ACTION_ACTION_REMOVE_CHILD_RESPONSE);
        mMapFragment.setIntentFilters(intentFilter);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (mMapFragment.handleWebServiceResponseAction(intent))
            return  true;
        else if (intent.getAction().equals(ACTION_ACTION_ADD_CHILD_RESPONSE)) {
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null){
                NewPointRequest result = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
                AbstractGamePoint newPoint = new AbstractGamePoint(result);
                newPoint.setNextPointIndex(getItem().getGamePoints().size());
                getItem().getGamePoints().add(newPoint);
                tableFragment.setItems(getItem().getGamePoints()); //TODO: change logic
                setItemChanged(true);
                mMapFragment.updateMap();
            }
            else {
                showError(error);
            }

            return  true;
        }
        else if (intent.getAction().equals(ACTION_ACTION_REMOVE_CHILD_RESPONSE)) {
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null){
                int pos = intent.getIntExtra(EXTRA_CHILD_INDEX, -1);
                getItem().getGamePoints().remove(pos);
                tableFragment.setItems(getItem().getGamePoints()); //TODO: change logic
                setItemChanged(true);
                mMapFragment.updateMap();
            }
            else {
                showError(error);
            }

            return  true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_new).setVisible(true);
        menu.findItem(R.id.action_new).setTitle(R.string.new_game_point_caption);
        //menu.findItem(R.id.action_save).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new) {
            NewGamePointDialogFragment dialog = new NewGamePointDialogFragment();
            dialog.setNextPointIndex(getItem().getGamePoints().size() + 1);
            dialog.setMapWidth(mMapFragment.getBitmap().getWidth());
            dialog.setMapHeight(mMapFragment.getBitmap().getHeight());
            dialog.setDelegate(new DialogOnClickDelegate() {
                @Override
                public void doAction(Object result) {
                    showProgress();

                    ((NewPointRequest)result).setGameId(getItem().getId());
                    RestApiService.startActionAddChild(GameActivity.this,
                            getItem().getId(), AbstractGamePoint.urlForActionName(),
                            (NewPointRequest)result);
                }
            });
            dialog.show(getSupportFragmentManager(), "new_entity");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
