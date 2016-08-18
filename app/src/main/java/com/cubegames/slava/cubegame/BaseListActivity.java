package com.cubegames.slava.cubegame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;

import java.util.ArrayList;

public abstract class BaseListActivity<T extends BasicNamedDbEntity> extends BaseActivityWithMenu {
    private ArrayList<T> items = new ArrayList<>();
    private ListView listView;
    private BroadcastReceiver mGetListBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_list);

        setCaption(getCaptionResource());

        listView = (ListView) this.findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<T>(this, android.R.layout.simple_list_item_1, getItems()){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;

                if (view == null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(BaseListActivity.this);
                    view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setText(getItem(position).getName());

                return view;
            }
        });
        listView.setOnItemClickListener(getOnItemClickListener());

        registerRestApiResponseReceivers();

        showProgress();

        getData();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mGetListBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    protected void registerRestApiResponseReceivers() {
        super.registerRestApiResponseReceivers();

        mGetListBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideProgress();

                ArrayList<T> lst  = intent.getParcelableArrayListExtra(getResponseExtra());
                setItems(lst);
            }
        };
        IntentFilter intentFilter = new IntentFilter(getResponseAction());
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mGetListBroadcastReceiver, intentFilter);
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;

        ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
        adapter.clear();
        adapter.addAll(getItems());
        adapter.notifyDataSetChanged();
    }

    protected abstract String getListAction();
    protected abstract String getResponseAction();
    protected abstract String getResponseExtra();
    protected abstract String getEntityExtra();
    protected abstract Class<?> getActivityClass();

    protected void getData(){
        RestApiService.startActionGetList(getApplicationContext(), getListAction());
    }

    protected AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
                T item = getItems().get(position);

                Intent intent = new Intent(getApplicationContext(), getActivityClass());
                intent.putExtra(getEntityExtra(), item);
                startActivity(intent);
            }
        };

    }

    protected abstract int getCaptionResource();
    private void setCaption(int resourceID){
        setTitle(getResources().getString(resourceID));
    }
}
