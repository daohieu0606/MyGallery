package com.example.mygallery.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.R;

public class SplashScreen extends Activity implements Animation.AnimationListener {

    Animation slideUp;
    Animation slideDown;
    Animation fadeIn;

    ImageView imgLogo;
    TextView txtAppName;
    TextView txtAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        setUpUI();
        setUpAnimation();
        starViewAnimation();
    }

    private void starViewAnimation() {
        imgLogo.startAnimation(slideDown);
  //      txtAppName.startAnimation(slideUp);

        fadeIn.setStartOffset(2000);
        txtAuthor.startAnimation(fadeIn);
    }

    private void setUpUI() {
        imgLogo = findViewById(R.id.imgLogo);
        txtAppName = findViewById(R.id.txtAppName);
        txtAuthor = findViewById(R.id.txtAuthor);
    }

    private void setUpAnimation() {
        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        slideUp.setAnimationListener(this);
        slideDown.setAnimationListener(this);
        fadeIn.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == fadeIn) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);     //A task contains a stack of activities
            startActivity(intent);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}