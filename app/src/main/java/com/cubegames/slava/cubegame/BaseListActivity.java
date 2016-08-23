package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cubegames.slava.cubegame.api.RestApiService;
import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;
import com.cubegames.slava.cubegame.model.ErrorEntity;

import java.util.ArrayList;

import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_DELETE_ENTITY_RESPONSE;

public abstract class BaseListActivity<T extends BasicNamedDbEntity> extends BaseActivityWithMenu {
    private ArrayList<T> items = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_list);

        setCaption(getCaptionResource());

        listView = (ListView) this.findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<T>(this, getListItemViewID(), getItems()){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                ItemHolder holder;

                if (row == null) {
                    LayoutInflater layoutInflater = LayoutInflater.from(BaseListActivity.this);
                    row = layoutInflater.inflate(getListItemViewID(), parent, false);

                    holder = new ItemHolder();
                    holder.textName = (TextView) row.findViewById(getListItemTextID());
                    if(getListItemDeleteBtnID() >= 0){
                        holder.btnDelete = (Button) row.findViewById(getListItemDeleteBtnID());
                    }
                    if(getListItemUserActionBtnID() >= 0){
                        holder.btnUserAction = (Button) row.findViewById(getListItemUserActionBtnID());
                    }

                    row.setTag(holder);
                }
                else {
                    holder = (ItemHolder) row.getTag();
                }

                final T item  = getItem(position);
                holder.textName.setText(item.getName());
                holder.textName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), getDetailsActivityClass());
                        intent.putExtra(getEntityExtra(), item);
                        startActivity(intent);
                    }
                });
                //todo:enabled by created user
                if (holder.btnDelete != null){
                    holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProgress();
                            RestApiService.startActionDeleteEntity(getApplicationContext(), item);
                        }
                    });
                }
                if (holder.btnUserAction != null){
                    holder.btnUserAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doUserAction(item);
                        }
                    });
                }

                return row;
            }

            class ItemHolder {
                TextView textName;
                Button btnUserAction = null;
                Button btnDelete = null;
            }
        });
    }

    protected int getListItemViewID(){
        return android.R.layout.simple_list_item_1;
    }
    protected int getListItemTextID(){
        return android.R.id.text1;
    }
    protected int getListItemDeleteBtnID(){
        return -1;
    }
    protected int getListItemUserActionBtnID(){
        return -1;
    }
    protected void doUserAction(BasicNamedDbEntity item){}

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        showProgress();

        getData();
    }

    @Override
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_DELETE_ENTITY_RESPONSE);
        intentFilter.addAction(getListResponseAction());

        return intentFilter;
    }

    @Override
    protected boolean handleWebServiceResponseAction(Context context, Intent intent) {
        if (intent.getAction().equals(getListResponseAction())){
            ArrayList<T> lst  = intent.getParcelableArrayListExtra(getListResponseExtra());
            setItems(lst);

            return true;
        }
        else if (intent.getAction().equals(ACTION_DELETE_ENTITY_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null){
                showProgress();
                getData();
            }
            else {
                //todo: error message
            }

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
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
    protected abstract String getListResponseAction();
    protected abstract String getListResponseExtra();
    protected abstract String getEntityExtra();
    protected abstract Class<?> getDetailsActivityClass();

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

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sync:
                showProgress();
                getData();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
