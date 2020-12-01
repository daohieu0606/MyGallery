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
import com.example.mygallery.data.model.MyVideo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoDetailDialog extends AppCompatDialogFragment {

    private MyVideo myVideo;
    public VideoDetailDialog(MyVideo myVideo) {
        this.myVideo = myVideo;
    }

    private boolean isShowing;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        TextView txtName;
        TextView txtPath;
        TextView txtSize;
        TextView txtTime;
        TextView txtDuration;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_video_detail,null);
        builder.setView(view)
                .setTitle("Detail")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("mm:ss");
        String date = sdf.format(new Date( ((long)myVideo.getvContent().getDate_modified())*1000L));
        txtName = view.findViewById(R.id.txtName);
        txtTime = view.findViewById(R.id.txtTime);
        txtSize = view.findViewById(R.id.txtSize);
        txtPath = view.findViewById(R.id.txtPath);
        txtDuration = view.findViewById(R.id.txtDuration2);
        double size = (double)Math.round(myVideo.getvContent().getVideoSize()*Math.pow(2,-20)*100)/100;
        txtSize.setText(size + " MB ");
        txtName.setText(myVideo.getvContent().getVideoName());
        txtPath.setText(myVideo.getvContent().getPath());
        txtTime.setText(date);
        String duration = sdf2.format(new Date( (myVideo.getvContent().getVideoDuration())));
        txtDuration.setText(duration);
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
