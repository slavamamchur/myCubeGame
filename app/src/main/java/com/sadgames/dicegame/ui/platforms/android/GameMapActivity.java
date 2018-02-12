package com.sadgames.dicegame.ui.platforms.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.sadgames.dicegame.R;
import com.sadgames.dicegame.rest_api.model.entities.ErrorEntity;
import com.sadgames.dicegame.rest_api.model.entities.GameMapEntity;
import com.sadgames.dicegame.ui.platforms.android.framework.BaseItemDetailsActivity;
import com.sadgames.dicegame.ui.platforms.android.framework.MapFragment;
import com.sadgames.sysutils.common.DateTimeUtils;

import static com.sadgames.dicegame.rest_api.RestApiService.EXTRA_ENTITY_OBJECT;

public class GameMapActivity extends BaseItemDetailsActivity<GameMapEntity> implements BaseItemDetailsActivity.WebErrorHandler {

    public static final int UPLOAD_MAP_IMAGE_ACTION = 2;

    private MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_map);

        mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getItem().getName() + "(ID: " + getItem().getId() + ", created: " +
                DateTimeUtils.formatDateTime(getItem().getCreatedDate()) + ")");

        if(getItem() != null && getItem().getId() != null){
            showProgress();
            mMapFragment.InitMap(getItem(), this);
        }
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        mMapFragment.setIntentFilters(intentFilter);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (mMapFragment.handleWebServiceResponseAction(intent))
            return  true;
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    @Override
    protected String getItemExtra() {
        return EXTRA_ENTITY_OBJECT;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_upload_file).setVisible(true);
        menu.findItem(R.id.action_upload_file).setEnabled(getItem().getTenantId() != null);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_upload_file:
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        UPLOAD_MAP_IMAGE_ACTION);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPLOAD_MAP_IMAGE_ACTION && resultCode == Activity.RESULT_OK && null != data)
            mMapFragment.saveMapImage(data, getItem());
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onError(ErrorEntity error) {
        showError(error);
    }
}
