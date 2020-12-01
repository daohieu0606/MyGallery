package com.example.mygallery.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mygallery.activity.MainActivity;

public class MainVMFactory implements ViewModelProvider.Factory {

    private MainActivity activity;

    public MainVMFactory(MainActivity newActivity) {
        activity = newActivity;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(activity);
    }
}
