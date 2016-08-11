package com.cubegames.slava.cubegame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.GameMap;

import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_OBJECT;

public class GameMapActivity extends BaseActivityWithMenu {

    private BroadcastReceiver mGetImageBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_map);

        registerRestApiResponseReceivers();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        GameMap map = getIntent().getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
        if(map != null){
            setTitle(map.getName());
            showProgress();
            RestApiService.startActionGetMapImage(getApplicationContext(), map);
        }
    }

    @Override
    protected void registerRestApiResponseReceivers() {
        super.registerRestApiResponseReceivers();

        mGetImageBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgress();

                GameMap response = intent.getParcelableExtra(RestApiService.EXTRA_GAME_MAP_OBJECT);
                if (response.getBinaryData() != null) {
                    Bitmap mapImage = BitmapFactory.decodeByteArray(response.getBinaryData(), 0, response.getBinaryData().length);
                    ImageView mMapView = (ImageView) findViewById(R.id.map_image);
                    mMapView.setImageBitmap(mapImage);
                    mMapView.invalidate();
                } else {
                    Toast.makeText(GameMapActivity.this, "Can not download image.", Toast.LENGTH_LONG).show();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(RestApiService.ACTION_MAP_IMAGE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mGetImageBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mGetImageBroadcastReceiver);

        super.onDestroy();
    }
}
