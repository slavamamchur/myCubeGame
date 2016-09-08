package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_SAVE_ENTITY_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_ENTITY_OBJECT;

public abstract class BaseItemDetailsActivity<T extends BasicNamedDbEntity> extends BaseActivityWithMenu {

    private boolean itemChanged = false;
    private T item;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        item = getIntent().getParcelableExtra(getItemExtra());

        setTitle(item.getName());
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
        if (!isItemChanged())
            finish();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.save_confirm_text)
                    .setTitle(R.string.confirm_title);
            builder.setPositiveButton(R.string.yes_btn_caption, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    saveItem();
                }
            });
            builder.setNegativeButton(R.string.no_btn_caption, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.setNeutralButton(R.string.cancel_btn_caption, null);

            AlertDialog dialog = builder.create();
            showAlert(dialog);
        }
    }

    private boolean saveItem(){
        showProgress();

        RestApiService.startActionSaveEntity(getApplicationContext(), item);

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
        //todo: error and create
        if (intent.getAction().equals(ACTION_SAVE_ENTITY_RESPONSE)){
            item = intent.getParcelableExtra(EXTRA_ENTITY_OBJECT);

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    public boolean isItemChanged() {
        return itemChanged || item.getId() == null;
    }
    public T getItem() {
        return item;
    }
    protected abstract String getItemExtra();
}
