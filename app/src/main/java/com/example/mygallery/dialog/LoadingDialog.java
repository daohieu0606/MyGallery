package com.example.mygallery.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.mygallery.R;

public class LoadingDialog {
    Activity activity;
    AlertDialog dialog;
    private boolean isShowing;

    public LoadingDialog(Activity newActivity) {
        activity = newActivity;
    }

    public void startLoading() {
        isShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_custom_loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
        isShowing = false;
    }

    public boolean isShowing() {
        return isShowing;
    }
}
