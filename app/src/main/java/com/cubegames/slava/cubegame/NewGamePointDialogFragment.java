package com.cubegames.slava.cubegame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.cubegames.slava.cubegame.model.points.NewPointRequest;
import com.cubegames.slava.cubegame.model.points.PointType;


public class NewGamePointDialogFragment extends DialogFragment {

    private View form = null;
    private NumberPicker xPosPicker;
    private NumberPicker yPosPicker;
    private Spinner pointTypeSpinner;
    private SeekBar flyIndexSeekBar;

    private DialogOnClickDelegate delegate = null;
    private int nextPointIndex = -1;
    private int mapWidth = 0;
    private int mapHeight = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        form = getActivity().getLayoutInflater().inflate(R.layout.new_gamepoint_dialog, null);

        xPosPicker = (NumberPicker) form.findViewById(R.id.xpos_picker);
        yPosPicker = (NumberPicker) form.findViewById(R.id.ypos_picker);
        xPosPicker.setMaxValue(mapWidth);
        yPosPicker.setMaxValue(mapHeight);
        xPosPicker.setValue(0);
        yPosPicker.setValue(0);

        pointTypeSpinner = (Spinner) form.findViewById(R.id.spinner_point_type);
        ArrayAdapter<?> adapter = new ArrayAdapter<Object>(getActivity(),
                android.R.layout.simple_spinner_item, PointType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointTypeSpinner.setAdapter(adapter);
        if (nextPointIndex > 1)
            pointTypeSpinner.setSelection(1);
        pointTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {

                flyIndexSeekBar.setEnabled(
                        PointType.FLY_BACK.equals(PointType.values()[selectedItemPosition])
                        || PointType.FLY_FORWARD.equals(PointType.values()[selectedItemPosition]));
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        flyIndexSeekBar = (SeekBar) form.findViewById(R.id.seekbar_fly_index);
        flyIndexSeekBar.setEnabled(false);
        flyIndexSeekBar.setMax(100);
        flyIndexSeekBar.setProgress(nextPointIndex);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder.setTitle(R.string.new_gamepoint_dialog_title).setView(form)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null).create();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    NewPointRequest result = new NewPointRequest();
                    result.setxPos(xPosPicker.getValue());
                    result.setyPos(yPosPicker.getValue());
                    result.setType(PointType.values()[pointTypeSpinner.getSelectedItemPosition()]);
                    result.setFlyIndex(flyIndexSeekBar.getProgress());
                    result.setNextIndex(PointType.FINISH.equals(
                            PointType.values()[pointTypeSpinner.getSelectedItemPosition()])
                            ? -1 : nextPointIndex);

                        if (getDelegate() != null)
                            getDelegate().doAction(result);

                        d.dismiss();

                }
            });
        }
    }

    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }
    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }

    public DialogOnClickDelegate getDelegate() {
        return delegate;
    }
    public void setDelegate(DialogOnClickDelegate delegate) {
        this.delegate = delegate;
    }
    public void setNextPointIndex(int nextPointIndex) {
        this.nextPointIndex = nextPointIndex;
    }
    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }
    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }
}
