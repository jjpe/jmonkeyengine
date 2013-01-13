package com.jme3.system;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jme3.audio.AudioRenderer;
import com.jme3.audio.lwjgl.LwjglAudioRenderer;
import com.jme3.system.JmeContext.Type;
import com.jme3.system.lwjgl.LwjglCanvas;
import com.jme3.system.lwjgl.LwjglDisplay;
import com.jme3.system.lwjgl.LwjglOffscreenBuffer;

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
	private static final String LWJGL_RENDERER_PREFIX = "LWJGL";
	private static final String JOGL_RENDERER_PREFIX = "JOGL";
	private static final String CUSTOM_RENDERER_PREFIX = "CUSTOM";
	private static final String UNSUPPORTED_AUDIO_PREFIX = "DerpSounds";
	private static final String UNSUPPORTED_RENDERER_PREFIX = "DerpReindeer";
	private static final String NULL_RENDERER = "NULL";
	
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
		new FakeLogger3(Level.SEVERE, expectedErrorMessage);
		Class<?> customDelegate = new CustomTestClassLoader(LwjglAudioRenderer.class.getName())
		.customLoadClass(JmeDesktopSystem.class);
		Object renderer = customDelegate.getMethod("newAudioRenderer", AppSettings.class)
		.invoke(customDelegate.newInstance(), lwjglAudioSettings);
		Assert.assertNull("Somehow it magically got a renderer", renderer);
	}
	
	/**
	 * Tests newContext with an unsupported renderer.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testNewContextUnsupported() {
		newContext(UNSUPPORTED_RENDERER_PREFIX, null);
	}
	
	/**
	 * Tests newContext with an unsupported renderer and the Headless type.
	 * Resulting in a NullContext.
	 */
	@Test
	public void testNewContextWithHeadlessType() {
		JmeContext context = newContext(UNSUPPORTED_RENDERER_PREFIX, Type.Headless);
		Assert.assertEquals("The renderer didnt get transferred to the context",
				UNSUPPORTED_RENDERER_PREFIX, context.getSettings().getRenderer());
		Assert.assertEquals("The context was not a NullContext",
				NullContext.class, context.getClass());
	}
	
	/**
	 * Tests newContext with a NullRenderer and headless type,
	 * resulting in a NullContext.
	 */
	@Test
	public void testNewContextWithNullRenderer() {
		JmeContext context = newContext(NULL_RENDERER, Type.Headless);
		Assert.assertEquals("The renderer didnt get transferred to the context",
				NULL_RENDERER, context.getSettings().getRenderer());
		Assert.assertEquals("The context was not a NullContext",
				NullContext.class, context.getClass());
	}
	
	/**
	 * Tests newContext with null as renderer and the Headless Type,
	 * resulting in a NullContext.
	 */
	@Test
	public void testNewContextWithoutRenderer() {
		JmeContext context = newContext(null, Type.Headless);
		Assert.assertNull("The AppSettings didnt get transfered to the context",
				context.getSettings().getRenderer());
		Assert.assertEquals("The context was not a NullContext",
				NullContext.class, context.getClass());
	}
	
	/**
	 * Tests newContext with an LWJGL renderer and the canvas type.
	 */
	@Test
	public void testNewContextWithLwjglRendererAndCanvasType() {
		expectContext(LWJGL_RENDERER_PREFIX, Type.Canvas, LwjglCanvas.class);
	}
	
	/**
	 * Tests newContext with an LWJGL renderer and the display type.
	 */
	@Test
	public void testNewContextWithLwjglRendererAndDisplayType() {
		expectContext(LWJGL_RENDERER_PREFIX, Type.Display, LwjglDisplay.class);
	}
	
	/**
	 * Tests newContext with an LWJGL renderer and the offscreenSurface type.
	 */
	@Test
	public void testNewContextWithLwjglRendererAndOffscreenSurfaceType() {
		expectContext(LWJGL_RENDERER_PREFIX, Type.OffscreenSurface, LwjglOffscreenBuffer.class);
	}
	
	/**
	 * Tests the error handling when the requested context is not on the classpath.
	 */
	@Test
	public void testNewContextWithLwjglRendererAndWrongPathToCanvas() throws Throwable {
		newContextWithCustomClassLoader(Level.SEVERE, "CRITICAL ERROR: Context class is missing!\n"
                + "Make sure jme3_lwjgl-ogl is on the classpath.", 
                LWJGL_RENDERER_PREFIX, Type.Canvas, LwjglCanvas.class);
	}
	
	// FIXME: we have no access to the Jogl Context classes, 
	// so they should still be tested.
	
	/**
	 * Tests newContext with a custom renderer resolving to the {@link LwjglCanvas} context.
	 * Apparently in this case the renderer in the AppSetting should actually resolve to a context
	 * instead of a renderer.
	 * Also, as long as the type is not Headless it is disregarded, so that cuts down on testcases.
	 */
	@Test
	public void testNewContextWithCustomRenderer() {
		// type doesnt matter (unless its headless) for this case
		expectContext(CUSTOM_RENDERER_PREFIX+LwjglCanvas.class.getName(), Type.OffscreenSurface, LwjglCanvas.class);
	}
	
	/**
	 * Tests newContext with a custom renderer resolving to a non-existent class.
	 * It expects the proper error log and the NPE thrown because of ctx.setSettings.
	 */
	@Test(expected=NullPointerException.class)
	public void testNewContextWithCustomRendererAndWrongPath() {
		new FakeLogger3(Level.SEVERE, "CRITICAL ERROR: Context class is missing!");
		newContext(CUSTOM_RENDERER_PREFIX+UNSUPPORTED_RENDERER_PREFIX, Type.OffscreenSurface);
	}
	
	
	// uses the CustomTestClassLoader to cause a ClassNotFoundException
	// assumes error is logged at level l using Logger.log(Level,String,Throwable)
	// calls the newContext with the given renderer in the AppSettings and the given Type
	// the result should be a nullPointerException thrown by JmeDesktopSystem.newContext at cxt.setSettings
	// uses the given expected class to be able to cause the ClassNotFoundException
	private void newContextWithCustomClassLoader(Level l, String error, 
			String renderer, Type type, Class<? extends JmeContext> expected) throws Throwable {
		new FakeLogger3(l, error);
		new NoNativeExtraction();
		
		AppSettings set = new AppSettings(false);
		set.setRenderer(renderer);
		
		Class<?> customDelegate = new CustomTestClassLoader(expected.getName())
		.customLoadClass(JmeDesktopSystem.class);
		
		try {
			customDelegate.getMethod("newContext", AppSettings.class, Type.class)
			.invoke(customDelegate.newInstance(), set, type);
		} catch(InvocationTargetException e) {
			Assert.assertEquals("Threw some other error than the NPE expected to come from cxt.setSettings",
					NullPointerException.class, e.getCause().getClass());
			return;
		}
		Assert.fail("No NullPointer happened, somehow it got a context...");
	}
	
	
	// calls newContext and compares with the given expected context class
	private void expectContext(String renderer, Type type, Class<? extends JmeContext> expected) {
		Assert.assertEquals("The context was not of the requested type/class",
				expected, newContext(renderer, type).getClass()
				);
	}
	
	// calls newContext on the delegate with the given renderer in the appsettings
	// and the given type.
	private JmeContext newContext(String renderer, Type type) {
		delegate.initialized = true;
		AppSettings set = new AppSettings(false);
		set.setRenderer(renderer);
		return delegate.newContext(set, type);
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

/**
 * A mock to check if Logger.log(Level,String,Throwable)
 * is invoked exactly once, and with the given level and message.
 * 
 * @author Volker Lanting
 *
 */
class FakeLogger3 extends MockUp<Logger> {
	private final String expected;
	private final Level level;
	public FakeLogger3(Level level, String expectedMessage) {
		this.level=level;
		expected=expectedMessage;
	}
	@Mock(invocations=1)
	void log(Level l, String message, Throwable cause) {
		Assert.assertEquals("The expected log message did not occur", 
				expected, message);
		Assert.assertEquals("The expected log level was not found", 
				level, l);
	}
}