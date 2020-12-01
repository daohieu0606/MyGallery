package com.example.mygallery.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class ListDialog extends DialogFragment {
    private Activity activity;
    private AlertDialog.Builder builderSingle;
    private OnItemClickListener onItemClickListener;
    private List<String> itemValues;

    public ListDialog(Activity newActivity, List<String> newValues) {
        activity = newActivity;
        builderSingle = new AlertDialog.Builder(activity);
        itemValues = newValues;
        onItemClickListener = (OnItemClickListener) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builderSingle.setTitle("Select One Album:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onItemClickListener.onListDialogClick();
            }
        });
        arrayAdapter.addAll(itemValues);
        arrayAdapter.notifyDataSetChanged();
        return builderSingle.create();
    }

    public interface OnItemClickListener{
        void onListDialogClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onItemClickListener = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public List<String> getValues() {
        return itemValues;
    }
}
