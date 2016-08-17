package com.cubegames.slava.cubegame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.GameMap;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.api.RestApiService.EXTRA_GAME_MAP_OBJECT;

public class GameMapsListActivity extends BaseListActivity<GameMap> {

    private BroadcastReceiver mGetMapsListBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerRestApiResponseReceivers() {
        super.registerRestApiResponseReceivers();

        mGetMapsListBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgress();

                ArrayList<GameMap> mapList  = intent.getParcelableArrayListExtra(RestApiService.EXTRA_GAME_MAP_LIST);
                setItems(mapList);
            }
        };
        IntentFilter intentFilter = new IntentFilter(RestApiService.ACTION_GAME_MAP_LIST_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mGetMapsListBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mGetMapsListBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    protected void getData() {
        RestApiService.startActionGetMapList(getApplicationContext());
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
                GameMap item = getItems().get(position);

                Intent mintent = new Intent(getApplicationContext(), GameMapActivity.class);
                mintent.putExtra(EXTRA_GAME_MAP_OBJECT, item);
                startActivity(mintent);
            }
        };

    }
}
