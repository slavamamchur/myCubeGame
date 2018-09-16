package com.sadgames.dicegame.ui.framework;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.RestApiService;
import com.sadgames.dicegame.ui.DBPlayersListActivity;
import com.sadgames.dicegame.ui.GameInstanceListActivity;
import com.sadgames.dicegame.ui.GameListActivity;
import com.sadgames.dicegame.ui.GameMapsListActivity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.ErrorEntity;

import java.util.ArrayList;

import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_DELETE_ENTITY_RESPONSE;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.ACTION_SAVE_ENTITY_RESPONSE;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.EXTRA_ENTITY_OBJECT;
import static com.sadgames.gl3dengine.gamelogic.client.GameConst.EXTRA_ERROR_OBJECT;

public abstract class BaseListActivity<T extends BasicNamedDbEntity> extends BaseActivityWithMenu {

    public static final String NAME_FIELD_NAME = "name";
    public static final String CREATED_DATE_FIELD_NAME = "createdDate";

    public static final String EDIT_ENTITY_TAG = "EDIT_ENTITY";

    private DBTableFragment tableFragment;
    private ArrayList<T> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_list);
        setCaption(getCaptionResource());

        tableFragment = (DBTableFragment) getSupportFragmentManager().findFragmentById(R.id.table_fragment);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        DBTableFragment.OnItemClickDelegate onItemClickDelegate = new DBTableFragment.OnItemClickDelegate() {
            @Override
            public void onClick(String tag, Object item) {
                if (EDIT_ENTITY_TAG.equals(tag)) {
                    performEditAction((T)item);
                }
                else if (DBTableFragment.DELETE_ENTITY_TAG.equals(tag)) {
                    showProgress();
                    RestApiService.startActionDeleteEntity(getApplicationContext(), (T)item);
                }
                else
                    doUserAction((T) item, tag);
            }

            @Override
            public boolean isEnabled(String tag, Object item) {
                return isUserButtonEnabled(tag, (T)item);
            }
        };

        tableFragment.initTable(getColumnInfo(), onItemClickDelegate);

        showProgress();
        getData();
    }

    protected void performEditAction(BasicNamedDbEntity item) {
        Intent intent = new Intent(getApplicationContext(), getDetailsActivityClass());
        intent.putExtra(getEntityExtra(), item);
        startActivityForResult(intent, BaseItemDetailsActivity.EDITOR_REQUEST);
    }

    protected boolean isUserButtonEnabled(String tag, BasicNamedDbEntity item) {
        return true;
    }

    protected ArrayList<DBColumnInfo> getColumnInfo() {
        return null;
    }

    protected void doUserAction(T item, String tag){}

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_DELETE_ENTITY_RESPONSE);
        intentFilter.addAction(ACTION_SAVE_ENTITY_RESPONSE);
        intentFilter.addAction(getListResponseAction());

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(getListResponseAction())){
            ArrayList<T> lst  = (ArrayList<T>) intent.getSerializableExtra(getListResponseExtra());
            tableFragment.setItems(lst);
            items = lst;

            return true;
        }
        else if (intent.getAction().equals(ACTION_DELETE_ENTITY_RESPONSE)){
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
        else if (intent.getAction().equals(ACTION_SAVE_ENTITY_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(EXTRA_ERROR_OBJECT);
            if (error == null){
                Intent mIntent = new Intent(getApplicationContext(), getDetailsActivityClass());
                mIntent.putExtra(getEntityExtra(), intent.getParcelableExtra(EXTRA_ENTITY_OBJECT));
                mIntent.putExtra(BaseItemDetailsActivity.EXTRA_ENTITY_CHANGED, true);
                startActivityForResult(mIntent, BaseItemDetailsActivity.EDITOR_REQUEST);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == BaseItemDetailsActivity.EDITOR_RESULT_OK){
            ///showProgress();
            getData();
            //tableFragment.setItems(items);
        }
    }

    protected abstract String getListAction();
    protected abstract String getListResponseAction();
    protected abstract String getListResponseExtra();
    protected abstract String getEntityExtra();
    protected abstract Class<?> getDetailsActivityClass();
    protected abstract T getNewItem();
    protected abstract String getNewItemActionName();

    protected void getData(){
        RestApiService.startActionGetList(getApplicationContext(), getListAction());
    }

    protected abstract int getCaptionResource();
    private void setCaption(int resourceID){
        setTitle(getResources().getString(resourceID));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_sync).setVisible(true);
        menu.findItem(R.id.action_new).setVisible(getNewItemActionName() != null);
        menu.findItem(R.id.action_new).setTitle(getNewItemActionName());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sync:
                showProgress();
                getData();

                return true;
            case R.id.action_new:
                handleActionNew();

                return true;
            case R.id.action_maps_list:
                startActivity(new Intent(getApplicationContext(), GameMapsListActivity.class));
                finish();

                return true;
            case R.id.action_games_list:
                startActivity(new Intent(getApplicationContext(), GameListActivity.class));
                finish();

                return true;
            case R.id.action_game_instances_list:
                startActivity(new Intent(getApplicationContext(), GameInstanceListActivity.class));
                finish();

                return true;
            case R.id.action_dbplayers_list:
                startActivity(new Intent(getApplicationContext(), DBPlayersListActivity.class));
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleActionNew(){
        InputNameDialogFragment dialog = new InputNameDialogFragment();
        dialog.setDelegate(new DialogOnClickDelegate() {
            @Override
            public void doAction(Object result) {
                T newItem = getNewItem();
                newItem.setName((String)result);
                createEntity(newItem);
            }
        });
        dialog.show(getSupportFragmentManager(), "new_entity");
    }

    public void createEntity(T newItem){
        showProgress();

        RestApiService.startActionSaveEntity(getApplicationContext(), newItem, ACTION_SAVE_ENTITY_RESPONSE);
    }
}
