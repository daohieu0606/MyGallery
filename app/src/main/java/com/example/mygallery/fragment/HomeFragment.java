package com.example.mygallery.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mygallery.R;
import com.example.mygallery.activity.MainActivity;
import com.example.mygallery.adapter.PhotoListAdapter;
import com.example.mygallery.adapter.VideoListAdapter;
import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.DAO.ImageDAOImpl;
import com.example.mygallery.data.helper.MD5;
import com.example.mygallery.data.model.Album;
import com.example.mygallery.data.model.MyPicture;
import com.example.mygallery.data.model.MyVideo;
import com.example.mygallery.data.SavedApplicationStateObject;
import com.example.mygallery.data.DAO.VideoDAOImpl;
import com.example.mygallery.dialog.DeletingDuplicateFileDialog;
import com.example.mygallery.dialog.EditTextDialog;
import com.example.mygallery.dialog.LoadingDialog;
import com.example.mygallery.response.FragCallBack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;

public class HomeFragment extends Fragment implements FragCallBack, View.OnClickListener {

    private static final String UPDATE_FAVORITE_PICTURES = "UPDATE_FAVORITE_PICTURES";
    private static final String UPDATE_RECENT_PICTURES = "UPDATE_RECENT_PICTURES";
    private static final String UPDATE_PICTURE_LIST_SIZE = "UPDATE_PICTURE_LIST_SIZE";

    private static final String UPDATE_FAVORITE_VIDEOS = "UPDATE_FAVORITE_VIDEOS";
    private static final String UPDATE_RECENT_VIDEOS = "UPDATE_RECENT_VIDEOS";
    private static final String UPDATE_VIDEO_LIST_SIZE = "UPDATE_VIDEO_LIST_SIZE";
    private static final String CREATE_ALBUM = "CREATE_ALBUM";

    private static final int RENAME_TAG = 101;
    private static final int REMOVE_TAG = 102;

    static final int REQUEST_IMAGE_CAPTURE = 201;
    static final int REQUEST_VIDEO_CAPTURE = 202;
    private static final String CREATE_ALBUM_QUERY = "CREATE_ALBUM_QUERY";
    private static final String TAG = "homefrag";

    private List <MyPicture> recPhotos;
    private List <MyVideo> recVideos;

    private List<MyPicture> favPhotos;
    private List<MyVideo> favVideos;

    private PhotoListAdapter recPhotoAdapter;
    private VideoListAdapter recVideoAdapter;
    private PhotoListAdapter favPhotoAdapter;
    private VideoListAdapter favVideoAdapter;

    private MainActivity activity;

    private TextView txtVideoCount;
    private TextView txtPhotoCount;
    private TextView txtFolderCount;

    private Button btnCaptureImage;
    private Button btnRecordVideo;
    private Button btnCreateAlbum;
    private Button btnScanDuplicateFile;

    private EditTextDialog createAlbumDialog;
    private DeletingDuplicateFileDialog deletingDuplicateFileDialog;
    private LoadingDialog loadingDialog;

    ImageDAOImpl imageDAO;
    VideoDAOImpl videoDAO;
    private SavedApplicationStateObject savedApplicationStateObject;
    private String newImageUri;
    private String newVideoUri;

    private boolean isBackgroundTaskRunning;
    private boolean isCreatingAlbum;
    private boolean isScanfile;
    private String createAlbumQuery;
    private Thread thread;

    public HomeFragment() {
    }

    public HomeFragment(MainActivity newActivity) {
        activity=newActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activity == null) {
            activity= (MainActivity) getActivity();
        }
        imageDAO = new ImageDAOImpl(getContext());
        videoDAO = new VideoDAOImpl(getContext());

