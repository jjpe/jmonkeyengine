package com.jme3.system;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.junit.Before;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.system.JmeContext.Type;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class JmeSystemDelegateTest {

	private SystemDelegateTester delegate;
	
	@Before
	public void setupTester() {
		delegate = new SystemDelegateTester();
	}
	
	// test storage folder
	//  lowPermissions
	//  ! lowPermissions
	//  storageFolder still not set
	//  storageFolder already set
	
//	public void testStorageFolderWithLowPermissions {
//		delegate.lowPermissions = true;
//	}
}

class SystemDelegateTester extends JmeSystemDelegate {
	@Override
	public void writeImageFile(OutputStream outStream, String format, ByteBuffer imageData, int width, int height) throws IOException {}
	@Override
	public AssetManager newAssetManager(URL configFile) {return null;}
	@Override
	public AssetManager newAssetManager() {return null;}
	@Override
	public void showErrorDialog(String message) {}
	@Override
	public boolean showSettingsDialog(AppSettings sourceSettings, boolean loadFromRegistry) {return false;}
	@Override
	public JmeContext newContext(AppSettings settings, Type contextType) {return null;}
	@Override
	public AudioRenderer newAudioRenderer(AppSettings settings) {return null;}
	@Override
	public void initialize(AppSettings settings) {}
	@Override
	public ImageRaster createImageRaster(Image image, int slice) {return null;}
}
