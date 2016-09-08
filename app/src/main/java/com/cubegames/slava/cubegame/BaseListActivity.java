package com.cubegames.slava.cubegame;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import static com.cubegames.slava.cubegame.api.RestApiService.ACTION_SAVE_ENTITY_RESPONSE;

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
                ListItemHolder holder;
                final T item  = position == 0 ? null : getItem(position);

                if (row == null) {
                    holder = createHolder();
                    LayoutInflater layoutInflater = LayoutInflater.from(BaseListActivity.this);
                    if (position == 0)
                        row = layoutInflater.inflate( getListHeaderID(), parent, false);
                    else {
                        row = layoutInflater.inflate(getListItemViewID(), parent, false);
                        initHolder(row, holder, item);
                    }

                    row.setTag(holder);
                }
                else {
                    holder = (ListItemHolder) row.getTag();
                }

                if (position > 0)
                    fillHolder(holder, item);

                return row;
            }

        });
    }

    protected int getListItemViewID(){
        return android.R.layout.simple_list_item_1;
    }
    protected int getListHeaderID(){ return -1;}
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
    protected ListItemHolder createHolder(){
        return new ListItemHolder();
    }
    protected void initHolder(View row, ListItemHolder holder, final T item){
        holder.textName = (TextView) row.findViewById(getListItemTextID());

        if(getListItemDeleteBtnID() >= 0){
            holder.btnDelete = (Button) row.findViewById(getListItemDeleteBtnID());
        }
        if(getListItemUserActionBtnID() >= 0){
            holder.btnUserAction = (Button) row.findViewById(getListItemUserActionBtnID());
        }

        holder.textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), getDetailsActivityClass());
                intent.putExtra(getEntityExtra(), item);
                startActivity(intent);
            }
        });
        if (holder.btnDelete != null){
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgress();
                    RestApiService.startActionDeleteEntity(getApplicationContext(), item);
                }
            });
            holder.btnDelete.setEnabled(item.getTenantId() != null);
        }
        if (holder.btnUserAction != null){
            holder.btnUserAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doUserAction(item);
                }
            });
        }
    }
    protected  void fillHolder(ListItemHolder holder, final T item){
        holder.textName.setText(item.getName());
    }

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
        intentFilter.addAction(ACTION_SAVE_ENTITY_RESPONSE);
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
                showError(error);
            }

            return true;
        }
        else if (intent.getAction().equals(ACTION_SAVE_ENTITY_RESPONSE)){
            ErrorEntity error = intent.getParcelableExtra(RestApiService.EXTRA_ERROR_OBJECT);
            if (error == null){
                Intent mIntent = new Intent(getApplicationContext(), getDetailsActivityClass());
                mIntent.putExtra(getEntityExtra(), intent.getParcelableExtra(RestApiService.EXTRA_ENTITY_OBJECT));
                startActivity(mIntent);
            }
            else {
                showError(error);
            }

            return true;
        }
        else
            return super.handleWebServiceResponseAction(context, intent);
    }

    protected void showError(ErrorEntity error){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error.getError())
                .setTitle(R.string.error_title);
        builder.setPositiveButton(R.string.pos_btn_caption, null);

        AlertDialog dialog = builder.create();
        showAlert(dialog);
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items  = new ArrayList<>();
        if (items.size() > 0)
            this.items.add(items.get(0));
        this.items.addAll(items);

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
                new InputNameDialogFragment().show(getSupportFragmentManager(),
                        "new_entity");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createEntity(String entityName){
        showProgress();

        T newItem = getNewItem();
        newItem.setName(entityName);

        RestApiService.startActionSaveEntity(getApplicationContext(), newItem);
    }
}
