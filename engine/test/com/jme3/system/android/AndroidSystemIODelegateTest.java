package com.jme3.system.android;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.NonStrict;
import mockit.NonStrictExpectations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.jme3.asset.AndroidImageInfo;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.DefaultImageRaster;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;

public class AndroidSystemIODelegateTest {
	private AndroidSystemIODelegate delegate;
	
	@Before
	public void setup() {
		delegate = new AndroidSystemIODelegate();
	}
	
	/**
	 * Tests creating an image raster if image.efficientData != null
	 */
	@Test
	public void createImageRasterWithNonNullEfficentDataTest() {
		// l. 57/58 - image.getEfficentData() != null
		
		final AndroidImageInfo efficentData = new AndroidImageInfo(null);
		
		new MockUp<Image>() {
			@SuppressWarnings("unused")
			@Mock
			public Object getEfficentData() {
				return efficentData; 
			}
		};
		
		Image img = new Image();
		int w = 5;
		int h = 3;
		img.setHeight(h);
		img.setWidth(w);
		img.setFormat(Format.RGBA8);
		final ByteBuffer buff = BufferUtils.createByteBuffer(h*w*4);
		int pixel00 = 0xFFF00F00;
		int pixel21 = 0xAA996655;
		// apparently putting the int as bytes reverses the byte order
		buff.putInt(0, Integer.reverseBytes(pixel00));
		buff.putInt(1*w*4+2*4, Integer.reverseBytes(pixel21));
		img.setData(buff);
		// select the buffer we gave
		int slice = 0;
		ImageRaster raster = delegate.createImageRaster(img, slice);
		
		Assert.assertEquals("Did not instantiate an AndroidImageInfo instance", 
				efficentData, raster);
	}
	
	/**
	 * Tests creating an image raster if image.efficientData == null
	 */
	@Test
	public void createImageRasterWithNullEfficentDataTest() {
		// l. 59/60 - else
		
		new MockUp<Image>() {
			@SuppressWarnings("unused")
			@Mock
			public Object getEfficentData() {
				return null;
			}
		};
		
		// Set up the image 
		Image img = new Image();
		int w = 5;
		int h = 3;
		img.setHeight(h);
		img.setWidth(w);
		img.setFormat(Format.RGBA8);
		final ByteBuffer buff = BufferUtils.createByteBuffer(h*w*4);
		int pixel00 = 0xFFF00F00;
		int pixel21 = 0xAA996655;
		// apparently putting the int as bytes reverses the byte order
		buff.putInt(0, Integer.reverseBytes(pixel00));
		buff.putInt(1*w*4+2*4, Integer.reverseBytes(pixel21));
		img.setData(buff);
		// select the buffer we gave
		int slice = 0;
		ImageRaster raster = delegate.createImageRaster(img, slice);
		
		Assert.assertEquals("Did not instantiate a DefaultImageRaster instance", 
				DefaultImageRaster.class, raster.getClass());
	}
	
	/**
	 * Tests writing a PNG image file
	 * @throws IOException
	 */
	@Test
	public void writePngImageFileTest(final OutputStream os) throws IOException {
		// l. 44 - format.equals("png")

		final String format = "png";
		final int width = 2;
		final int height = 2;
		final ByteBuffer buf = BufferUtils.createByteBuffer(width*height*4);
		new Expectations() {
			@NonStrict Bitmap mock;
			{
				Bitmap.createBitmap(anyInt, anyInt, (Bitmap.Config) withNotNull());result=mock;
				mock.getWidth();result=width;
				mock.getHeight();result=height;
				mock.compress(Bitmap.CompressFormat.PNG, anyInt, os);result=true;times=1;
			}
		};
		
		delegate.writeImageFile(os, format, buf, width, height);
	}
	
	/**
	 * Tests writing a JPG image file
	 * @throws IOException
	 */
	@Test
	public void writeJpgImageFileTest(final OutputStream os) throws IOException {

		final String format = "jpg";
		final int width = 2;
		final int height = 2;
		final ByteBuffer buf = BufferUtils.createByteBuffer(width*height*4);
		new Expectations() {
			@NonStrict Bitmap mock;
			{
				Bitmap.createBitmap(anyInt, anyInt, (Bitmap.Config) withNotNull());result=mock;
				mock.getWidth();result=width;
				mock.getHeight();result=height;
				mock.compress(Bitmap.CompressFormat.JPEG, anyInt, os);result=true;times=1;
			}
		};
		
		delegate.writeImageFile(os, format, buf, width, height);
	}
	
	/**
	 * Tests writing an image with an unsupported format
	 * @throws IOException
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void writeUnsupportedImageFileTest(final OutputStream os) throws IOException {
		// l. 49 - UnsupportedOperationException
		
		final String format = "derpformat";
		final int width = 2;
		final int height = 2;
		final ByteBuffer buf = BufferUtils.createByteBuffer(width*height*4);
		new Expectations() {
			@NonStrict Bitmap mock;
			{
				Bitmap.createBitmap(anyInt, anyInt, (Bitmap.Config) withNotNull());result=mock;
				mock.getWidth();result=width;
				mock.getHeight();result=height;
			}
		};
		
		delegate.writeImageFile(os, format, buf, width, height);
		
	}
	
	/**
	 * Tests retrieving the storage folder when the FS is mounted.
	 */
	@Test
	public void getMountedStorageFolderTest(final Activity a) {
		JmeAndroidSystem.setActivity(a);
		new NonStrictExpectations() {
			Environment e;
			Context c;
			{
				a.getApplicationContext();result=c;
				c.getExternalFilesDir(null);result=new File("");
				Environment.getExternalStorageState();result=Environment.MEDIA_MOUNTED;
			}
		};
		Assert.assertNotNull("didnt get a storagedir", delegate.getStorageFolder());
	}
	
	/**
	 * Tests retrieving the storage folder when the FS is not mounted.
	 */
	@Test
	public void getUnmountedStorageFolderTest() {
		new NonStrictExpectations() {
			Environment e;
			{
				Environment.getExternalStorageState();result=Environment.MEDIA_UNMOUNTED;
			}
		};
		Assert.assertNull("somehow got a storagedir, should have been null", delegate.getStorageFolder());
	}
}
