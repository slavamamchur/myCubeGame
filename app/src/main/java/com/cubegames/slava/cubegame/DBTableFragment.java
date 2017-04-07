package com.cubegames.slava.cubegame;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cubegames.slava.cubegame.model.BasicNamedDbEntity;
import com.cubegames.slava.cubegame.model.GameInstance;
import com.cubegames.slava.cubegame.model.points.PointType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.cubegames.slava.cubegame.DBColumnInfo.ColumnType.COLUMN_BUTTON;
import static com.cubegames.slava.cubegame.DBColumnInfo.ColumnType.COLUMN_CHECK_BOX;
import static com.cubegames.slava.cubegame.DBColumnInfo.ColumnType.COLUMN_COLOR_BOX;
import static com.cubegames.slava.cubegame.DBColumnInfo.ColumnType.COLUMN_REFERENCE;

public class DBTableFragment extends Fragment {

    public static final String DELETE_ENTITY_TAG = "DELETE_ENTITY";

    public interface OnItemClickDelegate {
        void onClick(String tag, Object item);
        boolean isEnabled(String tag, Object item);
    }

    private OnItemClickDelegate onItemClickDelegate;

    private ListView dbTable;
    private LinearLayout header;
    private List<DBColumnInfo> columns;
    private Integer captionColor = null;
    private Integer selectedPos = -1;

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

    public void initTable(List<DBColumnInfo> columns, OnItemClickDelegate onItemClickDelegate) {
        this.columns = columns;
        this.onItemClickDelegate = onItemClickDelegate;

        initHeaders();

        dbTable.setAdapter(getAdapter());
    }

    private void initHeaders() {
        header.removeAllViews();

        if (columns != null)
            for (DBColumnInfo column : columns) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(0, MATCH_PARENT);
                lparams.weight = column.getWeight();

                TextView caption = new TextView(getContext());
                caption.setText(column.getType().equals(COLUMN_BUTTON) ? "" : column.getCaption());
                caption.setTextSize(18);
                caption.setTypeface(null, Typeface.BOLD);
                if (!column.getType().equals(COLUMN_BUTTON) && column.getIconResId() > 0)
                    caption.setBackgroundResource(column.getIconResId());
                if (captionColor != null)
                    caption.setTextColor(0xFF000000 | captionColor);

                header.addView(caption, lparams);
            }
    }

    private ArrayAdapter<BasicNamedDbEntity> getAdapter() {

        return new ArrayAdapter<BasicNamedDbEntity> (getContext(), android.R.layout.simple_list_item_1) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                ArrayList<View> holder;
                final Object item  = getItem(position);

                if (row == null) {
                    row = createRowView(position);
                    holder = createHolder((LinearLayout) row);
                    row.setTag(holder);
                }
                else
                    holder = (ArrayList<View>) row.getTag();

                row.setBackgroundColor(position == selectedPos ? 0x70000000 : 0);

                fillHolder(holder, item);

                return row;
            }
        };
    }

    private ArrayList<View> createHolder(LinearLayout row) {
        ArrayList<View> holder = new ArrayList<>();

        for (int i = 0; i < row.getChildCount(); i++)
            holder.add(row.getChildAt(i));

        return holder;
    }

    private void fillHolder(ArrayList<View> holder, final Object item) {
        View.OnClickListener onClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickDelegate != null)
                            onItemClickDelegate.onClick((String) v.getTag(), item);
                    }
                };

        for (int i = 0; i < holder.size(); i++) {
            fillColumnData(holder.get(i), columns.get(i), item);

            if (COLUMN_BUTTON.equals(columns.get(i).getType()) || COLUMN_REFERENCE.equals(columns.get(i).getType()))
                holder.get(i).setOnClickListener(onClickListener);
        }
    }

    private LinearLayout createRowView(int pos) {
        LinearLayout row = new LinearLayout(getContext());
        row.setWeightSum(100);
        row.setOrientation(LinearLayout.HORIZONTAL);

        if (columns != null)
            for (DBColumnInfo column : columns) {
                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(0, MATCH_PARENT);
                lparams.weight = column.getWeight();

                View col;
                if (column.getType().equals(COLUMN_CHECK_BOX)) {
                    col = new CheckBox(getContext());
                    ((CheckBox)col).setGravity(Gravity.CENTER);
                }
                else if (!column.getType().equals(COLUMN_BUTTON)) {
                    col = new TextView(getContext());
                    ((TextView)col).setTextSize(18);
                    //col.setPadding(8, 0, 0, 0);
                    ((TextView)col).setGravity(Gravity.CENTER_VERTICAL);
                }
                else {
                    //ContextThemeWrapper newContext = new ContextThemeWrapper(getContext(), android.R.style.Theme_DeviceDefault);
                    col = new Button(getContext());
                    col.setPadding(0, 0, 5, 0);

                    if (column.getIconResId() < 0) {
                        ((Button) col).setText(column.getCaption());
                        col.setBackgroundResource(android.R.drawable.btn_default_small);
                    }
                    else {
                        col = new ImageButton(getContext());
                        col.setBackgroundResource(android.R.drawable.btn_default_small);
                        ((ImageButton) col).setImageDrawable(
                                getResources().getDrawable(column.getIconResId()));
                    }

                }

                col.setTag(column.getTAG());

                row.addView(col, lparams);
            }

        return row;
    }

    public void fillColumnData(View view, DBColumnInfo column, Object item) {
        try {
            Object value = column.getDataField() != null ? column.getDataField().get(item) : "";

            if (column.getType().equals(COLUMN_CHECK_BOX))
                ((CheckBox)view).setChecked((Boolean)value);
            else if (column.getType().equals(COLUMN_COLOR_BOX))
                view.setBackgroundColor(0xFF000000 | (Integer) value);
            else
                if (!column.getType().equals(COLUMN_BUTTON)) {
                    ((TextView) view).setText(getStringValue(value));
                    Integer color = getColorFromRef(column, item);
                    if (color != null)
                        ((TextView) view).setTextColor(color);
                }
                else
                    if (DELETE_ENTITY_TAG.equals(column.getTAG()) && (item instanceof BasicNamedDbEntity))
                        view.setEnabled(((BasicNamedDbEntity)item).getTenantId() != null);
                    else if (onItemClickDelegate != null)
                            view.setEnabled(onItemClickDelegate.isEnabled(column.getTAG(), item));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private Integer getColorFromRef(DBColumnInfo column, Object item) {
        Object colorRef = column.getColorRef();
        if (colorRef == null)
            return null;
        else if (colorRef instanceof Integer)
            return 0xFF000000 | (Integer) colorRef;
        else
            try {
                return 0xFF000000 | (Integer) ((Field) colorRef).get(item);
            } catch (IllegalAccessException e) {
                return null;
            }
    }

    private String getStringValue(Object value){
        if (value instanceof Long)
            return Utils.formatDateTime((Long) value);
        else if (value instanceof List)
            return String.valueOf(((List) value).size());
        else if (value instanceof GameInstance.State)
            return ((GameInstance.State) value).name();
        else if (value instanceof PointType)
            return ((PointType) value).name();
        else if (value instanceof Integer)
            return String.format("%d", value);
        else
            return (String)value;
    }

    public void setItems(Collection items) {
        ArrayAdapter adapter = (ArrayAdapter) dbTable.getAdapter();
        adapter.clear();
        if (items != null)
            adapter.addAll(items);
        adapter.notifyDataSetChanged();
    }

    public void selecItem(int pos) {
       selectedPos = pos;
    }

    public void setCaptionColor(Integer captionColor) {
        this.captionColor = captionColor;
    }
}
