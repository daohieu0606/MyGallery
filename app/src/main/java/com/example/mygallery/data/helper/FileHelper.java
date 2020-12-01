package com.example.mygallery.data.helper;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.mygallery.data.DAO.ImageDAOImpl;
import com.example.mygallery.data.DAO.VideoDAOImpl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileHelper {

    private static final int RENAME_TAG = 101;
    private static final int REMOVE_TAG = 102;

    public static boolean rename(Activity context, String uriStr, String newFileName) {

        boolean result = false;
        ContentValues updatedSongDetails = new ContentValues();
        updatedSongDetails.put(MediaStore.Images.Media.DISPLAY_NAME, newFileName);

        Uri uri = Uri.parse(uriStr);
        try{
            context.getContentResolver().update(uri, updatedSongDetails,null, null);
            result = true;
        } catch (SecurityException securityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException =
                            (RecoverableSecurityException)securityException;
                } else {
                    throw new RuntimeException(
                            securityException.getMessage(), securityException);
                }
                IntentSender intentSender =recoverableSecurityException.getUserAction()
                        .getActionIntent().getIntentSender();
                try{
                    context.startIntentSenderForResult(intentSender, RENAME_TAG,
                            null, 0, 0, 0, null);
                } catch (Exception e){

                }
            } else {                //it can be file is read-only mode
                throw new RuntimeException(
                        securityException.getMessage(), securityException);
            }
        }
        return result;
    }

    public static void addMedia(Context c, File f) {
/*        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(f));
        c.sendBroadcast(intent);*/
    }

/*    *//** Create a File for saving an image or video *//*
    public static File getOutputMediaFile(int type){

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraVideo");


        // Create the storage directory(MyCameraVideo) if it does not exist
        if (!mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs()){

                output.setText("Failed to create directory MyCameraVideo.");

                Toast.makeText(ActivityContext, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();

                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }


        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());

        File mediaFile;

        if(type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");

        } else {
            return null;
        }

        return mediaFile;
    }*/

    public static int getFolderCount(Context context) {
        ImageDAOImpl imageDAO = new ImageDAOImpl(context);
        VideoDAOImpl videoDAO = new VideoDAOImpl(context);

        int result = 0;
        List<String> folders = new ArrayList<>();

        for (int i = 0; i < imageDAO.getAllMediaFiles().size(); i++) {
            String path = FileHelper.getFolder(imageDAO.getAllMediaFiles().get(i).getpContent().getPicturePath());
            if (!folders.contains(path)) {
                folders.add(path);
            }
        }
        for (int i = 0; i < videoDAO.getAllMediaFiles().size(); i++) {
            String path = FileHelper.getFolder(videoDAO.getAllMediaFiles().get(i).getvContent().getPath());
            if (!folders.contains(path)) {
                folders.add(path);
            }
        }
        result = folders.size();

        return result;
    }

    public static String getFolder(String absolutePath) {
        String result = null;
        String[] folders = absolutePath.split("/");

        result = folders[folders.length - 2];

        return result;
    }

    public static boolean removeFile(Activity context, String uriStr) {

        boolean result = false;

        Uri uri = Uri.parse(uriStr);
        try{
            context.getContentResolver().delete(uri, null, null);
            result = true;
        } catch (SecurityException securityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException =
                            (RecoverableSecurityException)securityException;
                } else {
                    throw new RuntimeException(
                            securityException.getMessage(), securityException);
                }
                IntentSender intentSender =recoverableSecurityException.getUserAction()
                        .getActionIntent().getIntentSender();
                try{
                    context.startIntentSenderForResult(intentSender, REMOVE_TAG,
                            null, 0, 0, 0, null);
                } catch (Exception e){

                }
            } else {                //it can be file is read-only mode
                throw new RuntimeException(
                        securityException.getMessage(), securityException);
            }
        }

        return result;
    }


    public static File createImageFile(Context context) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


    public static void createFolder(Context context, String newFolderName) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + newFolderName);
        if(!file.exists()) {
            file.mkdirs();
        }
    }

}
























