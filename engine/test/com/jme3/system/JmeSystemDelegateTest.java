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
import mockit.Mocked;

import org.junit.Before;
import org.junit.Test;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.SoftTextDialogInput;
import com.jme3.system.JmeContext.Type;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

/**
 * Test of the code of the {@link JmeSystemDelegate}.
 * Note that it does not test the wanted behavior (as that is unknown to me),
 * just the implemented behavior.
 * 
 * 
 * @author Volker Lanting
 *
 */
public class JmeSystemDelegateTest {

	private static final String FILE_LOCATION = JmeSystem.class.getResource(".").getPath();
	private static final String STORAGE_FOLDER = ".jme3";
	private SystemDelegateTester delegate;
	
	@Before
	public void setupTester() {
		delegate = new SystemDelegateTester();
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
	
	/**
	 * Tests if getFullName returns {@link JmeVersion#FULL_NAME}.
	 */
	@Test
	public void testGetFullName() {
		Assert.assertEquals("getFullName no longer refers to JmeVersion.FULL_NAME", 
				JmeVersion.FULL_NAME, delegate.getFullName());
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
	 * Tests if trackDirectMemory returns false.
	 */
	@Test
	public void testTrackDirectMemory() {
		Assert.assertFalse("trackDirectMemory no longer defaults to false", 
				delegate.trackDirectMemory());
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
	
	@Mocked 
	private SoftTextDialogInput softTextDialogInput;
	
	/**
	 * Tests if setSoftTextDialogInput sets the softDialogInput field.
	 */
	@Test
	public void testSetSoftTextDialogInput() {
		delegate.softTextDialogInput = null;
		delegate.setSoftTextDialogInput(softTextDialogInput);
		Assert.assertEquals("the softTextDialogInput was not properly set",
				softTextDialogInput, delegate.softTextDialogInput);
	}
	
	/**
	 * Tests if getSoftTextDialogInput retrieves the softDialogInput field.
	 */
	@Test
	public void testGetSoftTextDialogInput() {
		delegate.softTextDialogInput = softTextDialogInput;
		Assert.assertEquals("the SoftTextDialogInput was not retrieved properly", 
				softTextDialogInput, delegate.getSoftTextDialogInput());
	}
	
	/**
	 * Tests the getPlatform method for several (fictive) machine setups 
	 */
	@Test
	public void testGetPlatform() {
		MockOSMachine.LINUX_I386.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Linux i386",
				Platform.Linux32, delegate.getPlatform());
		MockOSMachine.FREEBSD_AMD64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: FreeBsd amd64",
				Platform.Linux64, delegate.getPlatform());
		MockOSMachine.SUNOS_I686.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: SunOs i686",
				Platform.Linux32, delegate.getPlatform());
		
		MockOSMachine.MACOSX_UNIVERSAL.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac universal",
				Platform.MacOSX32, delegate.getPlatform());
		MockOSMachine.MACOSX_AMD64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac amd64",
				Platform.MacOSX64, delegate.getPlatform());
		MockOSMachine.MACOSX_PPC.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac ppc",
				Platform.MacOSX_PPC32, delegate.getPlatform());
		MockOSMachine.MACOSX_PPC64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac ppc64",
				Platform.MacOSX_PPC64, delegate.getPlatform());
		
		MockOSMachine.WINDOWS_X86.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Windows x86",
				Platform.Windows32, delegate.getPlatform());
		MockOSMachine.WINDOWS_X86_64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Windows x86_64",
				Platform.Windows64, delegate.getPlatform());
	}
	
	/**
	 * Tests a case which should probably work, 
	 * but fails due to an equals check with uppercase letters
	 * on a string which is always lowercased.
	 * See the private method JmeSystemDelegate.is64Bit at "PowerPC",
	 * which is called on System.getProperty("os.arch").toLowerCase
	 * in {@link JmeSystemDelegate#getPlatform()}. 
	 */
	@Test
	public void behaviourTestGetPlatform() {
		MockOSMachine.WINDOWS_POWERPC.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Windows PowerPC",
				Platform.Windows32, delegate.getPlatform());
	}
	
	/**
	 * Tests getPlatform with a non supported (or existent) operating system.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testGetPlatformWithUnsupportedOS() {
		MockOSMachine.NON_EXISTENT_OS.mock();
		delegate.getPlatform();
	}
	
	/**
	 * Tests getPlatform with a non supported (or existent) architecture.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testGetPlatformWithUnsupportedArchitecture() {
		MockOSMachine.NON_EXISTENT_ARCHITECTURE.mock();
		delegate.getPlatform();
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
