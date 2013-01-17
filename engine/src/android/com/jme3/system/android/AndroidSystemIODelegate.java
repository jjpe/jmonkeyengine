package com.jme3.system.android;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

import com.jme3.asset.AndroidImageInfo;
import com.jme3.system.AbstractSystemIODelegate;
import com.jme3.texture.Image;
import com.jme3.texture.image.DefaultImageRaster;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.AndroidScreenshots;

public class AndroidSystemIODelegate extends AbstractSystemIODelegate {

    static {
        try {
            System.loadLibrary("bulletjme");
        } catch (UnsatisfiedLinkError e) {
        }
    }
	
    @Override
    public ImageRaster createImageRaster(Image image, int slice) {
        if (image.getEfficentData() != null) {
            return (AndroidImageInfo) image.getEfficentData();
        } else {
            return new DefaultImageRaster(image, slice);
        }
    }

	@Override
    public void writeImageFile(	OutputStream outStream,
    							String format, 
    							ByteBuffer imageData, 
    							int width, 
    							int height) throws IOException {
        Bitmap bitmapImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        AndroidScreenshots.convertScreenShot(imageData, bitmapImage);
        Bitmap.CompressFormat compressFormat;
        if (format.equals("png")) {
            compressFormat = Bitmap.CompressFormat.PNG;
        } else if (format.equals("jpg")) {
            compressFormat = Bitmap.CompressFormat.JPEG;
        } else {
            throw new UnsupportedOperationException("Only 'png' and 'jpg' formats are supported on Android");
        }
        bitmapImage.compress(compressFormat, 95, outStream);
        bitmapImage.recycle();
    }
	
	@Override
    public synchronized File getStorageFolder() {
		// FIXME dirty way to obtain the activity
		Activity activity = JmeAndroidSystem.getActivity();
        //http://developer.android.com/reference/android/content/Context.html#getExternalFilesDir
        //http://developer.android.com/guide/topics/data/data-storage.html

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // getExternalFilesDir automatically creates the directory if necessary.
            // directory structure should be: /mnt/sdcard/Android/data/<packagename>/files
            // when created this way, the directory is automatically removed by the Android
            //   system when the app is uninstalled
            storageFolder = activity.getApplicationContext().getExternalFilesDir(null);
            Logger.getLogger(AndroidSystemIODelegate.class.getName()).log(Level.INFO, "Storage Folder Path: {0}", storageFolder.getAbsolutePath());

            return storageFolder;
        } else {
            return null;
        }

    }
}
