package com.example.mygallery.data.model;

import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;

import java.io.Serializable;

public class MyPicture extends MediaFile implements Serializable {
    private  pictureContent pContent;

    public MyPicture(pictureContent newPictureContent, boolean isFavorite) {
        pContent = newPictureContent;
        this.isFavorite = isFavorite;
    }

    public pictureContent getpContent() {
        return pContent;
    }

    public void setpContent(pictureContent pContent) {
        this.pContent = pContent;
    }

    public void setpName(String name){
        this.pContent.setPicturName(name);
    }
}
