package com.jme3.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import junit.framework.Assert;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Before;
import org.junit.Test;

import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class AbstractSystemIODelegateTest {
	private static final String FILE_LOCATION = JmeSystem.class.getResource(".").getPath();
	private static final String STORAGE_FOLDER = ".jme3";

	private MyTestAbstractSystemIODelegate delegate;
	
	@Before
	public void setup() {
		delegate = new MyTestAbstractSystemIODelegate();
	}
	
	/**
	 * Tests if setLowPermissions can toggle the lowPermissions boolean.
	 */
	@Test
	public void testSetLowPermissions() {
		delegate.lowPermissions = false;
		delegate.setLowPermissions(true);
		Assert.assertTrue("setLowPermissions does not set the permissions to true if asked to", 
				delegate.lowPermissions);
		delegate.setLowPermissions(false);
		Assert.assertFalse("setLowPermissions does not set the permissions to false if asked to", 
				delegate.lowPermissions);
	}
	
	/**
	 * Tests if isLowPermissions can retrieve the lowPermissions boolean.
	 */
	@Test
	public void testIsLowPermissions() {
		delegate.lowPermissions = true;
		Assert.assertTrue("isLowPermissions does not read the permissions if they are true", 
				delegate.isLowPermissions());
		delegate.setLowPermissions(false);
		Assert.assertFalse("isLowPermissions does not read the permissions if they are false", 
				delegate.isLowPermissions());
	}
	
	/**
	 * Tests if getResourceAsStream delegates to its ClassLoader.
	 */
	@Test
	public void testGetResourceAsStream() {
		final String resourceName = "some_resource";
		new MockUp<Class<JmeSystemDelegate>>() {
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			InputStream getResourceAsStream(String name) {
				Assert.assertEquals("The resourcename was not passed unaltered to the classloader", 
						resourceName, name);
				return null;
			}
		};
		Assert.assertNull("the result from the classloader was not returned",
				delegate.getResourceAsStream(resourceName));
	}
	
	/**
	 * Tests if getResource delegates to its ClassLoader.
	 */
	@Test
	public void testGetResource() {
		final String resourceName = "some_resource";
		new MockUp<Class<JmeSystemDelegate>>() {
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			URL getResource(String name) {
				Assert.assertEquals("The resourcename was not passed unaltered to the classloader", 
						resourceName, name);
				return null;
			}
		};
		Assert.assertNull("the result from the classloader was not returned",
				delegate.getResource(resourceName));
	}
	
	/**
	 * Tests getStorageFolder when the delegate has low permissions.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testStorageFolderWithLowPermissions() {
		delegate.lowPermissions = true;
		delegate.getStorageFolder();
	}
	
	/**
	 * Tests getStorageFolder for 'high' permissions and an
	 * already set storageFolder.
	 */
	@Test
	public void testStorageFolderWithPresetFolder() {
		File file = new File("doesnt exist");
		delegate.storageFolder = file;
		delegate.lowPermissions = false;
		Assert.assertEquals("getStorageFolder did not return the same folder as was set.", 
				file, delegate.getStorageFolder());
	}
	
	/**
	 * Tests getStorageFolder with 'high' permissions and
	 * the default location for the storage folder 
	 * (which might exist or not, depending on your system).
	 */
	@Test
	public void testStorageFolderWithExistingDefault() {
		delegate.lowPermissions = false;
		delegate.storageFolder = null;
		File file = delegate.getStorageFolder();
		Assert.assertTrue("The returned storageFolder does not exist", file.exists());
		Assert.assertEquals("The storage folder unexpectedly changed", 
				file, delegate.getStorageFolder());
	}
	
	
	/**
	 * Tests getStorageFolder with 'high' permissions and
	 * the default location for the storage folder
	 * set to {@value #STORAGE_FOLDER} in {@value #FILE_LOCATION}.
	 * This {@value #STORAGE_FOLDER} folder will be deleted to make sure the 
	 * method is tested on a non existent folder.
	 */
	@Test
	public void testStorageFolderWithNonExistentDefault() {
		new MockUp<System>() {
			@SuppressWarnings("unused")	
			@Mock(invocations=1)
			String getProperty(String name) {
				return FILE_LOCATION;
			}
		};
		File dir = new File(FILE_LOCATION,STORAGE_FOLDER);
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

class MyTestAbstractSystemIODelegate extends AbstractSystemIODelegate {
	@Override
	public ImageRaster createImageRaster(Image image, int slice) {return null;}
	@Override
	public void writeImageFile(OutputStream out, String format,
			ByteBuffer imageData, int width, int height) throws IOException {}
}