        savedApplicationStateObject = SavedApplicationStateObject.getInstance();
        recVideos = new ArrayList<>();
        recPhotos = new ArrayList<>();
        favPhotos = new ArrayList<>();
        favVideos = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null){
            try {
                thread.interrupt();
            } catch (Exception e){
                //do nothing
            }
        }
        isBackgroundTaskRunning = false;
        if (createAlbumDialog.isShowing()) {
            createAlbumQuery = createAlbumDialog.getValue();
            isCreatingAlbum = true;
            createAlbumDialog.dismiss();
        } else {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismissDialog();
            }
            if (deletingDuplicateFileDialog.isShowing()) {
                deletingDuplicateFileDialog.dismiss();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isCreatingAlbum) {
            outState.putString("DIALOG_SHOW", "CREATE_ALBUM");
            outState.putString(CREATE_ALBUM_QUERY, createAlbumQuery);
        } else if (isScanfile) {
            outState.putString("DIALOG_SHOW", "SCAN_DUP_FILE");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.btnSearch).setVisible(false);
        menu.findItem(R.id.btnSorting).setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView;

        convertView = inflater.inflate(R.layout.fragment_home, container, false);

        loadingDialog = new LoadingDialog(activity);
        createAlbumDialog = new EditTextDialog(getActivity(),"Create album");
        deletingDuplicateFileDialog = new DeletingDuplicateFileDialog(activity);

        txtPhotoCount = convertView.findViewById(R.id.txtPhotoCount1);
        txtVideoCount = convertView.findViewById(R.id.txtVideoCount1);
        txtFolderCount = convertView.findViewById(R.id.txtFolderCount1);

        setUpRecentPictureList(convertView);
        setUpRecentVideoList(convertView);
        setUpFavoritePictureList(convertView);
        setUpFavoriteVideoList(convertView);
        setUpQuickAccessFunction(convertView);

        if (savedInstanceState != null){
            if (savedInstanceState.getString("DIALOG_SHOW") != null) {
                if (savedInstanceState.getString("DIALOG_SHOW").equals("CREATE_ALBUM")) {
                    isCreatingAlbum = true;
                    createAlbumQuery = savedInstanceState.getString(CREATE_ALBUM_QUERY);
                } else if (savedInstanceState.getString("DIALOG_SHOW").equals("SCAN_DUP_FILE")) {
                    isScanfile = true;
                }
            }
        }
        return convertView;
    }

    private void setUpQuickAccessFunction(View convertView) {
        btnCreateAlbum = convertView.findViewById(R.id.btnCreateAlbum);
        btnCreateAlbum.setOnClickListener(this);

        btnCaptureImage = convertView.findViewById(R.id.btnCaptureImage);
        btnCaptureImage.setOnClickListener(this);

        btnRecordVideo = convertView.findViewById(R.id.btnRecordVideo);
        btnRecordVideo.setOnClickListener(this);

        btnScanDuplicateFile = convertView.findViewById(R.id.btnScanDuplicateFile);
        btnScanDuplicateFile.setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        updateTextView();
        updateRecentPicture();
        updateRecentVideo();
        updateFavoriteFile();
        if (isCreatingAlbum) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    createAlbumDialog.show(getParentFragmentManager(), "Create album");
                    createAlbumDialog.setTextAndRequestFocus(createAlbumQuery);
                }
            }, 100);
            isCreatingAlbum = false;
        } else if (isScanfile) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    handleFindDuplicateFiles();
                }
            }, 100);
        }

    }

    @Override
    public void onStop() {
        if (thread != null) {
            if (thread.isAlive()) {
                thread.isInterrupted();
            }
        }
        super.onStop();
    }

    private void setUpFavoriteVideoList(View convertView) {
        RecyclerView favVideoRecycleView = convertView.findViewById(R.id.rccFavoriteVideo);
        favVideoRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        favVideoAdapter = new VideoListAdapter(getContext(), favVideos);
        favVideoRecycleView.setAdapter(favVideoAdapter);
    }

    private void setUpFavoritePictureList(View convertView) {
        RecyclerView favPhotoRecycleView = convertView.findViewById(R.id.rccFavoritePhoto);
        favPhotoRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        favPhotoAdapter = new PhotoListAdapter(getContext(), favPhotos);
        favPhotoRecycleView.setAdapter(favPhotoAdapter);
    }

    private void setUpRecentVideoList(View convertView) {
        RecyclerView recVideoRecycleView = convertView.findViewById(R.id.rccRecentVideos);
        recVideoRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        recVideoAdapter = new VideoListAdapter(getContext(), recVideos);
        recVideoRecycleView.setAdapter(recVideoAdapter);
    }

    private void setUpRecentPictureList(View convertView) {
        RecyclerView recPhotoRecycleView = convertView.findViewById(R.id.rccRecentPictures);
        recPhotoRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        recPhotoAdapter = new PhotoListAdapter(getContext(), recPhotos);
        recPhotoRecycleView.setAdapter(recPhotoAdapter);
    }

    private void processCreateAlbum() {
        createAlbumDialog.show(getParentFragmentManager(), "Create album");
    }

    private void processFindDuplicatedFile() {
        isScanfile = true;
        isBackgroundTaskRunning = true;
        final  List<String> dupUris = new ArrayList<>();
        List<MyPicture> allPic = imageDAO.getAllMediaFiles();
        Map<String, List<String>> dupMap = new HashMap<>();     //(md5String, List<id>)

        for (MyPicture pic:
                allPic) {
            if (!isBackgroundTaskRunning) {
                Thread.currentThread().isInterrupted();
                return;
            }
            Bitmap bm;
            ByteArrayOutputStream baos;
            byte[] bitmapBytes = new byte[1000];
            try {
                bm = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContext().getContentResolver(), Uri.parse(pic.getpContent().getAssertFileStringUri())));
                baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                bitmapBytes = baos.toByteArray();
            } catch (IOException e) {
                //
            }

            String md5String = MD5.calculateMD5(bitmapBytes);
            Log.d(UPDATE_FAVORITE_PICTURES, "processFindDuplicatedFile: " + md5String);
            if (dupMap.containsKey(md5String)) {
                List<String> dupList = dupMap.get(md5String);
                dupList.add(String.valueOf(pic.getpContent().getPictureId()));
            } else {
                List<String> newDupList = new ArrayList<>();
                newDupList.add(String.valueOf(pic.getpContent().getPictureId()));
                dupMap.put(md5String, newDupList);
            }
        }

        for (String i : dupMap.keySet()) {
            if (!isBackgroundTaskRunning) {
                Thread.currentThread().isInterrupted();
                return;
            }
            List<String> tmpDupFile = dupMap.get(i);
            if (tmpDupFile.size() > 1) {
                for (int index = 1; index < tmpDupFile.size(); index++) {
                    if (!isBackgroundTaskRunning) {
                        Thread.currentThread().isInterrupted();
                        return;
                    }
                    String id = tmpDupFile.get(index);
                    MyPicture picture = imageDAO.getMediaFile(Integer.valueOf(id));
                    if (picture != null) {
                        dupUris.add(picture.getpContent().getAssertFileStringUri());
                    }
                }
            }
        }
        if (!isBackgroundTaskRunning) {
            Thread.currentThread().isInterrupted();
            return;
        }
        deletingDuplicateFileDialog.setUriList(dupUris);
        isBackgroundTaskRunning = false;
        loadingDialog.dismissDialog();
        isScanfile = false;
        deletingDuplicateFileDialog.show(getParentFragmentManager(), "DeletingDuplicateFileDialog");
        Thread.currentThread().isInterrupted();
    }

    private void startCaptureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri uri = createNewImageUri();
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        newImageUri = uri.toString();
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private Uri createNewImageUri() {
        Uri result = null;

        ContentResolver resolver = getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MyGallery/Image");

        result = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        return result;
    }
    private Uri createNewVideoUri() {
        Uri result = null;

        ContentResolver resolver = getContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "newVideo");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MyGallery/Video");

        result = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

        return result;
    }

    private void updateRecentPicture() {
        List<Integer> recPhotoList = savedApplicationStateObject.getRecentPictures();
        recPhotos.clear();
        for (Integer photoId:
                recPhotoList) {
            for (int i = 0; i < imageDAO.getAllMediaFiles().size(); i++) {
                if (imageDAO.getAllMediaFiles().get(i).getpContent().getPictureId() == photoId) {
                    recPhotos.add(imageDAO.getAllMediaFiles().get(i));
                    break;
                }
            }
        }
        recPhotoAdapter.notifyDataSetChanged();
    }

    private void updateRecentVideo() {
        List<Integer> recVideoList = savedApplicationStateObject.getRecentVideos();
        recVideos.clear();
        for (Integer videoId:
                recVideoList) {
            for (int i = 0; i < videoDAO.getAllMediaFiles().size(); i++) {
                if (videoDAO.getAllMediaFiles().get(i).getvContent().getVideoId() == videoId) {
                    recVideos.add(videoDAO.getAllMediaFiles().get(i));
                    break;
                }
            }
        }
        recVideoAdapter.notifyDataSetChanged();
    }

    private void updateFavoriteFile() {
        List<Integer> favList = savedApplicationStateObject.getFavoriteFileIDs();
        favPhotos.clear();
        favVideos.clear();
        for (Integer fav:
                favList) {
            for (int i = 0; i < imageDAO.getAllMediaFiles().size(); i++) {
                if (imageDAO.getAllMediaFiles().get(i).getpContent().getPictureId() == fav) {
                    favPhotos.add(imageDAO.getAllMediaFiles().get(i));
                }
            }
            for (int i = 0; i < videoDAO.getAllMediaFiles().size(); i++) {
                if (videoDAO.getAllMediaFiles().get(i).getvContent().getVideoId() == fav) {
                    favVideos.add(videoDAO.getAllMediaFiles().get(i));
                }
            }
        }
        favPhotoAdapter.notifyDataSetChanged();
        favVideoAdapter.notifyDataSetChanged();
    }

    private void processRecordVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        ContentValues values = new ContentValues();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";

        values.put(MediaStore.Video.Media.TITLE, videoFileName);
        Uri videoUri = getContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        newVideoUri = videoUri.toString();
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

        startActivityForResult(videoIntent, REQUEST_VIDEO_CAPTURE);
    }

    private void handleFindDuplicateFiles() {
        loadingDialog.startLoading();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                processFindDuplicatedFile();
            }
        });
        thread.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (RESULT_CANCELED == resultCode) {
                FileHelper.removeFile(getActivity(), newImageUri);
            } else {
                imageDAO.updateList();
                onUpdateUI();
            }
        } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (RESULT_CANCELED == resultCode) {
                FileHelper.removeFile(getActivity(), newVideoUri);
            } else {
                videoDAO.updateList();
                onUpdateUI();
            }
        }
    }

    private void onUpdateUI() {
        updateTextView();
        updateRecentPicture();
        updateRecentVideo();
    }

    private void updateTextView() {
        imageDAO.updateList();
        videoDAO.updateList();
        txtPhotoCount.setText(String.valueOf(imageDAO.getAllMediaFiles().size()));
        txtFolderCount.setText(String.valueOf(FileHelper.getFolderCount(activity)));
        txtVideoCount.setText(String.valueOf(videoDAO.getAllMediaFiles().size()));
    }

    @Override
    public void onMsgFromMainToFrag(String sender, String message) {
        if (message.equals(UPDATE_FAVORITE_PICTURES) || message.equals(UPDATE_FAVORITE_VIDEOS)) {
            updateFavoriteFile();
        } else if (message.equals(UPDATE_RECENT_PICTURES)) {
            updateRecentPicture();
        } else if (message.equals(UPDATE_PICTURE_LIST_SIZE)) {
            updateRecentPicture();
            updateFavoriteFile();
            updateTextView();
        } else if (message.equals(UPDATE_RECENT_VIDEOS)) {
            updateRecentVideo();
        } else if (message.equals(UPDATE_VIDEO_LIST_SIZE)) {
            updateRecentVideo();
            updateFavoriteFile();
            updateTextView();
        } else if (String.valueOf(REMOVE_TAG).equals(message)) {
            deletingDuplicateFileDialog.onDeleteListener();
        } else if (CREATE_ALBUM.equals(message)) {
            startCreateAlbum();
        } else if (message.equals("FINISH_DELETE")) {
            imageDAO.updateList();
            onUpdateUI();
        }
    }

    public void startCreateAlbum() {
        String albumName = createAlbumDialog.getValue();
        boolean isCreated = false;
        isCreated = Album.createAlbum(activity, albumName);
        AlertDialog.Builder builderInner = new AlertDialog.Builder(activity);
        builderInner.setTitle("Create album");
        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });
        if (isCreated) {
            builderInner.setMessage("Successfully");
        } else {
            builderInner.setMessage("Failed");
        }
        builderInner.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnCreateAlbum:
                processCreateAlbum();
                break;
            case R.id.btnCaptureImage:
                startCaptureImage();
                break;
            case R.id.btnRecordVideo:
                processRecordVideo();
                break;
            case R.id.btnScanDuplicateFile:
                handleFindDuplicateFiles();
                break;
        }
    }
}

