package com.example.mygallery.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.mygallery.R;
import com.example.mygallery.data.model.MyPicture;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoDetailDialog extends AppCompatDialogFragment {

    MyPicture myPicture;
    private boolean isShowing;
    public PhotoDetailDialog(MyPicture myPicture) {
        this.myPicture = myPicture;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        TextView txtName;
        TextView txtPath;
        TextView txtSize;
        TextView txtTime;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_photo_detail,null);
        builder.setView(view)
                .setTitle("Detail")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String date = sdf.format(new Date( ((long)myPicture.getpContent().getDate_modified())*1000L));
        txtName = view.findViewById(R.id.txtName);
        txtTime = view.findViewById(R.id.txtTime);
        txtSize = view.findViewById(R.id.txtSize);
        txtPath = view.findViewById(R.id.txtPath);
        double size = (double)Math.round(myPicture.getpContent().getPictureSize()*Math.pow(2,-20)*100)/100;
        txtSize.setText(size + " MB ");
        txtName.setText(myPicture.getpContent().getPicturName());
        txtPath.setText(myPicture.getpContent().getPicturePath());
        txtTime.setText(date);
        return builder.create();
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
}
