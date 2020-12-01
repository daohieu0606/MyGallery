package com.example.mygallery.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.mygallery.activity.MainActivity;
import com.example.mygallery.fragment.HomeFragment;
import com.example.mygallery.fragment.PhotoFragment;
import com.example.mygallery.fragment.SettingFragment;
import com.example.mygallery.fragment.VideoFragment;

public class MainViewModel extends ViewModel {
    private HomeFragment homeFragment;
    private PhotoFragment photoFragment;
    private VideoFragment videoFragment;
    private SettingFragment settingFragment;
    private MainActivity activity;


    MainViewModel(MainActivity newActivity) {
        activity = newActivity;

        homeFragment = new HomeFragment(activity);
        photoFragment = new PhotoFragment(activity);
        videoFragment = new VideoFragment(activity);
        settingFragment = new SettingFragment(activity);
    }

    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

    public PhotoFragment getPhotoFragment() {
        return photoFragment;
    }

    public VideoFragment getVideoFragment() {
        return videoFragment;
    }

    public SettingFragment getSettingFragment() {
        return settingFragment;
    }
}
