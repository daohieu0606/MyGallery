    package com.example.mygallery.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.RecyclerView;

import com.example.mygallery.R;
import com.example.mygallery.adapter.HorizontalVideoListAdapter;
import com.example.mygallery.data.SavedApplicationStateObject;
import com.example.mygallery.data.DAO.VideoDAOImpl;
import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.model.MyVideo;
import com.example.mygallery.dialog.EditTextDialog;
import com.example.mygallery.dialog.VideoDetailDialog;
import com.example.mygallery.response.MainCallback;

import java.io.File;

    public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener, MainCallback, EditTextDialog.OnEditTextDialogListener {

    private static final String UPDATE_FAVORITE_VIDEOS = "UPDATE_FAVORITE_VIDEOS";
    private static final String UPDATE_RECENT_VIDEOS = "UPDATE_RECENT_VIDEOS";
    private static final String UPDATE_VIDEO_LIST_SIZE = "UPDATE_VIDEO_LIST_SIZE";
    private static final String ADAPTER_TAG = "NearestVideoAdapter";
    private static final String PLAYBACK_TIME = "play_time";
    private static final String DETAIL_DIALOG = "DETAIL_DIALOG";
    private static final String RENAME_QUERY_TAG = "RENAME_QUERY_TAG";
    private static final String IS_SHOW_DIALOG_TAG = "IS_SHOW_DIALOG_TAG";
    private static final String RENAME_DIALOG = "RENAME_DIALOG";
    private static final String TAG = "videoplayerrr";
    private static final int RENAME_TAG = 101;
    private static final int REMOVE_TAG = 102;

    private VideoDetailDialog videoDetailDialog;
    private EditTextDialog editTextDialog;
    private RecyclerView rccNearestVideoList;
    private HorizontalVideoListAdapter adapter;

    private VideoDAOImpl videoDAO;

    private MediaController controller;
    private CustomVideoView videoShowStage;
    private ToggleButton btnFavoriteMark;
    private ToggleButton btnPause;
    private Button btnZoomVideo;

    private TextView txtVideoName;
    private TextView txtVideoName2;
    private TextView txtVideoDescription;

    private Button btnPreVideo;
    private Button btnNextVideo;
    private Button btnClose;
    private Button btnVideoMenuPopUp;
    private Button btnDeleteVideo;
    private Button btnRename;

    private int CURRENT_MIDDLE_NEAREST_LIST;
    private int currentVideoID;
    private int currentTimeOffset;

    Intent returnIntent;
    private boolean isShowingDetailDialog;
    private boolean isShowingRenameDialog;
    private String savedRenameQuery;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_video);
        returnIntent = new Intent();
        setUp();
        if (savedInstanceState != null) {
            currentTimeOffset = savedInstanceState.getInt(PLAYBACK_TIME, 0);
            if (savedInstanceState.getString(IS_SHOW_DIALOG_TAG) != null) {
                Log.d(TAG, "onCreate: " + savedInstanceState.getString(IS_SHOW_DIALOG_TAG));
                if (savedInstanceState.getString(IS_SHOW_DIALOG_TAG).equals(DETAIL_DIALOG)) {
                    isShowingDetailDialog = true;
                }else if (savedInstanceState.getString(IS_SHOW_DIALOG_TAG).equals(RENAME_DIALOG)) {
                    isShowingRenameDialog = true;
                    savedRenameQuery = savedInstanceState.getString(RENAME_QUERY_TAG);
                }
            }
        }

    }

    @SuppressLint({"WrongConstant", "CheckResult", "WrongViewCast"})
    // @SuppressLint("WrongConstant")
    private void setUp() {
        videoShowStage = findViewById(R.id.videoShowStage);
        videoShowStage.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {

            @Override
            public void onPlay() {
                btnPause.setChecked(true);
            }

            @Override
            public void onPause() {
                btnPause.setChecked(false);
            }
        });
        btnFavoriteMark = findViewById(R.id.btnFavoriteVideo);

        txtVideoName = findViewById(R.id.txtVideoName);
        txtVideoName2 =findViewById(R.id.txtVideoName2);
        txtVideoDescription = findViewById(R.id.txtVideoDescription);

        btnPreVideo = findViewById(R.id.btnPreVideo);
        btnNextVideo = findViewById(R.id.btnNextVideo);
        btnClose = findViewById(R.id.btnCloseVideoPlayer);
        btnVideoMenuPopUp = findViewById(R.id.btnVideoMenuPopUp);
        btnDeleteVideo = findViewById(R.id.btnDeleteVideo);

        btnZoomVideo = findViewById(R.id.btnZoomVideo);
        btnZoomVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyVideo currentVideo = videoDAO.getMediaFile(currentVideoID);
                Intent intent = new Intent(VideoPlayerActivity.this, VideoFullScreenActivity.class);
                intent.putExtra("video", currentVideo.getvContent().getPath());
                startActivity(intent);
            }
        });

        btnRename = findViewById(R.id.btnRenameVideo);
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeNameDialog();
            }
        });

        videoDAO = new VideoDAOImpl(this);
        currentVideoID = (int) getIntent().getLongExtra("VIDEO",-1);

        if(currentVideoID == -1){
            finish();
        } else {
            //do nothing
        }

        videoDetailDialog = new VideoDetailDialog(videoDAO.getMediaFile(currentVideoID));
        editTextDialog = new EditTextDialog(this,"Rename");
        rccNearestVideoList = findViewById(R.id.rccNearestVideoList);
        adapter = new HorizontalVideoListAdapter(videoDAO.getAllMediaFiles(),this);
        rccNearestVideoList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL,false));
        rccNearestVideoList.setAdapter(adapter);

        CURRENT_MIDDLE_NEAREST_LIST = 0;
        currentTimeOffset = 0;

        btnFavoriteMark.setOnClickListener(this);
        btnPreVideo.setOnClickListener(this);
        btnNextVideo.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnVideoMenuPopUp.setOnClickListener(this);
        btnDeleteVideo.setOnClickListener(this);

        controller = new MediaController(this);
        controller.setMediaPlayer(videoShowStage);
        videoShowStage.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPause.setChecked(false);
            }
        });
        videoShowStage.setMediaController(controller);

        btnPause = findViewById(R.id.btnPause);
        btnPause.setChecked(false);
        btnPause.setOnClickListener(this);
        onUpdateUI();
    }

    private void updateMiddleNearestList(int offset) {
        if(CURRENT_MIDDLE_NEAREST_LIST + offset >= 1 && CURRENT_MIDDLE_NEAREST_LIST + offset < videoDAO.getAllMediaFiles().size())
        {
            CURRENT_MIDDLE_NEAREST_LIST += offset;
            rccNearestVideoList.smoothScrollToPosition(CURRENT_MIDDLE_NEAREST_LIST - 1);
        }
    }

    private void onUpdateUI() {
        MyVideo currentVideo = null;
        for (MyVideo video:
                videoDAO.getAllMediaFiles()) {
            if ((int)video.getvContent().getVideoId() == currentVideoID) {
                currentVideo = video;
                break;
            }
        }

        if (currentVideo == null){
            return;
        }

        txtVideoName.setText(currentVideo.getvContent().getVideoName());
        txtVideoName2.setText(currentVideo.getvContent().getVideoName());
        txtVideoDescription.setText(getDescription());

        btnFavoriteMark.setChecked(currentVideo.isFavorite());

        if (videoShowStage.isPlaying()) {
            btnPause.setChecked(true);
        } else {
            btnPause.setChecked(false);
        }

        updateMiddleNearestList(videoDAO.getAllMediaFiles().indexOf(currentVideo) - CURRENT_MIDDLE_NEAREST_LIST);
        SavedApplicationStateObject savedApplicationStateObject = SavedApplicationStateObject.getInstance();
        savedApplicationStateObject.addRecentVideoID((int)currentVideoID);
        returnIntent.putExtra(UPDATE_RECENT_VIDEOS, UPDATE_RECENT_VIDEOS);
    }
    
    private String getDescription () {
        StringBuilder result = new StringBuilder();

        MyVideo currentVideo = videoDAO.getMediaFile(currentVideoID);
        result.append("path: ");
        result.append(currentVideo.getvContent().getPath());
        result.append("\n");
        result.append("Size: ");
        result.append(String.valueOf(currentVideo.getvContent().getVideoSize()));

        return  result.toString();
    }
    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
        if (isShowingDetailDialog) {
            videoDetailDialog.show(getSupportFragmentManager(), "Photo details");
            isShowingDetailDialog = false;
        } else if(isShowingRenameDialog) {
            editTextDialog.setTextAndRequestFocus(savedRenameQuery);
            editTextDialog.show(getSupportFragmentManager(), "Rename");
            isShowingRenameDialog = false;
            savedRenameQuery = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoShowStage.pause();
        if (editTextDialog.isShowing()) {
            savedRenameQuery = editTextDialog.getValue();
            isShowingRenameDialog = true;
            editTextDialog.dismiss();
            Log.d(TAG, "onPause: edit");
        }else if (videoDetailDialog.isShowing()) {
            isShowingDetailDialog = true;
            videoDetailDialog.dismiss();
            Log.d(TAG, "onPause: detail");
        }

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isShowingDetailDialog) {
            Log.d(TAG, "onSaveInstanceState: save detail" );
            outState.putString(IS_SHOW_DIALOG_TAG, DETAIL_DIALOG);
        }else  if (isShowingRenameDialog) {
            outState.putString(IS_SHOW_DIALOG_TAG, RENAME_DIALOG);
            outState.putString(RENAME_QUERY_TAG, savedRenameQuery);
        }
        outState.putInt(PLAYBACK_TIME, currentTimeOffset);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        videoShowStage.stopPlayback();
        btnPause.setChecked(false);
    }

    private void initializePlayer() {
        MyVideo currentVideo = videoDAO.getMediaFile(currentVideoID);
        String path = currentVideo.getvContent().getPath();
        Uri uri = Uri.parse(path);

        videoShowStage.setTag(uri.toString());
        videoShowStage.setVideoPath(path);
        if (currentTimeOffset > 0) {
            videoShowStage.seekTo(currentTimeOffset);
        } else {
            videoShowStage.seekTo(1);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnPreVideo){
            handleLeftButton();
        } else if(id == R.id.btnNextVideo){
            handleRightButton();
        } else if(id == R.id.btnFavoriteVideo){
            handleFavoriteButton();
        } else if(id == R.id.btnCloseVideoPlayer){
            handleCloseButton();
        } else if(id == R.id.btnVideoMenuPopUp){
            handlePopUpMenuButton();
        } else if (id == R.id.btnDeleteVideo) {
            handleDeleteVideo();
        } else if (id == R.id.btnPause) {
            handlePauseVideo();
        }
    }

    private void handlePauseVideo() {
        if (videoShowStage.isPlaying()) {
            videoShowStage.pause();
            currentTimeOffset = videoShowStage.getCurrentPosition();
            btnPause.setChecked(false);
        } else {
            videoShowStage.seekTo(currentTimeOffset);
            videoShowStage.start();
            btnPause.setChecked(true);
        }
    }

    private void handleFavoriteButton() {
        returnIntent.putExtra(UPDATE_FAVORITE_VIDEOS, UPDATE_FAVORITE_VIDEOS);
        if (videoDAO.getMediaFile(currentVideoID).isFavorite()) {
            videoDAO.delFavoriteVideo((int)videoDAO.getMediaFile(currentVideoID).getvContent().getVideoId());
        } else {
            videoDAO.addFavoriteVideo((int)videoDAO.getMediaFile(currentVideoID).getvContent().getVideoId());
        }
    }

    private void handleCloseButton() {
        finish();
    }

    private void handleRightButton() {
        updateMiddleNearestList(1);
    }

    private void handleLeftButton() {
        updateMiddleNearestList(-1);
    }

    private  void handlePopUpMenuButton(){
        PopupMenu popupMenu = new PopupMenu(this, btnVideoMenuPopUp);
        popupMenu.getMenuInflater().inflate(R.menu.menu_video_more_options,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.optChangeNameVideo:
                        openChangeNameDialog();
                        break;
                    case  R.id.optDetailVideo:
                        openDetailDialog();
                        break;
                    case R.id.shareVideo:
                        handleShare();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void openDetailDialog() {
        videoDetailDialog.show(getSupportFragmentManager(),"Detal Dialog");
    }

    private void openChangeNameDialog() {
        editTextDialog.show(getSupportFragmentManager(),"Chang Name Dialog");
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }

    @Override
    public void onMsgToMain(String sender, String message) {
        if (sender.equals(ADAPTER_TAG)) {
            currentVideoID = Integer.valueOf(message);
            onUpdateUI();
            currentTimeOffset = 0;
            initializePlayer();
        }
    }

    @Override
    public void onEdtDialogClicked() {
        String uriStr = videoDAO.getMediaFile(currentVideoID).getvContent().getAssetFileStringUri();
        boolean isRenamed = FileHelper.rename(this, uriStr, editTextDialog.getValue());
        if (isRenamed) {
            videoDAO.updateMediaFile(currentVideoID);
            onUpdateUI();
        }
    }

    private void handleDeleteVideo() {
        MyVideo currentViedo = videoDAO.getMediaFile(currentVideoID);
        String uriStr = currentViedo.getvContent().getAssetFileStringUri();

        boolean isDeleted = FileHelper.removeFile(this, uriStr);
        if (isDeleted) {
            videoDAO.updateList();
            generateAnotherVideoId();
            onUpdateUI();
            currentTimeOffset = 0;
            initializePlayer();
            adapter.notifyDataSetChanged();
            returnIntent.putExtra(UPDATE_VIDEO_LIST_SIZE, UPDATE_VIDEO_LIST_SIZE);
        }
    }

    private void generateAnotherVideoId() {
        if(videoDAO.getAllMediaFiles().size() == 0) {
            Toast.makeText(getApplicationContext(), "No exist picture in your device", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (CURRENT_MIDDLE_NEAREST_LIST >= videoDAO.getAllMediaFiles().size()) {
            CURRENT_MIDDLE_NEAREST_LIST = videoDAO.getAllMediaFiles().size() - 1;
        } else if (CURRENT_MIDDLE_NEAREST_LIST < 0) {
            CURRENT_MIDDLE_NEAREST_LIST = 0;
        }

        currentVideoID = (int) videoDAO.getAllMediaFiles().get(CURRENT_MIDDLE_NEAREST_LIST).getvContent().getVideoId();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RENAME_TAG == requestCode) {
            if (resultCode == RESULT_CANCELED) {
                return;
            }
            onEdtDialogClicked();
        } else if (REMOVE_TAG == requestCode) {
            if (resultCode == RESULT_CANCELED) {
                return;
            }
            handleDeleteVideo();
        }
    }

    private void handleShare() {
        MyVideo video = videoDAO.getMediaFile(currentVideoID);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent share;
        try {
            share = new Intent(Intent.ACTION_SEND);
            share.setType("video/*");
            File media = new File(video.getvContent().getPath());
            Uri uri = Uri.fromFile(media);
            share.putExtra(Intent.EXTRA_STREAM,uri);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        startActivity(Intent.createChooser(share,"share video"));
    }

}