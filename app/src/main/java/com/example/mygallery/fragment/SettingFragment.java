package com.example.mygallery.fragment;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygallery.R;
import com.example.mygallery.activity.MainActivity;

import java.util.Locale;

public class SettingFragment extends Fragment {

    private MainActivity activity;
    private Button btnAboutUs;
    private ViewStub viewAboutUs;
    private boolean isAboutUsViewShowed;

    public SettingFragment() {
    }

    public SettingFragment(MainActivity newActivity) {
        activity = newActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activity == null) {
            activity= (MainActivity) getActivity();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_setting, container, false);

        viewAboutUs = result.findViewById(R.id.viewAboutUs);
        View stubView =viewAboutUs.inflate();
        TextView txtMail = stubView.findViewById(R.id.txtMail);
        txtMail.setText("damryo06@gmail.com");
        viewAboutUs.setVisibility(View.GONE);
        isAboutUsViewShowed = false;

        btnAboutUs = result.findViewById(R.id.btnAbout);
        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAboutUsButton();
            }
        });

        return result;
    }

    private void handleAboutUsButton() {
        if (isAboutUsViewShowed == false) {
            isAboutUsViewShowed = true;
            viewAboutUs.setVisibility(View.VISIBLE);
        } else {
            isAboutUsViewShowed = false;
            viewAboutUs.setVisibility(View.GONE);
        }
    }
}