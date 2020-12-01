package com.example.mygallery.data.filter;

import com.example.mygallery.data.helper.FileHelper;
import com.example.mygallery.data.model.MyPicture;
import com.example.mygallery.data.model.MyVideo;

import java.util.ArrayList;
import java.util.List;

public class FileFilter {
    public static List<MyPicture> filterPictureByFolder(List<MyPicture> pictures, String folderName) {
        List<MyPicture> result = new ArrayList<>();
        for (MyPicture picture:
             pictures) {
            if (folderName.equals(FileHelper.getFolder(picture.getpContent().getPicturePath()))) {
                result.add(picture);
            }
        }
        return  result;
    }
    public static List<MyVideo> filterVideoByFolder(List<MyVideo> videos, String folderName) {
        List<MyVideo> result = new ArrayList<>();
        for (MyVideo video:
             videos) {
            if (folderName.equals(FileHelper.getFolder(video.getvContent().getPath()))) {
                result.add(video);
            }
        }
        return  result;
    }
}
