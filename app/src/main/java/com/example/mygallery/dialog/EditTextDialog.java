package com.example.mygallery.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.mygallery.R;

public class EditTextDialog extends AppCompatDialogFragment {

    private EditText value;
    private String tittle;
    private OnEditTextDialogListener onEditTextDialogListener;
    private Activity activity;
    private boolean isShowing;
    private String preQuery;

    public EditTextDialog(Activity newContext, String newTittle) {
        tittle = newTittle;
        activity = newContext;
        onEditTextDialogListener = (OnEditTextDialogListener) activity;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edt,null);
        builder.setView(view)
                .setTitle(tittle)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isShowing = false;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onEditTextDialogListener.onEdtDialogClicked();
                        isShowing = false;
                    }
                });

        value = view.findViewById(R.id.edtContent);
        if (preQuery != null) {
            value.setText(preQuery);
            value.requestFocus();
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onEditTextDialogListener = (OnEditTextDialogListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    public interface OnEditTextDialogListener {
        void onEdtDialogClicked();
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        isShowing = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShowing = false;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public String getValue() {
        return value.getText().toString();
    }

    public void setTextAndRequestFocus(String str) {
        if (value != null) {
            value.setText(str);
            value.requestFocus();
        } else {
            preQuery = str;
        }
    }

    public void showWithText(@NonNull FragmentManager manager, @Nullable String tag, String text) {
        show(manager, tag);
        setTextAndRequestFocus(text);
    }
}
