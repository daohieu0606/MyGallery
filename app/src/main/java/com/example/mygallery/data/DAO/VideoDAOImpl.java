package com.example.mygallery.data.DAO;

import android.app.Activity;
import android.content.Context;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.VideoGet;
import com.CodeBoy.MediaFacer.mediaHolders.videoContent;
import com.example.mygallery.data.SavedApplicationStateObject;
import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.model.MyVideo;

import java.util.ArrayList;
import java.util.List;

public class VideoDAOImpl implements MediaFileDAO<MyVideo> {

    List<MyVideo> videoList;
    Context context;
    SavedApplicationStateObject savedApplicationStateObject;

    public VideoDAOImpl(Context newContext) {
        context = newContext;
        videoList = new ArrayList<MyVideo>();
        savedApplicationStateObject = SavedApplicationStateObject.getInstance();

        loadVideoList();
        loadFavoriteVideo(videoList);
    }
    private void loadVideoList() {
        ArrayList<videoContent> allVideos;
        allVideos = MediaFacer
                .withVideoContex(context)
                .getAllVideoContent(VideoGet.externalContentUri);

        for (int i = 0; i <allVideos.size(); i++) {
            videoContent vContent = allVideos.get(i);
            MyVideo video = new MyVideo(vContent, false);
            videoList.add(video);
        }
    }
    public List<MyVideo> getAllMediaFiles() {
        return videoList;
    }

    @Override
    public MyVideo getMediaFile(int id) {

        MyVideo result = null;

        for (MyVideo video: videoList) {
            if (video.getvContent().getVideoId() == id){
                result = video;
                break;
            }
        }
        return result;
    }

    @Override
    public void updateMediaFile(int vidId) {

        MyVideo currentVideo = null;

        for (MyVideo video:
                getAllMediaFiles()) {
            if (video.getvContent().getVideoId() == vidId) {
                currentVideo = video;
            }
        }

        if (currentVideo == null)  return;

        ArrayList<videoContent> videoContents;
        videoContents = MediaFacer
                .withVideoContex(context)
                .getAllVideoContent(VideoGet.externalContentUri);

        for (videoContent vc:
                videoContents) {
            if (vc.getVideoId() == currentVideo.getvContent().getVideoId()) {
                currentVideo.setvContent(vc);
                break;
            }
        }
    }

    @Override
    public void updateList() {
        videoList.clear();
        loadVideoList();
    }

    public void loadFavoriteVideo(List<MyVideo> videos) {
        for (MyVideo video:
                videos) {
            if (savedApplicationStateObject.getFavoriteFileIDs().contains(video.getvContent().getVideoId())){
                video.setFavorite(true);
            } else {
                video.setFavorite(false);
            }
        }
    }

    @Override
    public void addMediaFile(MyVideo mediaFile) {

    }

    @Override
    public void deleteMediaFile(MyVideo mediaFile) {
        FileHelper.removeFile((Activity) context,mediaFile.getvContent().getAssetFileStringUri());
    }

    public void addFavoriteVideo(int id) {
        for (MyVideo video:
                videoList) {
            if ((int)video.getvContent().getVideoId() == id) {
                video.setFavorite(true);
                savedApplicationStateObject.addFavoriteFileID(id);
                break;
            }
        }
    }

    public void delFavoriteVideo(int id) {
        for (MyVideo video:
                videoList) {
            if ((int)video.getvContent().getVideoId()== id) {
                video.setFavorite(false);
                savedApplicationStateObject.removeFavoriteFileID(id);
                break;
            }
        }
    }

    public void addCurrentVideo(int id) {
        for (MyVideo video:
                videoList) {
            if (video.getvContent().getVideoId() == id){
                savedApplicationStateObject.addRecentVideoID(id);
                break;
            }
        }
    }
}
