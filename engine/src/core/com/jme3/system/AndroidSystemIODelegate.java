package com.jme3.system;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class AndroidSystemIODelegate extends AbstractSystemIODelegate {

	@Override
	public ImageRaster createImageRaster(Image img, int slice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLowPermissions(boolean newVal) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLowPermissions() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getResource(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getStorageFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeImageFile(	OutputStream out, 
								String format,
								ByteBuffer imageData, 
								int width, 
								int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(AppSettings settings) {
		// TODO Auto-generated method stub

	}

}
