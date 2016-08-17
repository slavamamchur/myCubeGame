package com.cubegames.slava.cubegame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;

import java.util.ArrayList;

public abstract class BaseListActivity<T extends BasicNamedDbEntity> extends BaseActivityWithMenu {
    private ArrayList<T> items = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_list);

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

    protected abstract void getData();
    protected abstract OnItemClickListener getOnItemClickListener();
}
