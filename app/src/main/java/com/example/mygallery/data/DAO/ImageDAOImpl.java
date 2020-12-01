package com.example.mygallery.data.DAO;

import android.app.Activity;
import android.content.Context;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.PictureGet;
import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;
import com.example.mygallery.data.SavedApplicationStateObject;
import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.model.MyPicture;

import java.util.ArrayList;
import java.util.List;

public class ImageDAOImpl implements MediaFileDAO<MyPicture> {

    List<MyPicture> pictureList;
    Context context;
    SavedApplicationStateObject savedApplicationStateObject;

    public ImageDAOImpl(Context newContext) {

        savedApplicationStateObject = SavedApplicationStateObject.getInstance();
        pictureList = new ArrayList<MyPicture>();
        context = newContext;

        loadPictureList();
        loadFavoriteImage(pictureList);
    }

    @Override
    public List<MyPicture> getAllMediaFiles() {
        return pictureList;
    }

    @Override
    public MyPicture getMediaFile(int id) {
        MyPicture result = null;

        for (MyPicture picture: pictureList) {
            if (picture.getpContent().getPictureId() == id){
                result = picture;
                break;
            }
        }
        return result;
    }

    @Override
    public void updateList() {
        pictureList.clear();
        loadPictureList();
        loadFavoriteImage(pictureList);
    }

    private void loadPictureList() {
        ArrayList<pictureContent> allPictures;
        allPictures = MediaFacer
                .withPictureContex(context)
                .getAllPictureContents(PictureGet.externalContentUri);

        for (int i = 0; i <allPictures.size(); i++) {
            pictureContent pContent = allPictures.get(i);
            MyPicture picture = new MyPicture(pContent, false);
            pictureList.add(picture);
        }
    }
    @Override
    public void updateMediaFile(int imgId) {

        MyPicture currentPicture = null;

        for (MyPicture pic:
             getAllMediaFiles()) {
            if (pic.getpContent().getPictureId() == imgId) {
                currentPicture = pic;
            }
        }

        if (currentPicture == null)  return;

        ArrayList<pictureContent> allPictures;
        allPictures = MediaFacer
                .withPictureContex(context)
                .getAllPictureContents(PictureGet.externalContentUri);
        for (pictureContent pc:
             allPictures) {
            if (pc.getPictureId() == currentPicture.getpContent().getPictureId()) {
                currentPicture.setpContent(pc);
                break;
            }
        }

    }

    @Override
    public void addMediaFile(MyPicture mediaFile) {

    }

    @Override
    public void deleteMediaFile(MyPicture picture) {
        FileHelper.removeFile((Activity) context, picture.getpContent().getAssertFileStringUri());
    }

    public void loadFavoriteImage(List<MyPicture> myPictures) {
        for (MyPicture picture:
                myPictures) {
            if (savedApplicationStateObject.getFavoriteFileIDs().contains(picture.getpContent().getPictureId())){
                picture.setFavorite(true);
            } else {
                picture.setFavorite(false);
            }
        }
    }

    public void addFavoriteImage(int id) {
        for (MyPicture picture:
             pictureList) {
            if (picture.getpContent().getPictureId()== id) {
                picture.setFavorite(true);
                savedApplicationStateObject.addFavoriteFileID(id);
                break;
            }
        }
    }

    public void delFavoriteImage(int id) {
        for (MyPicture picture:
             pictureList) {
            if (picture.getpContent().getPictureId() == id) {
                picture.setFavorite(false);
                savedApplicationStateObject.removeFavoriteFileID(id);
                break;
            }
        }
    }

    public void addCurrentImage(int id) {
        for (MyPicture picture:
             pictureList) {
            if (picture.getpContent().getPictureId() == id){
                savedApplicationStateObject.addRecentImageID(id);
                break;
            }
        }
    }
}
