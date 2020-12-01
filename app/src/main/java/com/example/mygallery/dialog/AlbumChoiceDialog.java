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
import androidx.fragment.app.FragmentManager;

import com.example.mygallery.data.model.Album;

import java.util.List;

public class AlbumChoiceDialog extends DialogFragment {

    private Activity activity;
    private AlertDialog.Builder builderSingle;
    private List<Album> albums;
    private AlbumChoiceDialog.OnItemClickListener onItemClickListener;
    private String selectedAlbum;
    private boolean isShowing;

    public AlbumChoiceDialog(Activity newActivity, List<Album> newAlbums) {
        activity = newActivity;
        builderSingle = new AlertDialog.Builder(activity);
        albums = newAlbums;
        selectedAlbum = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builderSingle.setTitle("Select One Album:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);
        for (Album album:
             albums) {
            arrayAdapter.add(album.getAlbumName());
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedAlbum = arrayAdapter.getItem(which);
                onItemClickListener.onAlbumDialogClick();
            }
        });

        return builderSingle.create();
    }

    public interface OnItemClickListener{
        void onAlbumDialogClick();
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

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        super.show(manager, tag);
        isShowing = true;
    }

    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShowing = false;
    }

    public String getSelectedAlbum() {
        return selectedAlbum;
    }
}
