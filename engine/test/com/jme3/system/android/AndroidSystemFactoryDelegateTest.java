package com.jme3.system.android;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jme3.asset.AndroidAssetManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.android.AndroidAudioRenderer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

public class AndroidSystemFactoryDelegateTest {
	private AndroidSystemFactoryDelegate delegate = new AndroidSystemFactoryDelegate();
	
	@Before
	public void setup(){
		delegate = new AndroidSystemFactoryDelegate();
	}
	
	/**
	 * Tests initializing a new {@link JmeContext} instance
	 */
	@Test
	public void newContextTest() {
		String myrenderer = "derprenderer";
		AppSettings settings = new AppSettings(false);
		settings.setAudioRenderer(myrenderer);
		JmeContext ctx = delegate.newContext(settings, null);
		Assert.assertEquals("didnt get a OGLES context", OGLESContext.class, ctx.getClass());
		Assert.assertEquals("setttings got scrambled", myrenderer, ctx.getSettings().getAudioRenderer());
	}
	
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
	
	/**
	 * Tests instantiating a new AudioRenderer instance
	 */
	@Test
	public void newAudioRendererTest(AndroidAudioRenderer expected) {
		// The null doesn't matter as the implementation doesn't use the parameter.
		Assert.assertEquals("newAudioRenderer() did not create an AndroidAudioRenderer instance.", 
				expected.getClass(), delegate.newAudioRenderer(null).getClass()); 
	}
}
