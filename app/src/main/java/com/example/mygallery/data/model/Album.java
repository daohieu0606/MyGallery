package com.example.mygallery.data.model;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Album {

    private String AlbumName;

    public Album(String albumName) {
        AlbumName = albumName;
    }

    public String getAlbumName() {
        return AlbumName;
    }

    public static boolean createAlbum(Context context, String albumName) {
        boolean result = false;

        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MyGallery/Album/" + albumName);

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        resolver.delete(uri, null, null);
        if (uri != null) {
            result = true;
        }

        return result;
    }

    public static List<Album> getAlbumList(Context context) {
        List<Album> albums = new ArrayList<>();

        File albumFolder = new File("/storage/emulated/0/DCIM/MyGallery/Album/");
        if (albumFolder.list() != null) {
            for (String albumStr:
                    albumFolder.list()) {

                Album album = new Album(albumStr);
                albums.add(album);
            }
        }
        return albums;
    }

    public static boolean movePictureToAnotherAlbum(Activity context, MyPicture myPicture, String AlbumName) {
        boolean result = false;

        ContentResolver resolver = context.getApplicationContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, myPicture.getpContent().getPicturName());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MyGallery/Album/" + AlbumName);
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = (FileOutputStream) resolver.openOutputStream(uri);
            FileInputStream fileInputStream = (FileInputStream) resolver.openInputStream(Uri.parse(myPicture.getpContent().getAssertFileStringUri()));

            int length = 0;
            byte[] buffer = new byte[1024];
            while ((length = fileInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }

            fileInputStream.close();
            fileOutputStream.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }
}
