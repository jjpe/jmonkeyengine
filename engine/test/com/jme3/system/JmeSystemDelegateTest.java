package com.jme3.system;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import junit.framework.Assert;
import mockit.Mock;
import mockit.MockUp;

import org.junit.Before;
import org.junit.Test;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.system.JmeContext.Type;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class JmeSystemDelegateTest {

	private static final String FILE_LOCATION = "test-data/System";
	private SystemDelegateTester delegate;
	
	@Before
	public void setupTester() {
		delegate = new SystemDelegateTester();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testStorageFolderWithLowPermissions() {
		delegate.lowPermissions = true;
		delegate.getStorageFolder();
	}
	
	@Test
	public void testStorageFolderWithPresetFolder() {
		File file = new File("doesnt exist");
		delegate.storageFolder = file;
		delegate.lowPermissions = false;
		Assert.assertEquals("getStorageFolder did not return the same folder as was set.", 
				file, delegate.getStorageFolder());
	}
	
	@Test
	public void testStorageFolderWithExistingDefault() {
		delegate.lowPermissions = false;
		delegate.storageFolder = null;
		File file = delegate.getStorageFolder();
		Assert.assertTrue("The returned storageFolder does not exist", file.exists());
	}
	
	@Test
	public void testStorageFolderWithNonExistentDefault() {
		new MockUp<System>() {
			@SuppressWarnings("unused")	
			@Mock
			String getProperty(String name) {
				return FILE_LOCATION;
			}
		};
		File dir = new File(FILE_LOCATION,".jme3");
		dir.delete();
		delegate.lowPermissions = false;
		delegate.storageFolder = null;
		File file = delegate.getStorageFolder();
		Assert.assertTrue("The returned storageFolder does not exist", file.exists());
		dir.delete();
		Assert.assertEquals("The default storage folder has apparently changed location", 
				dir.getPath(), file.getPath());
	}
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
