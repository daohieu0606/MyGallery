package com.example.mygallery.data.model;

import com.CodeBoy.MediaFacer.mediaHolders.videoContent;

import java.io.Serializable;

public class MyVideo extends MediaFile implements Serializable {
    private videoContent vContent;

    public MyVideo(videoContent newVideoContent, boolean isFavorite){
        this.vContent = newVideoContent;
        this.isFavorite = isFavorite;
    }

    public videoContent getvContent() {
        return vContent;
    }

    public void setvContent(videoContent vContent) {
        this.vContent = vContent;
    }
}
