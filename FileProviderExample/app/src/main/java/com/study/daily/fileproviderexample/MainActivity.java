package com.study.daily.fileproviderexample;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.BuildCompat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources resources = getResources();
        ContentResolver resolver = getContentResolver();

        File photoDirectory = new File(getFilesDir(), "images");
        File imageToShare = new File(photoDirectory, "shared_image.jpg");

        String filePath = imageToShare.getPath();
        Uri fileUri = Uri.parse("file://" + filePath);

        if (!photoDirectory.exists()) {
            photoDirectory.mkdir();
        }

        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(fileUri, "w", null);
             FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
             InputStream is = resources.openRawResource(R.raw.shared_image)) {
            while (true) {
                int data = is.read();
                if (data == -1) {
                    break;
                }
                fos.write(data);
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        Uri contentUri = FileProvider.getUriForFile(this,
                "com.study.daily.fileproviderexample.files",
                imageToShare);

        ShareCompat.IntentBuilder intentBuilder = new ShareCompat.IntentBuilder(this);
        intentBuilder.setType("image/jpg")
                .setStream(contentUri)
                .startChooser();
    }
}