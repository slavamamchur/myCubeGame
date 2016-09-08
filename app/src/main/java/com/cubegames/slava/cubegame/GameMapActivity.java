package com.cubegames.slava.cubegame;

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

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_MAP_IMAGE_RESPONSE;
import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_OBJECT;

public class GameMapActivity extends BaseItemDetailsActivity<GameMap> {

    //todo: implement UI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_map);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setTitle(getString(R.string.edit_map_caption));

        if(getItem() != null && getItem().getId() != null){
            showProgress();
            RestApiService.startActionGetMapImage(getApplicationContext(), getItem());
        }
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_MAP_IMAGE_RESPONSE);

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_MAP_IMAGE_RESPONSE)){
            GameMap response = intent.getParcelableExtra(EXTRA_GAME_MAP_OBJECT);
            if (response.getBinaryData() != null) {
                Bitmap mapImage = BitmapFactory.decodeByteArray(response.getBinaryData(), 0, response.getBinaryData().length);
                ImageView mMapView = (ImageView) findViewById(R.id.map_image);
                mMapView.setImageBitmap(mapImage);
                mMapView.invalidate();
            } else {
                Toast.makeText(this, "Can not download image.", Toast.LENGTH_LONG).show();
            }

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    @Override
    protected String getItemExtra() {
        return EXTRA_GAME_MAP_OBJECT;
    }
}
