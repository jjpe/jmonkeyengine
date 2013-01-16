package com.jme3.system.android;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.graphics.Bitmap;

import com.jme3.asset.AndroidAssetManager;
import com.jme3.asset.AndroidImageInfo;
import com.jme3.asset.AssetManager;
import com.jme3.audio.android.AndroidAudioRenderer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.Platform;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.DefaultImageRaster;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.AndroidLogHandler;
import com.jme3.util.BufferUtils;

public class JmeAndroidSystemTest {
	
//	private static final String STORAGE_FOLDER = ".jme3"; // TODO remove
	private JmeAndroidSystem delegate;
	private AppSettings emptySettings;
	
	@Before
	public void setupTester() {
		delegate = new JmeAndroidSystem();
	}
	
	
	// Testing writeImageFile
	
	/**
	 * Tests writing a PNG image file
	 * @throws IOException
	 */
	@Test
	public void writePngImageFileTest(/*final OutputStream os*/) throws IOException {
		// l. 44 - format.equals("png")

		final String format = "png";
		final int width = 2;
		final int height = 2;
		final ByteBuffer buf = BufferUtils.createByteBuffer(width*height*4);
		buf.putInt(0, 0xFFF00F00);
		buf.putInt(3, 0xAA996655);
		
		
		final List<Integer> output = new ArrayList<Integer>();
		OutputStream os = new OutputStream() {
			@Override
			public void write(int bite) throws IOException {
				output.add(bite); 
			}
		};
		
		new BitmapMockup(); // mock Bitmap.createBitmap(int, int, Bitmap.Config)
		
		delegate.writeImageFile(os, format, buf, width, height);
	}
	
	/**
	 * Tests writing a JPG image file
	 * @throws IOException
	 */
	@Test
	public void writeJpgImageFileTest() throws IOException {

		final String format = "png";
		final int width = 2;
		final int height = 2;
		final ByteBuffer buf = BufferUtils.createByteBuffer(width*height*4);
		buf.putInt(0, 0xFFF00F00);
		buf.putInt(3, 0xAA996655);
		
		
		final List<Integer> output = new ArrayList<Integer>();
		OutputStream os = new OutputStream() {
			@Override
			public void write(int bite) throws IOException {
				output.add(bite); 
			}
		};
		
		new BitmapMockup(); // mock Bitmap.createBitmap(int, int, Bitmap.Config)
		
		delegate.writeImageFile(os, format, buf, width, height);
	}
	
