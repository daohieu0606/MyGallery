package com.example.mygallery.data.DAO;

import java.util.List;

public interface MediaFileDAO<T> {
    public List<T> getAllMediaFiles();
    public T getMediaFile(int id);
    public void updateMediaFile(int id);
    public void updateList();
    public void addMediaFile(T mediaFile);
    public void deleteMediaFile(T mediaFile);
}
