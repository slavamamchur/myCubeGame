package com.sadgames.dicegame.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.dicegame.gamelogic.server.rest_api.model.entities.DbPlayerEntity;
import com.sadgames.dicegame.gamelogic.server.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.ui.colorpicker.AmbilWarnaDialogFragment;
import com.sadgames.dicegame.ui.colorpicker.OnAmbilWarnaListener;
import com.sadgames.dicegame.ui.framework.BaseListActivity;
import com.sadgames.dicegame.ui.framework.DBColumnInfo;

import java.util.ArrayList;

import static com.sadgames.dicegame.gamelogic.client.GameConst.ACTION_GET_PLAYER_LIST;
import static com.sadgames.dicegame.gamelogic.client.GameConst.ACTION_LIST_RESPONSE;
import static com.sadgames.dicegame.gamelogic.client.GameConst.ACTION_SAVE_ENTITY_RESPONSE;
import static com.sadgames.dicegame.gamelogic.client.GameConst.EXTRA_ENTITY_OBJECT;
import static com.sadgames.dicegame.gamelogic.client.GameConst.EXTRA_ERROR_OBJECT;
import static com.sadgames.dicegame.gamelogic.client.GameConst.EXTRA_PLAYER_LIST;
import static com.sadgames.dicegame.ui.framework.DBTableFragment.DELETE_ENTITY_TAG;

public class DBPlayersListActivity extends BaseListActivity<DbPlayerEntity> {

    public static int ACTION_DICTIONARY = 3;
    public static final String EXTRA_STARTED_AS_DICTIONARY = "EXTRA_STARTED_AS_DICTIONARY";

    public static final String COLOR_FIELD_NAME = "color";

    private static final ArrayList<DBColumnInfo> PLAYERS_LIST_COLUMN_INFO = new ArrayList<DBColumnInfo>() {{
        try {
            add(new DBColumnInfo("Name", 80, DBColumnInfo.ColumnType.COLUMN_REFERENCE, DbPlayerEntity.class.getField(NAME_FIELD_NAME), EDIT_ENTITY_TAG));
            add(new DBColumnInfo("Color", 10, DBColumnInfo.ColumnType.COLUMN_COLOR_BOX, DbPlayerEntity.class.getDeclaredField(COLOR_FIELD_NAME), null));
            add(new DBColumnInfo("Remove", 10, DBColumnInfo.ColumnType.COLUMN_BUTTON, null, DELETE_ENTITY_TAG));
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }};

    private boolean isDictionary = false;

    @Override
    protected ArrayList<DBColumnInfo> getColumnInfo() {
        return PLAYERS_LIST_COLUMN_INFO;
    }
    @Override
    protected String getListAction() {
        return ACTION_GET_PLAYER_LIST;
    }
    @Override
    protected String getListResponseAction() {
        return ACTION_LIST_RESPONSE;
    }
    @Override
    protected String getListResponseExtra() {
        return EXTRA_PLAYER_LIST;
    }
    @Override
    protected String getEntityExtra() {
        return EXTRA_ENTITY_OBJECT;
    }
    @Override
    protected Class<?> getDetailsActivityClass() {
        return null;
    }
    @Override
    protected DbPlayerEntity getNewItem() {
        return new DbPlayerEntity();
    }
    @Override
    protected String getNewItemActionName() {
        return getString(R.string.add_new_player_caption);
    }
    @Override
    protected int getCaptionResource() {
        return R.string.players_list_title;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        isDictionary = getIntent().getBooleanExtra(EXTRA_STARTED_AS_DICTIONARY, false);
    }

    @Override
    protected void performEditAction(BasicNamedDbEntity item) {
        if (isDictionary) {
            Intent intent = new Intent();
            intent.putExtra(getEntityExtra(), item);
            setResult(Activity.RESULT_OK, intent);

            finish();
        }
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SAVE_ENTITY_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);
            if (error == null){
                showProgress();
                getData();
            }
            else {
                showError(error);
            }

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_maps_list).setVisible(true);
        menu.findItem(R.id.action_game_instances_list).setVisible(true);
        menu.findItem(R.id.action_games_list).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void handleActionNew() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(0xFF000000);
        fragment.setOnAmbilWarnaListener(new OnAmbilWarnaListener() {
                                             @Override
                                             public void onCancel(AmbilWarnaDialogFragment dialogFragment) {}

                                             @Override
                                             public void onOk(AmbilWarnaDialogFragment dialogFragment, int color, String name) {
                                                 DbPlayerEntity newItem = getNewItem();
                                                 newItem.setName(name);
                                                 newItem.setColor(color);

                                                 createEntity(newItem);
                                             }
                                         }
        );

        fragment.show(ft, "color_picker_dialog");
    }
}
