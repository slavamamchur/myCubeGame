package com.cubegames.slava.cubegame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.cubegames.slava.cubegame.model.points.NewPointRequest;
import com.cubegames.slava.cubegame.model.points.PointType;


public class NewGamePointDialogFragment extends DialogFragment {

    private View form=null;
    private DialogOnClickDelegate delegate = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        form= getActivity().getLayoutInflater()
                .inflate(R.layout.input_name_dialog_view, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return(builder.setTitle(R.string.input_name_caption).setView(form)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null).create());
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
                    result.setxPos(100); //TODO: init from UI
                    result.setyPos(200);
                    result.setType(PointType.START);
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
}
