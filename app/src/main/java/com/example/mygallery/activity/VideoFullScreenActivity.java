package com.example.mygallery.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mygallery.R;

public class VideoFullScreenActivity extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_single);
        videoView=this.findViewById(R.id.vidFullScreenView);
        if(getIntent() != null){
            String path = getIntent().getStringExtra("video");
            videoView.setVideoPath(path);
            videoView.start();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*int x = (int)event.getX();
        int y = (int)event.getY();*/
        finish();
        return true;
    }
}
