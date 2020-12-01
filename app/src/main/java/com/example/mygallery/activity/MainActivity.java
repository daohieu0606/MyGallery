package com.example.mygallery.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.mygallery.R;
import com.example.mygallery.data.SavedApplicationStateObject;
import com.example.mygallery.dialog.DeletingDuplicateFileDialog;
import com.example.mygallery.dialog.EditTextDialog;
import com.example.mygallery.dialog.ListDialog;
import com.example.mygallery.fragment.HomeFragment;
import com.example.mygallery.fragment.PhotoFragment;
import com.example.mygallery.fragment.SettingFragment;
import com.example.mygallery.fragment.VideoFragment;
import com.example.mygallery.response.MainCallback;
import com.example.mygallery.viewmodel.MainVMFactory;
import com.example.mygallery.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity implements
                    MainCallback, EditTextDialog.OnEditTextDialogListener,
            ListDialog.OnItemClickListener,
        DeletingDuplicateFileDialog.OnDeletingDialogListener {

    enum FragFlat{
        HOME_FLAT,
        PHOTO_FLAT,
        VIDEO_FLAT,
        SETTING_FLAT,
    }

    private BottomNavigationView navFragment;
    private SearchView searchView;

    private HomeFragment homeFragment;
    private PhotoFragment photoFragment;
    private VideoFragment videoFragment;
    private SettingFragment settingFragment;

    private TextView title;
    public static FragFlat CURRENT_FRAGMENT_FLAT;

    private static final String MAIN_ACTIVITY = "MAIN_ACTIVITY";

    private static final String UPDATE_FAVORITE_PICTURES = "UPDATE_FAVORITE_PICTURES";
    private static final String UPDATE_RECENT_PICTURES = "UPDATE_RECENT_PICTURES";
    private static final String UPDATE_PICTURE_LIST_SIZE = "UPDATE_PICTURE_LIST_SIZE";

    private static final String UPDATE_FAVORITE_VIDEOS = "UPDATE_FAVORITE_VIDEOS";
    private static final String UPDATE_RECENT_VIDEOS = "UPDATE_RECENT_VIDEOS";
    private static final String UPDATE_VIDEO_LIST_SIZE = "UPDATE_VIDEO_LIST_SIZE";

    private static final String CHANGE_PHOTO_CATALOG = "CHANGE_PHOTO_CATALOG";
    private static final String CHANGE_VIDEO_CATALOG = "CHANGE_VIDEO_CATALOG";

    private static final String CREATE_ALBUM = "CREATE_ALBUM";

    private static final int PHOTO_PLAYER_ACTIVITY = 1;
    private static final int VIDEO_PLAYER_ACTIVITY = 2;

    private static final int RENAME_TAG = 101;
    private static final int REMOVE_TAG = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadStateOfApp();
        RequestPermission();
        setUp();
        if (savedInstanceState == null) {
            setUpFragmentTransaction();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.prime6));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("SETTING", Context.MODE_PRIVATE);
        String  NEW_FRAG_FLAT = settings.getString("CURRENT_FRAG", FragFlat.HOME_FLAT.toString());
        if (NEW_FRAG_FLAT.equals(FragFlat.PHOTO_FLAT.toString())) {
            navFragment.setSelectedItemId(R.id.nav_photo);
        } else if (NEW_FRAG_FLAT.equals(FragFlat.VIDEO_FLAT.toString())) {
            navFragment.setSelectedItemId(R.id.nav_video);
        } else if (NEW_FRAG_FLAT.equals(FragFlat.SETTING_FLAT.toString())) {
            navFragment.setSelectedItemId(R.id.nav_setting);
        } else {
            navFragment.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tool_bar, menu);

        searchView = (SearchView) menu.findItem( R.id.btnSearch ).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return  true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return  true;
    }

    private void loadStateOfApp() {

        String FILENAME = "app_state.txt";
        try {
            FileInputStream fis = openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            SavedApplicationStateObject savedApplicationStateObject = (SavedApplicationStateObject) ois.readObject();
            SavedApplicationStateObject.getInstance().setFavoriteFileIDs(savedApplicationStateObject.getFavoriteFileIDs());
            SavedApplicationStateObject.getInstance().setRecentPictures(savedApplicationStateObject.getRecentPictures());
            SavedApplicationStateObject.getInstance().setRecentVideos(savedApplicationStateObject.getRecentVideos());

            fis.close();
            ois.close();
        }catch (Exception exception){
            //exception.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        saveState();
        super.onPause();
        SharedPreferences settings = getSharedPreferences("SETTING", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("CURRENT_FRAG", CURRENT_FRAGMENT_FLAT.toString());
        editor.apply();
    }

    private void saveState() {

        String FILENAME = "app_state.txt";
        SavedApplicationStateObject savedApplicationStateObject = SavedApplicationStateObject.getInstance();

        try {
            FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(savedApplicationStateObject);

            oos.close();
            fos.close();
        }catch (Exception exception){
            Toast.makeText(this, "save current state failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void RequestPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 7003);
        }
        requestPermissions( new String[]{Manifest.permission.CAMERA}, 1001);
        requestPermissions(new String[] {Manifest.permission.ACCESS_MEDIA_LOCATION}, 7000);
        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7001);
        requestPermissions(new String[] {Manifest.permission.SET_WALLPAPER_HINTS}, 7002);
    }

    private void setUp() {
        MainViewModel mainViewModel = ViewModelProviders.of(this, new MainVMFactory(this)).get(MainViewModel.class);
        if (homeFragment == null) {
            homeFragment = mainViewModel.getHomeFragment();
        }

        if (photoFragment == null) {
            photoFragment = mainViewModel.getPhotoFragment();
        }

        if (videoFragment == null) {
            videoFragment = mainViewModel.getVideoFragment();
        }

        if (settingFragment == null) {
            settingFragment = mainViewModel.getSettingFragment();
        }
        title = findViewById(R.id.toolbar_title);
        title.setText("HOME");
        navFragment = findViewById(R.id.nav_fragment);
        navFragment.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        title.setText("HOME");
                        changeCurrentFragment(FragFlat.HOME_FLAT);
                        break;
                    case R.id.nav_photo:
                        title.setText("PICTURE");
                        changeCurrentFragment(FragFlat.PHOTO_FLAT);
                        break;
                    case R.id.nav_video:
                        title.setText("VIDEO");
                        changeCurrentFragment(FragFlat.VIDEO_FLAT);
                        break;
                    case R.id.nav_setting:
                        title.setText("SETTING");
                        changeCurrentFragment(FragFlat.SETTING_FLAT);
                        break;
                }
                return true;
            }
        });
    }

    private void setUpFragmentTransaction() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.mainContainer, homeFragment, FragFlat.HOME_FLAT.toString());
        transaction.add(R.id.mainContainer, photoFragment, FragFlat.PHOTO_FLAT.toString());
        transaction.add(R.id.mainContainer, videoFragment, FragFlat.VIDEO_FLAT.toString());
        transaction.add(R.id.mainContainer, settingFragment, FragFlat.SETTING_FLAT.toString());

        transaction.hide(settingFragment);
        transaction.hide(videoFragment);
        transaction.hide(photoFragment);

        transaction.commit();
        CURRENT_FRAGMENT_FLAT = FragFlat.HOME_FLAT;
    }

    private void changeCurrentFragment(final FragFlat NEW_FRAGMENT_FLAT) {
        if (NEW_FRAGMENT_FLAT.toString().equals(CURRENT_FRAGMENT_FLAT.toString())) {
            return;
        }
         FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment oldFragment = manager.findFragmentByTag(CURRENT_FRAGMENT_FLAT.toString());
        Fragment newFragment = manager.findFragmentByTag(NEW_FRAGMENT_FLAT.toString());

        if (CURRENT_FRAGMENT_FLAT.compareTo(NEW_FRAGMENT_FLAT) > 0) {
            transaction.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        } else {
            transaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_right);
        }
        transaction.hide(oldFragment);
        transaction.show(newFragment);
        transaction.commit();

        CURRENT_FRAGMENT_FLAT = NEW_FRAGMENT_FLAT;
    }
    @Override
    public void onEdtDialogClicked() {
        homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, CREATE_ALBUM);
    }
    @Override
    public void onListDialogClick() {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PLAYER_ACTIVITY && resultCode == RESULT_OK) {
            if (data.hasExtra(UPDATE_FAVORITE_PICTURES)) {
                homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_FAVORITE_PICTURES);
            } if (data.hasExtra(UPDATE_PICTURE_LIST_SIZE)) {
                homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_PICTURE_LIST_SIZE);
                photoFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_PICTURE_LIST_SIZE);
            } if (data.hasExtra(UPDATE_RECENT_PICTURES)) {
                homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_RECENT_PICTURES);
            }
        } else if (requestCode == VIDEO_PLAYER_ACTIVITY && resultCode == RESULT_OK) {
            if (data.hasExtra(UPDATE_FAVORITE_VIDEOS)) {
                homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_FAVORITE_VIDEOS);
            } if (data.hasExtra(UPDATE_VIDEO_LIST_SIZE)) {
                homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_VIDEO_LIST_SIZE);
                videoFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_VIDEO_LIST_SIZE);
            } if (data.hasExtra(UPDATE_RECENT_VIDEOS)) {
                homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, UPDATE_RECENT_VIDEOS);
            }
        } else if (RENAME_TAG == requestCode) {
            //
        } else if (REMOVE_TAG == requestCode) {
            handleDeleteFile();
        }
    }
    @Override
    public void onDeletingDialogClicked() {
        homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, "FINISH_DELETE");
    }
    private void handleDeleteFile() {
        homeFragment.onMsgFromMainToFrag(MAIN_ACTIVITY, String.valueOf(REMOVE_TAG));
    }

    @Override
    public void onMsgToMain(String sender, String message) {
        if (sender.equals(CHANGE_PHOTO_CATALOG)) {
            photoFragment.onMsgFromMainToFrag(sender, message);
        } else if (sender.equals(CHANGE_VIDEO_CATALOG)) {
            videoFragment.onMsgFromMainToFrag(sender, message);
        }
    }
}