	/**
	 * Tests writing an image with an unsupported format
	 * @throws IOException
	 */
	@Test
	public void writeUnsupportedImageFileTest() throws IOException {
		// l. 49 - UnsupportedOperationException
		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	
	// Testing createImageRaster
	
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
	
	
	// Testing newAssetManager(URL configFile)
	
	/**
	 * Tests creating a new {@link AssetManager} with a specified configFile
	 * @throws MalformedURLException 
	 */
	@Test
	public void newAssetManagerWithParamTest() throws MalformedURLException {
		// l. 66-67

		final URL config = new URL("file://test-data/System");
		new CheckAndroidAssetManagerConstructor(config);
		Assert.assertEquals("newAssetManager(URL) didnt create a DesktopAssetManager",
				AndroidAssetManager.class, delegate.newAssetManager(config).getClass());
	}
	
	
	// Testing newAssetManager()
	
	/**
	 * Tests creating a new {@link AssetManager} with a default configFile
	 */
	@Test
	public void newAssetManagerTest() {
		// l. 72-73
		
		new CheckAndroidAssetManagerConstructor(null);
		Assert.assertEquals("newAssetManager didnt create a DesktopAssetManager",
				AndroidAssetManager.class, delegate.newAssetManager().getClass());
	}
	
	
	// TODO showErrorDialog is untested
	/**
	 * 
	 */
	@Test
	public void showErrorDialogTest() {
		final String msg = "Hello, I'm testing errors!";
		// make the error message 'appear' immediately, or the test might end before it does.
		new ForceInvoke();
		new Expectations() {
			JOptionPane mock;
			{
				JOptionPane.showMessageDialog(null, msg, (String) any, JOptionPane.ERROR_MESSAGE);
				times=1;
			}
		};
		
		delegate.showErrorDialog(msg);
	}
	
	// 
	
	/**
	 * Tests showSettingsDialog, which returns a flag.
	 */
	@Test
	public void showSettingsDialogTest() {
		// l.

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	
	// Testing newContext(AppSettings, Type)
	
	/**
	 * Tests initializing a new {@link JmeContext} instance
	 */
	@Test
	public void newContextTest() {
		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	
	// Testing newAudioRenderer(AppSettings)
	
	/**
	 * Tests instantiating a new AudioRenderer instance
	 */
	@Test
	public void newAudioRendererTest(AndroidAudioRenderer expected) {
		// The null doesn't matter as the implementation doesn't use the parameter.
		Assert.assertEquals("newAudioRenderer() did not create an AndroidAudioRenderer instance.", 
				expected.getClass(), delegate.newAudioRenderer(null).getClass()); 
	}
	
	
	// Testing initialize(AppSettings settings)
	
	/**
	 * Tests initializing a JmeAndroidSystem for the first time
	 */
	@Test
	public void initializeWithZeroHandlersTest() {
		new MockUp<Logger>() {
			@SuppressWarnings("unused")
			@Mock
			Handler[] getHandlers() {
				return new Handler[]{};
			}
		};
		
		
		
		delegate.initialize(emptySettings);
		
		
		
		
//		
//		new Expectations() {{
//			
//		}};
//		
//		new MockUp<Logger>() {
////			@SuppressWarnings("unused")
////			@Mock(invocations = 1)
////			void extractNativeLibs(Platform p, AppSettings s){
////				Assert.assertEquals("extracting native lib with different settings than those given to initialize",
////						emptySettings, s);
////			};
//
//			@SuppressWarnings("unused")
//			@Mock
//			Handler[] getHandlers() {
//				return new Handler[]{
//					new AndroidLogHandler(), 
//					new AndroidLogHandler()
//				};
//			}
//		};
		
//		delegate.lowPermissions = false;
//		delegate.initialize(emptySettings);
		
	}
	
	/**
	 * Tests 
	 */
//	@Test
//	public void initializeWithAtLeastOneHandlerTest() {
//		new MockUp<Logger>() {
//			@SuppressWarnings("unused")
//			@Mock
//			Handler[] getHandlers() {
//				return new Handler[]{
//					new AndroidLogHandler(), 
//					new AndroidLogHandler()
//				};
//			}
//		};
//		
//		delegate.initialize(emptySettings);
//	}
//	
	/**
	 * Tests initializing a JmeAndroidSystem if it's already been initialized
	 */
	@Test
	public void initializeTwiceTest() {
		delegate.initialize(emptySettings);
		delegate.initialize(emptySettings);
	}
	
	
	// TODO check with Volker on how to test the do-while
	
	
	// Tests getPlatform()
	
	/**
	 * Tests retrieving the {@link Platform#Android_ARM5} enum 
	 * value based on the {@link System} "os.arch" property.
	 */
	@Test
	public void getPlatformArmV5Test() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	/**
	 * Tests retrieving the {@link Platform#Android_ARM6} enum 
	 * value based on the {@link System} "os.arch" property.
	 */
	@Test
	public void getPlatformArmV6Test() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	/**
	 * Tests retrieving the {@link Platform#Android_ARM7} enum 
	 * value based on the {@link System} "os.arch" property.
	 */
	@Test
	public void getPlatformArmV7Test() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	/**
	 * Tests retrieving the {@link Platform} enum value based on the 
	 * {@link System} "os.arch" property when the arch is of unknown ARM type.
	 */
	@Test
	public void getPlatformUnknownArmTest() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	/**
	 * Tests retrieving the {@link Platform#Android_ARM7} enum value 
	 * if the arch is not recgnized as being of the ARM variety	
	 */
	@Test
	public void getPlatformUnsupportedAndroidPlatformTest() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	
	// 

	/**
	 * Tests retrieving the storage folder when the FS is mounted.
	 */
	@Test
	public void getMountedStorageFolderTest() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	/**
	 * Tests retrieving the storage folder when the FS is not mounted.
	 */
	@Test
	public void getUnmounedStorageFolderTest() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	
	//
	
	/**
	 * Tests 
	 */
	@Test
	public void setActivityTest() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
	
	// 
	
	/**
	 * Tests 
	 */
	@Test
	public void getActivityTest() {
		// l. 

		new MockUp<Object>() {
			@Mock
			public void m() {
				
			}
		};
		
		new Expectations() {{
			
		}};
		
	}
	
//	TODO remove
//	// 
//	
//	/**
//	 * Tests 
//	 */
//	@Test
//	public void cTest() {
//		// l. 
//
//		new MockUp<Object>() {
//			@Mock
//			public void m() {
//				
//			}
//		};
//		
//		new Expectations() {{
//			
//		}};
//		
//	}
//	
//	
//	// 
//	
//	/**
//	 * Tests 
//	 */
//	@Test
//	public void dTest() {
//		// l. 
//
//		new MockUp<Object>() {
//			@Mock
//			public void m() {
//				
//			}
//		};
//		
//		new Expectations() {{
//			
//		}};
//		
//	}
}

class CheckAndroidAssetManagerConstructor extends MockUp<AndroidAssetManager> {
	private final URL expected;
	public CheckAndroidAssetManagerConstructor(URL expected) {
		this.expected = expected;
	}
	@Mock(invocations=1)
	public void $init(URL u){
		Assert.assertEquals("The config was not passed properly to the new AssetManager", 
				expected, u);
	}
}

class ForceInvoke extends MockUp<Activity> {
	@Mock
	static void runOnUiThread(Runnable runnable) {
		runnable.run();
	}
}

class BitmapMockup extends MockUp<Bitmap> {
	static Bitmap createBitmap(int width, int height, Bitmap.Config config) {
		Assert.assertNotNull("Must not be null", config);
		
		return null;
	}
}