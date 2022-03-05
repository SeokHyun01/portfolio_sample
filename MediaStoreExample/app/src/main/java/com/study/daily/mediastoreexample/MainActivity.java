package com.study.daily.mediastoreexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

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

        ContentResolver resolver = this.getContentResolver();

        Uri ImageCollection;

        ImageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        ContentValues imageDetails = new ContentValues();
        imageDetails.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/my_images");
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "my_image.jpg");
        imageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);

        Uri imageUri = resolver.insert(ImageCollection, imageDetails);

        Resources resources = getResources();

        Log.d(TAG, imageUri.toString());

        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(imageUri, "w", null);
             FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
             InputStream is = resources.openRawResource(R.raw.my_image)) {
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

        imageDetails.clear();
        imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
        resolver.update(imageUri, imageDetails, null, null);
    }
}