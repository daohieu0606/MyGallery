package com.example.mygallery.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.mygallery.adapter.ImageAdapter;
import com.example.mygallery.data.helper.FileHelper;

import java.util.ArrayList;
import java.util.List;

public class DeletingDuplicateFileDialog extends DialogFragment {

    private Activity activity;
    private AlertDialog.Builder builder;
    private List<String> uris;
    private boolean isShowing;
    private DeletingDuplicateFileDialog.OnDeletingDialogListener onDeletingDialogListener;

    public DeletingDuplicateFileDialog(Activity newActivity) {
        activity = newActivity;
        builder = new AlertDialog.Builder(activity);
        onDeletingDialogListener = (DeletingDuplicateFileDialog.OnDeletingDialogListener) activity;
    }
    public void setUriList(List<String> newUris) {
        uris = newUris;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onDeletingDialogListener = (DeletingDuplicateFileDialog.OnDeletingDialogListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ListView listView = new ListView(activity);
        List<Uri> uriList = new ArrayList<>();
        ImageAdapter imageAdapter = new ImageAdapter(activity, uriList);
        listView.setAdapter(imageAdapter);
        builder.setView(listView);

        for (String uriStr:
             uris) {
            Uri uri = Uri.parse(uriStr);
            if (uri != null) {
                uriList.add(uri);
            }
        }

        imageAdapter.notifyDataSetChanged();

        builder.setTitle("Duplicate pictures");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteDuplicatePicture();
                        dialog.dismiss();
                        Toast.makeText(activity.getApplicationContext(), "Successfully", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(activity.getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
        return builder.create();
    }

    private void deleteDuplicatePicture() {
        for (String uri:
                uris) {
            FileHelper.removeFile(activity, uri);
        }
    }

    public void onDeleteListener() {
        for (String uri:
                uris) {
            try{
                activity.getApplicationContext().getContentResolver().delete(Uri.parse(uri), null, null);
            } catch (Exception ex){
                //do nothing
            }
        }
        onDeletingDialogListener.onDeletingDialogClicked();
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

    public interface OnDeletingDialogListener {
        void onDeletingDialogClicked();
    }
}
