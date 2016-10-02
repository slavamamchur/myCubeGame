package com.cubegames.slava.cubegame;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;

import java.util.Collection;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class DBTableFragment extends Fragment {

    private ListView dbTable;
    private LinearLayout header;
    private List<DBColumnInfo> columns;

    public DBTableFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.db_table_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbTable = (ListView) view.findViewById(R.id.list_view);
        header = (LinearLayout) view.findViewById(R.id.header_view);
    }

    public void initTable(List<DBColumnInfo> columns) {
        this.columns = columns;

        initHeaders();
    }

    private void initHeaders() {
        header.removeAllViews();

        if (columns != null)
            for (DBColumnInfo column : columns) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(0, MATCH_PARENT);
                lparams.weight = column.getWeight();

                TextView caption = new TextView(getContext());
                caption.setText(column.getCaption());
                caption.setTextSize(18);
                caption.setTypeface(null, Typeface.BOLD);

                header.addView(caption, lparams);
            }
    }

    public void fillColumnData(TextView view, DBColumnInfo column, BasicNamedDbEntity item) {
        try {
            Object value = column.getDataField().get(item);

            if (column.getType().equals(DBColumnInfo.ColumnType.COLUMN_COLOR_BOX))
                view.setBackgroundColor((Integer) value);
            else
                view.setText((String) value);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setItems(Collection items) {
        ArrayAdapter adapter = (ArrayAdapter) getDbTable().getAdapter();
        adapter.clear();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }

    public ListView getDbTable() {
        return dbTable;
    }

}
