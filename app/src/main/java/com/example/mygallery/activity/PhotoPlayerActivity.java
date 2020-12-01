package com.example.mygallery.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.R;
import com.example.mygallery.adapter.HorizontalPhotoListAdapter;
import com.example.mygallery.crop.CropHelper;
import com.example.mygallery.data.DAO.ImageDAOImpl;
import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.model.Album;
import com.example.mygallery.data.model.MyPicture;
import com.example.mygallery.dialog.AlbumChoiceDialog;
import com.example.mygallery.dialog.EditTextDialog;
import com.example.mygallery.dialog.PhotoDetailDialog;
import com.example.mygallery.response.MainCallback;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoPlayerActivity extends AppCompatActivity implements View.OnClickListener, EditTextDialog.OnEditTextDialogListener, MainCallback, AlbumChoiceDialog.OnItemClickListener {

    private static final String UPDATE_FAVORITE_PICTURES = "UPDATE_FAVORITE_PICTURES";
    private static final String UPDATE_RECENT_PICTURES = "UPDATE_RECENT_PICTURES";
    private static final String UPDATE_PICTURE_LIST_SIZE = "UPDATE_PICTURE_LIST_SIZE";
    private static final String ADAPTER_TAG = "NearestPhotoAdapter";

    private static final int RENAME_TAG = 101;
    private static final int REMOVE_TAG = 102;
    private static final String DETAIL_DIALOG = "DETAIL_DIALOG";
    private static final String RENAME_QUERY_TAG = "RENAME_QUERY_TAG";
    private static final String IS_SHOW_DIALOG_TAG = "IS_SHOW_DIALOG_TAG";
    private static final String RENAME_DIALOG = "RENAME_DIALOG";
    private static final String ALBUM_CHOICE_DIALOG = "ALBUM_CHOICE_DIALOG";
    private static final String TAG = "photoplayerrrr";

    private EditTextDialog editTextDialog;
    private AlbumChoiceDialog albumChoiceDialog;
    private PhotoDetailDialog photoDetailDialog;

    private RecyclerView recyclerView;
    private HorizontalPhotoListAdapter adapter;

    private ImageView imgPhotoShowStage;
    private TextView txtPhotoName;
    private TextView txtSize;
    private ToggleButton btnFavoriteMark;

    private ImageDAOImpl imageDAO;
    private int currentPictureID;

    private Button btnLeft;
    private Button btnRight;
    private Button btnClose;
    private Button btnMenuPopUp;
    private Button btnDeletePhoto;
    private Button btnCrop;

    private int CURRENT_MIDDLE_IN_NEAREST_LIST;
    private Intent returnIntent;

    private boolean isShowingAlbumChoiceDialog;
    private boolean isShowingDetailDialog;
    private boolean isShowingRenameDialog;
    private String savedRenameQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_photo);
        setUp();
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(IS_SHOW_DIALOG_TAG) != null) {
                Log.d(TAG, "onCreate: " + savedInstanceState.getString(IS_SHOW_DIALOG_TAG));
                if (savedInstanceState.getString(IS_SHOW_DIALOG_TAG).equals(DETAIL_DIALOG)) {
                    isShowingDetailDialog = true;
                } else if (savedInstanceState.getString(IS_SHOW_DIALOG_TAG).equals(ALBUM_CHOICE_DIALOG)) {
                    isShowingAlbumChoiceDialog = true;
                } else if (savedInstanceState.getString(IS_SHOW_DIALOG_TAG).equals(RENAME_DIALOG)) {
                    isShowingRenameDialog = true;
                    savedRenameQuery = savedInstanceState.getString(RENAME_QUERY_TAG);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isShowingDetailDialog) {
            photoDetailDialog.show(getSupportFragmentManager(), "Photo details");
            isShowingDetailDialog = false;
        } else  if (isShowingAlbumChoiceDialog) {
            albumChoiceDialog.show(getSupportFragmentManager(), "Choose Album");
            isShowingAlbumChoiceDialog = false;
        } else  if (isShowingRenameDialog) {
            editTextDialog.setTextAndRequestFocus(savedRenameQuery);
            editTextDialog.show(getSupportFragmentManager(), "Rename");
            isShowingRenameDialog = false;
            savedRenameQuery = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (editTextDialog.isShowing()) {
            savedRenameQuery = editTextDialog.getValue();
            isShowingRenameDialog = true;
            editTextDialog.dismiss();
            Log.d(TAG, "onPause: edit");
        } else if (albumChoiceDialog.isShowing()) {
            isShowingAlbumChoiceDialog = true;
            albumChoiceDialog.dismiss();
            Log.d(TAG, "onPause: album choice");
        } else if (photoDetailDialog.isShowing()) {
            isShowingDetailDialog = true;
            photoDetailDialog.dismiss();
            Log.d(TAG, "onPause: detail");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isShowingDetailDialog) {
            Log.d(TAG, "onSaveInstanceState: save detail" );
            outState.putString(IS_SHOW_DIALOG_TAG, DETAIL_DIALOG);
        } else  if (isShowingAlbumChoiceDialog) {
            outState.putString(IS_SHOW_DIALOG_TAG, ALBUM_CHOICE_DIALOG);
        } else  if (isShowingRenameDialog) {
            outState.putString(IS_SHOW_DIALOG_TAG, RENAME_DIALOG);
            outState.putString(RENAME_QUERY_TAG, savedRenameQuery);
        }
    }

    @SuppressLint({"WrongConstant", "CheckResult"})
    private void setUp() {
        returnIntent = new Intent();
        btnFavoriteMark = findViewById(R.id.btnFavorite);
        imgPhotoShowStage = findViewById(R.id.imgPhotoShowStage);
        txtPhotoName = findViewById(R.id.txtPhotoName);
        txtSize = findViewById(R.id.txtSize);

        recyclerView = findViewById(R.id.rccNearestPhotoList);

        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnClose = findViewById(R.id.btnClose);
        btnMenuPopUp = findViewById(R.id.btnMenuPopUp);
        btnCrop = findViewById(R.id.btnCrop);

        imageDAO = new ImageDAOImpl(this);
        currentPictureID = -1;
        CURRENT_MIDDLE_IN_NEAREST_LIST = 0;

        btnDeletePhoto = findViewById(R.id.btnDeletePhoto);
        btnDeletePhoto.setOnClickListener(this);

        currentPictureID = (int) getIntent().getExtras().get("IMAGE_ID");

        if (currentPictureID == -1) {
            finish();
        } else {
            //do nothing
        }

        imgPhotoShowStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhotoPlayerActivity.this, SinglePhotoActivity.class);

                MyPicture myPicture = imageDAO.getMediaFile(currentPictureID);
                if (myPicture == null) {
                    Toast.makeText(getApplicationContext(), "Error: file not file", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("image", myPicture.getpContent().getAssertFileStringUri());
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat
                                                        .makeSceneTransitionAnimation(
                                                                PhotoPlayerActivity.this,
                                                                imgPhotoShowStage,
                                                                "imageTransition");

                startActivity(intent);
            }
        });

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnMenuPopUp.setOnClickListener(this);
        btnFavoriteMark.setOnClickListener(this);
        btnCrop.setOnClickListener(this);

        adapter = new HorizontalPhotoListAdapter(imageDAO.getAllMediaFiles(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        editTextDialog = new EditTextDialog(this, "Rename");
        albumChoiceDialog = new AlbumChoiceDialog(this, Album.getAlbumList(this));
        photoDetailDialog = new PhotoDetailDialog(imageDAO.getMediaFile(currentPictureID));

        onUpdateUI();
    }

    private void updateMiddleNearestList(int offset) {
        if(CURRENT_MIDDLE_IN_NEAREST_LIST + offset >= 1
                && CURRENT_MIDDLE_IN_NEAREST_LIST + offset < imageDAO.getAllMediaFiles().size())
        {
            CURRENT_MIDDLE_IN_NEAREST_LIST += offset;
            recyclerView.smoothScrollToPosition(CURRENT_MIDDLE_IN_NEAREST_LIST - 1);
        }
    }

    private void onUpdateUI(){
        MyPicture currentPicture = null;
        for (MyPicture pic:
             imageDAO.getAllMediaFiles()) {
            if (pic.getpContent().getPictureId() == currentPictureID) {
                currentPicture = pic;
            }
        }

        if (currentPicture == null) {
            return;
        }

        bind(currentPicture.getpContent().getPicturePath());
        txtPhotoName.setText(currentPicture.getpContent().getPicturName());

        double size = (double)Math.round(currentPicture.getpContent().getPictureSize()*Math.pow(2,-20)*100)/100;
        txtSize.setText("Size: " + size + " MB ");

        btnFavoriteMark.setChecked(currentPicture.isFavorite());
        updateMiddleNearestList(imageDAO.getAllMediaFiles().indexOf(currentPicture) - CURRENT_MIDDLE_IN_NEAREST_LIST);

        imageDAO.addCurrentImage(currentPictureID);
        returnIntent.putExtra(UPDATE_RECENT_PICTURES, String.valueOf(currentPictureID));
    }

    private void bind(String path) {
        Glide.with(this).load(path)
                .apply(new RequestOptions().centerCrop())
                .into(imgPhotoShowStage);
    }
    private void test() {

    }
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(R.id.btnLeft == id){
            handleLeftButton();
        } else if(R.id.btnRight == id){
            handleRightButton();
        }else if(R.id.btnFavorite == id){
            handleFavoriteButton();
        } else if(R.id.btnClose == id){
            handleCloseButton();
        } else if(R.id.btnMenuPopUp == id){
            handlePopUpMenuButton();
        } else if (R.id.btnDeletePhoto == id){
            handleDeletePhoto();
        } else if (R.id.btnCrop == id) {
            processCrop();
        }

    }

    private void processCrop() {
        MyPicture currentPicture = imageDAO.getMediaFile(currentPictureID);
        CropHelper.startCrop(this, Uri.parse(currentPicture.getpContent().getAssertFileStringUri()));
    }

    private void handleDeletePhoto() {
        MyPicture currentPicture = imageDAO.getMediaFile(currentPictureID);
        String uriStr = currentPicture.getpContent().getAssertFileStringUri();

        boolean isDeleted = FileHelper.removeFile(this, uriStr);
        if (isDeleted) {
            imageDAO.updateList();
            generateAnotherPictureId();
            onUpdateUI();
            adapter.notifyDataSetChanged();
            returnIntent.putExtra(UPDATE_PICTURE_LIST_SIZE, UPDATE_PICTURE_LIST_SIZE);
        }
    }

    private void generateAnotherPictureId() {
        if(imageDAO.getAllMediaFiles().size() == 0) {
            Toast.makeText(getApplicationContext(), "No exist picture in your device", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (CURRENT_MIDDLE_IN_NEAREST_LIST >= imageDAO.getAllMediaFiles().size()) {
            CURRENT_MIDDLE_IN_NEAREST_LIST = imageDAO.getAllMediaFiles().size() - 1;
        } else if (CURRENT_MIDDLE_IN_NEAREST_LIST < 0) {
            CURRENT_MIDDLE_IN_NEAREST_LIST = 0;
        }

        currentPictureID = imageDAO.getAllMediaFiles().get(CURRENT_MIDDLE_IN_NEAREST_LIST).getpContent().getPictureId();
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

    private void handleFavoriteButton() {
        returnIntent.putExtra(UPDATE_FAVORITE_PICTURES, imageDAO.getMediaFile(currentPictureID).getpContent().getPictureId());
        if (imageDAO.getMediaFile(currentPictureID).isFavorite()) {
            imageDAO.delFavoriteImage(imageDAO.getMediaFile(currentPictureID).getpContent().getPictureId());
        } else {
            imageDAO.addFavoriteImage(imageDAO.getMediaFile(currentPictureID).getpContent().getPictureId());
        }
    }

    private  void handlePopUpMenuButton(){
        PopupMenu popupMenu = new PopupMenu(this, btnMenuPopUp);
        popupMenu.getMenuInflater().inflate(R.menu.menu_photo_more_options,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.optSetWallPaper:
                        setWallpaper();
                        break;
                    case R.id.optChangeNamePicture:
                        openChangeNameDialog();
                        break;
                    case  R.id.optDetailPicture:
                        openDetailDialog();
                        break;
                    case R.id.slideshow:
                        processSlideShow();
                        break;
                    case R.id.moveToAlbum:
                        processMoveToAlbum();
                        break;
                    case R.id.shareImage:
                        handleShare();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void processMoveToAlbum() {
        albumChoiceDialog.show(getSupportFragmentManager(), "Choose album");
    }

    private void processSlideShow() {
        Intent imageSliderIntent = new Intent(PhotoPlayerActivity.this, SlideShowActivity.class);
        startActivity(imageSliderIntent);
    }

    private void openDetailDialog() {
        photoDetailDialog.show(getSupportFragmentManager(),"Detal Dialog");
    }

    private void openChangeNameDialog() {
        editTextDialog.show(getSupportFragmentManager(),"Chang Name Dialog");
    }

    private void setWallpaper()
    {
        String imgPath = imageDAO.getMediaFile(currentPictureID).getpContent().getPicturePath();
        Bitmap tmpbitmap = BitmapFactory.decodeFile(imgPath);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int w=displayMetrics.widthPixels;
        int h=displayMetrics.heightPixels;
        Bitmap bitmap=Bitmap.createScaledBitmap(tmpbitmap,w,h,false);

        try{
            wallpaperManager.setBitmap(bitmap);
            wallpaperManager.suggestDesiredDimensions(w,h);
            Toast.makeText(getApplicationContext(), "Đổi ảnh nền thành công!", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            Toast.makeText(getApplicationContext(),"Lỗi! Không thể đổi ảnh nền.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEdtDialogClicked() {
        String uriStr = imageDAO.getMediaFile(currentPictureID).getpContent().getAssertFileStringUri();
        boolean isRenamed =FileHelper.rename(this, uriStr, editTextDialog.getValue());
        if (isRenamed) {
            imageDAO.updateMediaFile(currentPictureID);
            onUpdateUI();
        }
    }

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
    }

    @Override
    public void onMsgToMain(String sender, String message) {
        if (sender.equals(ADAPTER_TAG)) {
            currentPictureID = Integer.valueOf(message);
            onUpdateUI();
        }
    }

    private void saveCropFile(Uri croppedFileUri) throws Exception {

        FileInputStream fileInputStream = new FileInputStream(new File(croppedFileUri.getPath()));

        ContentResolver resolver = getApplicationContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "newpicture");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MyGallery");

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        FileOutputStream fileOutputStream = (FileOutputStream) resolver.openOutputStream(uri);

        int length = 0;
        byte[] buffer = new byte[1024];
        while ((length = fileInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, length);
        }

        fileInputStream.close();
        fileOutputStream.close();
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
            imageDAO.updateList();
            handleDeletePhoto();
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                saveCropFile(resultUri);
                imageDAO.updateList();
                returnIntent.putExtra(UPDATE_PICTURE_LIST_SIZE, UPDATE_PICTURE_LIST_SIZE);
                Toast.makeText(getApplicationContext(), "Saved croped picture successfully.", Toast.LENGTH_LONG).show();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            Log.e(UPDATE_FAVORITE_PICTURES, resultUri.toString());
        } else if (resultCode == UCrop.RESULT_ERROR && requestCode == UCrop.REQUEST_CROP) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    @Override
    public void onAlbumDialogClick() {
        String selectedAlbum = albumChoiceDialog.getSelectedAlbum();
        MyPicture currentPic = imageDAO.getMediaFile(currentPictureID);
        boolean isMoved = Album.movePictureToAnotherAlbum(this, currentPic, selectedAlbum);

        AlertDialog.Builder builderInner = new AlertDialog.Builder(this);
        builderInner.setTitle("Move to album");
        builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });
        if (isMoved) {
            imageDAO.deleteMediaFile(currentPic);
            imageDAO.updateList();
            onUpdateUI();
            returnIntent.putExtra(UPDATE_PICTURE_LIST_SIZE, UPDATE_PICTURE_LIST_SIZE);
            returnIntent.putExtra(UPDATE_FAVORITE_PICTURES, UPDATE_FAVORITE_PICTURES);
            builderInner.setMessage("Successfully");
        } else {
            builderInner.setMessage("Failed");
        }
        builderInner.show();
    }

    private void handleShare() {
        MyPicture picture = imageDAO.getMediaFile(currentPictureID);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent share;
        try {
            File media = new File(picture.getpContent().getPicturePath());
            Uri uri = Uri.fromFile(media);
            share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        startActivity(Intent.createChooser(share, "share image"));
    }
}