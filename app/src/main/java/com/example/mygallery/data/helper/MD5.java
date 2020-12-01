package com.example.mygallery.data.helper;


/*
* SOURCE:
* https://github.com/CyanogenMod/android_packages_apps_CMUpdater/blob/cm-10.2/src/com/cyanogenmod/updater/utils/MD5.java
* */

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final String TAG = "MD5";

/*    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }*/

    public static String calculateMD5(byte[] bytes) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(bytes,0, bytes.length);
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }

    public static boolean isEquals(String firstMd5, String secondMd5) {
        boolean result;

        result = firstMd5.equalsIgnoreCase(secondMd5);

        return result;
    }
}