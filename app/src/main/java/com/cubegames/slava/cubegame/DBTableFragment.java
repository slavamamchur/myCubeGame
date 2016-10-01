package com.cubegames.slava.cubegame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DBTableFragment extends Fragment {

    public DBTableFragment() {}

    private ListView dbTable;
    private LinearLayout header;
    private int header_layout_id;

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

    public void initTable(){
        header.addView(LayoutInflater.from(getContext()).inflate( header_layout_id, null, false));
    }

    public ListView getDbTable() {
        return dbTable;
    }
    public void setHeader_layout_id(int header_layout_id) {
        this.header_layout_id = header_layout_id;
    }
}
