package com.example.mygallery.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.data.DAO.ImageDAOImpl;
import com.example.mygallery.data.model.MyPicture;

import java.util.List;


public class SlideShowActivity extends AppCompatActivity {

    ViewFlipper imageSlider;
    List<MyPicture> pictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slide_show);
        setUpUI();
        pictures = new ImageDAOImpl(this).getAllMediaFiles();
        play();
    }

    private void play() {
        for (MyPicture pic:
             pictures) {
            String path = pic.getpContent().getPicturePath();
            addView(path);
        }
    }

    private void setUpUI() {
        imageSlider = findViewById(R.id.imageSlider);
        imageSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addView(String path) {
        ImageView imageView = new ImageView(this);
        bind(path, imageView);

        imageSlider.addView(imageView);
        imageSlider.setFlipInterval(3000);
        imageSlider.setAutoStart(true);

        imageSlider.setInAnimation(this, android.R.anim.slide_in_left);
        imageSlider.setOutAnimation(this, android.R.anim.slide_out_right);
    }

    private void bind(String path, ImageView imageView) {

        Glide.with(this)
                .load(path)
                .into(imageView);
    }
}