package com.example.mygallery.data;

import java.io.Serializable;
import java.util.ArrayList;

public class SavedApplicationStateObject implements Serializable {
    private ArrayList<Integer> recentVideos = new ArrayList<>();
    private ArrayList<Integer> recentPictures = new ArrayList<>();
    private ArrayList<Integer> favoriteFileIDs = new ArrayList<>();
    private static final int MAX_RECENT_FILE_COUNT = 10;

    private static SavedApplicationStateObject savedApplicationStateObjectInstance;
    private SavedApplicationStateObject() {

    }
    public static synchronized SavedApplicationStateObject getInstance() {
        if (savedApplicationStateObjectInstance == null) {
            savedApplicationStateObjectInstance = new SavedApplicationStateObject();
        }
        return savedApplicationStateObjectInstance;
    }
    public void addRecentVideoID(Integer vidId) {

        if (recentVideos.size() >= MAX_RECENT_FILE_COUNT) {
            recentVideos.remove(0);
        }
        if (recentVideos.contains(vidId)) {
            recentVideos.remove(recentVideos.indexOf(vidId));
        }
        recentVideos.add(0, vidId);
    }

    public void addRecentImageID(Integer picId) {
        if (recentPictures.size() >= MAX_RECENT_FILE_COUNT) {
            recentPictures.remove(0);
        }
        if (recentPictures.contains(picId)) {
            recentPictures.remove(recentPictures.indexOf(picId));
        }
        recentPictures.add(0, picId);
    }

    public void removeFavoriteFileID(int fileId) {
        if (favoriteFileIDs.contains(fileId)) {
            favoriteFileIDs.remove(favoriteFileIDs.indexOf(fileId));
        }
    }

    public void addFavoriteFileID(Integer fileId) {
        if (favoriteFileIDs.contains(fileId)) {
            return;
        }
        favoriteFileIDs.add(fileId);
    }

    public void setRecentVideos(ArrayList<Integer> recentVideos) {
        this.recentVideos = recentVideos;
    }

    public void setRecentPictures(ArrayList<Integer> recentPictures) {
        this.recentPictures = recentPictures;
    }

    public void setFavoriteFileIDs(ArrayList<Integer> favoriteFileIDs) {
        this.favoriteFileIDs = favoriteFileIDs;
    }

    public static void setSavedApplicationStateObjectInstance(SavedApplicationStateObject savedApplicationStateObjectInstance) {
        SavedApplicationStateObject.savedApplicationStateObjectInstance = savedApplicationStateObjectInstance;
    }

    public ArrayList<Integer> getRecentVideos() {return recentVideos;}
    public ArrayList<Integer> getRecentPictures() {return recentPictures;}
    public ArrayList<Integer> getFavoriteFileIDs() {return favoriteFileIDs;}
}
