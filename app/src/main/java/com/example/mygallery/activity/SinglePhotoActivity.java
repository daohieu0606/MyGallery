package com.example.mygallery.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.mygallery.R;

public class SinglePhotoActivity extends AppCompatActivity {

    private SubsamplingScaleImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_single);
        imageView = this.findViewById(R.id.imgFullScreenView);
        if(getIntent() != null){
            String uriStr = getIntent().getStringExtra("image");
            imageView.setImage(ImageSource.uri(Uri.parse(uriStr)));
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