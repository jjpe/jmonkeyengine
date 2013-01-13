package com.jme3.system;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jme3.audio.AudioRenderer;
import com.jme3.audio.lwjgl.LwjglAudioRenderer;

/**
 * A test of the code of {@link JmeDesktopSystem}.
 * Note that this does not test the wanted behavior (as that is unknown to me),
 * just the implemented behavior.
 * 
 * @author Volker Lanting
 *
 */
public class JmeDesktopSystemTest {

	private static final String LWJGL_AUDIO_PREFIX = "LWJGL";
	private static final String UNSUPPORTED_AUDIO_PREFIX = "DerpSounds";
	
	private JmeDesktopSystem delegate;
	
	private AppSettings emptySettings;
	private AppSettings lwjglAudioSettings;
	
	@Before
	public void setup() {
		delegate = new JmeDesktopSystem();
		emptySettings = new AppSettings(false);
		lwjglAudioSettings = new AppSettings(false);
		lwjglAudioSettings.setAudioRenderer(LWJGL_AUDIO_PREFIX);
	}
	
	/**
	 * Tests if the initialize method tries to extract native libraries
	 * with the correct AppSettings.
	 */
	@Test
	public void testInitialize() {
		new MockUp<Natives>(){
			@SuppressWarnings("unused")
			@Mock(invocations = 1)
			void extractNativeLibs(Platform p, AppSettings s){
				Assert.assertEquals("extracting native lib with different settings than those given to initialize",
						emptySettings, s);
			};
		};
		delegate.lowPermissions = false;
		delegate.initialize(emptySettings);
	}
	
	/**
	 * Tests if the initialize method continues without interruption,
	 * even if an IOException is thrown by {@link Natives#extractNativeLibs(Platform, AppSettings)}.
	 */
	@Test
	public void testInitializeWithIOException() {
		new MockUp<Natives>(){
			@SuppressWarnings("unused")
			@Mock(invocations = 1)
			void extractNativeLibs(Platform p, AppSettings s) throws IOException {
				throw new IOException("this is not allowed");
			};
		};
		delegate.lowPermissions = false;
		delegate.initialize(emptySettings);
	}
	
	/**
	 * Tests if the initialize method skips loading of native libraries
	 * if low permissions is set.
	 */
	@Test
	public void testInitializeWithLowPermissions() {
		new MockUp<Natives>(){
			@SuppressWarnings("unused")
			@Mock
			void extractNativeLibs(Platform p, AppSettings s){
				Assert.fail("With low permissions you should not be able to extract native libs");
			};
		};
		delegate.lowPermissions = true;
		delegate.initialize(emptySettings);
	}

	/**
	 * Tests if the initialize method skips loading of native libraries
	 * if it has already been called before.
	 */
	@Test
	public void testInitializeTwice() {
		new NoNativeExtraction();
		delegate.lowPermissions = false;
		delegate.initialize(emptySettings);
		delegate.initialize(emptySettings);
	}
	
	/**
	 * Test newAudioRenderer for LWGL renderer to
	 * check if the Delegate gets initialized.
	 */
	@Test
	public void testNewAudioRendererUninitialized() {
		new MockUp<JmeDesktopSystem>() {
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void initialize(AppSettings set) {}
		};
		delegate.newAudioRenderer(lwjglAudioSettings);
	}
	
	// FIXME: we can't test with Joal audiorendererer AppSettings,
	// as we don't have the JoalAudioRenderer.

	/**
	 * Test newAudioRenderer for LWJGL renderer to
	 * check if correct AudioRenderer is returned.
	 */
	@Test
	public void testNewAudioRendererLWJGL() {
		delegate.initialized = true;
		AudioRenderer renderer = delegate.newAudioRenderer(lwjglAudioSettings);
		Assert.assertEquals("obtained a wrong audioRenderer", 
				LwjglAudioRenderer.class, renderer.getClass());
	}
	
	/**
	 * Test newAudioRenderer for unsupported renderer.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testNewAudioRendererUnsupported() {
		delegate.initialized = true;
		AppSettings s = new AppSettings(false);
		s.setAudioRenderer(UNSUPPORTED_AUDIO_PREFIX);
		delegate.newAudioRenderer(s);
	}
	
	/**
	 * Test the behavior of newAudioRenderer (silent handling and returning null)
	 * when the requested class is not available on the classpath.
	 */
	@Test
	public void testNewAudioRendererNoRendererAvailable() throws Throwable {
		final String expectedErrorMessage = "CRITICAL ERROR: Audio implementation class is missing!\n"
				+ "Make sure jme3_lwjgl-oal or jm3_joal is on the classpath.";
		new NoNativeExtraction();
		new MockUp<Logger>() {
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void log(Level l, String message, Throwable cause) {
				Assert.assertEquals("The expected log message did not occur", 
						expectedErrorMessage, message);
			}
		};
		Class<?> customDelegate = new CustomTestClassLoader(LwjglAudioRenderer.class.getName())
		.customLoadClass(JmeDesktopSystem.class);
		Object renderer = customDelegate.getMethod("newAudioRenderer", AppSettings.class)
		.invoke(customDelegate.newInstance(), lwjglAudioSettings);
		Assert.assertNull("Somehow it magically got a renderer", renderer);
	}
}

/**
 * No need to clutter anything by loading natives if it is not required.
 * Instantiating this mockup will mock the Natives class and assert that
 * the extractNativeLibs(Platform, AppSettings) method is called once.
 * It will not execute the method though.
 * 
 * @author Volker Lanting
 *
 */
class NoNativeExtraction extends MockUp<Natives> {
	@Mock(invocations=1)
	void extractNativeLibs(Platform p, AppSettings s){};
}