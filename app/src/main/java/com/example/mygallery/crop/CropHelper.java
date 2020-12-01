package com.example.mygallery.crop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.mygallery.R;
import com.example.mygallery.activity.MainActivity;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class CropHelper {
    public static final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";

    public static void startCrop(Activity activity, @NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMG_NAME + ".png";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(activity.getCacheDir(), destinationFileName)));

        uCrop.withAspectRatio(1, 1);
/*        uCrop.withAspectRatio(3, 4);
        uCrop.withAspectRatio(4, 6);
        ....*/
        uCrop.withMaxResultSize(450,450);
        uCrop.withOptions(getCropOptions(activity));

        uCrop.start(activity);
    }

    public static UCrop.Options getCropOptions(Activity activity) {
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(70);

        //CompressType
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        //UI
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        //Color
        options.setStatusBarColor(activity.getApplicationContext().getResources().getColor(R.color.prime6));
        options.setToolbarColor(activity.getApplicationContext().getResources().getColor(R.color.prime3));

        options.setToolbarTitle("Cropperrrrr");
        return options;
    }
}
