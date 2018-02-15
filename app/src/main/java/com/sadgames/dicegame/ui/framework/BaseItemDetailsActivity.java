package com.sadgames.dicegame.ui.framework;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.sadgames.dicegame.RestApiService;
import com.sadgames.dicegame.game.server.rest_api.model.entities.BasicNamedDbEntity;
import com.sadgames.dicegame.game.server.rest_api.model.entities.ErrorEntity;

import static com.sadgames.dicegame.RestApiService.ACTION_SAVE_ENTITY_RESPONSE;
import static com.sadgames.dicegame.RestApiService.EXTRA_ENTITY_OBJECT;

public abstract class BaseItemDetailsActivity<T extends BasicNamedDbEntity> extends BaseActivityWithMenu {

    private boolean itemChanged = false;
    private T item;

    public static int EDITOR_REQUEST = 1;
    public static int EDITOR_RESULT_OK = 1;
    public static int EDITOR_RESULT_CANCEL = 2;

    public static final String EXTRA_ENTITY_CHANGED = "EXTRA_ENTITY_CHANGED";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        item = getIntent().getParcelableExtra(getItemExtra());
        setItemChanged(getIntent().getBooleanExtra(EXTRA_ENTITY_CHANGED, false));

        setTitle(item.getName() + "(ID: " + item.getId() + ")");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.action_save).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            doExit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doExit(){
        if (!isItemChanged()) {
            setResult(EDITOR_RESULT_CANCEL);
            finish();
        }
        else {
            setResult(EDITOR_RESULT_OK);
            finish();

//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage(R.string.save_confirm_text)
//                    .setTitle(R.string.confirm_title);
//            builder.setPositiveButton(R.string.yes_btn_caption, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    saveItem();
//                }
//            });
//            builder.setNegativeButton(R.string.no_btn_caption, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    setResult(EDITOR_RESULT_OK);
//                    finish();
//                }
//            });
//            builder.setNeutralButton(R.string.cancel_btn_caption, null);
//
//            AlertDialog dialog = builder.create();
//            showAlert(dialog);
        }
    }

    private boolean saveItem(){
        showProgress();

        RestApiService.startActionSaveEntity(getApplicationContext(), item, ACTION_SAVE_ENTITY_RESPONSE);

        return true;
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_SAVE_ENTITY_RESPONSE);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SAVE_ENTITY_RESPONSE)){
            item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);
            setItemChanged(true);
            finish();

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    public boolean isItemChanged() {
        return itemChanged;
    }
    public void setItemChanged(boolean itemChanged) {
        this.itemChanged = itemChanged;
    }

    public T getItem() {
        return item;
    }
    public void setItem(T item) {
        this.item = item;
    }
    protected abstract String getItemExtra();

    public interface WebErrorHandler {
        void onError(ErrorEntity error);
    }
}
