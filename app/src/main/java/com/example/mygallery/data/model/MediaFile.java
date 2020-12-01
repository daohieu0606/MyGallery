package com.example.mygallery.data.model;

import java.io.Serializable;

public class MediaFile implements Serializable {
    protected boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
