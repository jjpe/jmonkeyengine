package com.jme3.system;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;

import com.jme3.asset.AndroidImageInfo;
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
	public void initialize(AppSettings settings) {
		// TODO Auto-generated method stub

	}

}
