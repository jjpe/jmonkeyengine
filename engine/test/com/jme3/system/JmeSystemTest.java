package com.jme3.system;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import mockit.Mock;
import mockit.MockUp;
import mockit.NonStrictExpectations;

import org.junit.Assert;
import org.junit.Test;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.SoftTextDialogInput;
import com.jme3.system.android.AndroidSystemDialogDelegate;
import com.jme3.system.android.AndroidSystemFactoryDelegate;
import com.jme3.system.android.AndroidSystemIODelegate;
import com.jme3.system.android.AndroidSystemStateDelegate;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.JmeFormatter;

public class JmeSystemTest {
	
	private CustomTestClassLoader androidLoader = new CustomTestClassLoader(
			"com.jme3.system.DesktopSystemDialogDelegate",
			"com.jme3.system.DesktopSystemIODelegate",
			"com.jme3.system.DesktopSystemFactoryDelegate",
			"com.jme3.system.DesktopSystemStateDelegate");

	private CustomTestClassLoader desktopLoader = new CustomTestClassLoader(
			"com.jme3.system.android.AndroidSystemDialogDelegate",
			"com.jme3.system.android.AndroidSystemIODelegate",
			"com.jme3.system.android.AndroidSystemFactoryDelegate",
			"com.jme3.system.android.AndroidSystemStateDelegate");
	
	private CustomTestClassLoader nonLoader = new CustomTestClassLoader(
			"com.jme3.system.DesktopSystemDialogDelegate",
			"com.jme3.system.DesktopSystemIODelegate",
			"com.jme3.system.DesktopSystemFactoryDelegate",
			"com.jme3.system.android.AndroidSystemDialogDelegate",
			"com.jme3.system.android.AndroidSystemIODelegate",
			"com.jme3.system.android.AndroidSystemFactoryDelegate",
			"com.jme3.system.DesktopSystemStateDelegate",
			"com.jme3.system.android.AndroidSystemStateDelegate");
	
	
	/**
	 * Checks if a JmeSystem with no delegate fails on its static, parameterless methods.
	 */
	@Test
	public void testNoDelegateFailures() throws Throwable {
		new MockUp<Logger>() {
			@SuppressWarnings("unused")
			@Mock(minInvocations=1)
			public void log(Level level, String message) {
				Assert.assertEquals("The jmeSystem should have logged the absence of a delegate", 
						"Failed to find a JmeSystem delegate!\n"
                                + "Ensure either desktop or android jME3 jar is in the classpath.",
                        message
						);
				Assert.assertEquals("The JmeSystem should have logged the absence of a delegate with SEVERE level",
						Level.SEVERE, level);
				
			}
		};
		Thread.currentThread().setContextClassLoader(nonLoader);
		Class<?> system = nonLoader.customLoadClass(JmeSystem.class);
		for (Method method : system.getMethods()) {
			if (method.getParameterTypes().length == 0 
					&& Modifier.isStatic(method.getModifiers())) {
				try {
					method.invoke(null);
				} catch (InvocationTargetException e) {
					Assert.assertEquals(
							"A system without a delegate should throw an NPE, but it didnt for the method "+method.getName(),
							NullPointerException.class, 
							e.getCause().getClass());
					continue;
				}
				Assert.fail("Method "+method.getName()+" did not throw an NPE.");
			}
		}
	}
	
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getPlatform is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetPlatform() throws Throwable {
		new MockUp<AndroidSystemStateDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			Platform getPlatform(){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"getPlatform");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getStorageFolder is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetStorageFolder() throws Throwable {
		new MockUp<AndroidSystemIODelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			File getStorageFolder(){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"getStorageFolder");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getFullName is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetFullName() throws Throwable {
		new MockUp<AndroidSystemStateDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			String getFullName(){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"getFullName");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getResourceAsStream is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetResourceAsStream() throws Throwable {
		new MockUp<AndroidSystemIODelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			InputStream getResourceAsStream(String a){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"getResourceAsStream", new Class<?>[]{String.class}, new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getResource is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetResource() throws Throwable {
		new MockUp<AndroidSystemIODelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			URL getResource(String a){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"getResource", new Class<?>[]{String.class}, new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and trackDirectMemory is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnTrackDirectmemory() throws Throwable {
		new MockUp<AndroidSystemStateDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			boolean trackDirectMemory(){return true;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"trackDirectMemory");
	}

	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and setLowPermissions is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnSetLowPermissions() throws Throwable {
		new MockUp<AndroidSystemIODelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void setLowPermissions(boolean a){}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"setLowPermissions",new Class<?>[]{boolean.class},new Object[]{true});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and isLowPermissions is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnIsLowPermissions() throws Throwable {
		new MockUp<AndroidSystemIODelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			boolean isLowPermissions(){return true;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"isLowPermissions");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and {@link JmeAndroidSystem#setSoftTextDialogInput(SoftTextDialogInput)} is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnSetSoftTextDialogInput() throws Throwable {
		new MockUp<AndroidSystemDialogDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void setSoftTextDialogInput(SoftTextDialogInput a){}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"setSoftTextDialogInput",new Class<?>[]{SoftTextDialogInput.class},new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and getSoftTextDialogInput is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetSoftTextDialogInput() throws Throwable {
		new MockUp<AndroidSystemDialogDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			SoftTextDialogInput getSoftTextDialogInput(){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"getSoftTextDialogInput");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and writeImageFile is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnWriteImageFile() throws Throwable {
		new MockUp<AndroidSystemIODelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void writeImageFile(OutputStream a,String b,ByteBuffer c,int d,int e){}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"writeImageFile",new Class[]{OutputStream.class,String.class,ByteBuffer.class,int.class,int.class},new Object[]{null,null,null,0,0});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and newAssetManager(URL) is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnNewAssetManagerUrl() throws Throwable {
		new MockUp<AndroidSystemFactoryDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			AssetManager newAssetManager(URL a){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"newAssetManager",new Class[]{URL.class},new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and newAssetManager is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnNewAssetManager() throws Throwable {
		new MockUp<AndroidSystemFactoryDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			AssetManager newAssetManager(){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"newAssetManager");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and showSettingsDialog is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnShowSettingsDialog() throws Throwable {
		new MockUp<AndroidSystemDialogDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			boolean showSettingsDialog(AppSettings a, boolean b){return true;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"showSettingsDialog",new Class[]{AppSettings.class,boolean.class},new Object[]{null,false});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and newContext is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnNewContext() throws Throwable {
		new MockUp<AndroidSystemFactoryDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			JmeContext newContext(AppSettings a, JmeContext.Type b){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"newContext",new Class[]{AppSettings.class,JmeContext.Type.class},new Object[]{null,JmeContext.Type.Canvas});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and newAudioRenderer is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnNewAudioRenderer() throws Throwable {
		new MockUp<AndroidSystemFactoryDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			AudioRenderer newAudioRenderer(AppSettings a){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"newAudioRenderer",new Class[]{AppSettings.class},new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and initialize is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnInitialize() throws Throwable {
		new MockUp<AndroidSystemFactoryDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void initialize(AppSettings a){}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"initialize",new Class[]{AppSettings.class},new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and createImageRaster is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnCreateImageRaster() throws Throwable {
		new MockUp<AndroidSystemIODelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			ImageRaster createImageRaster(Image a, int b){return null;}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"createImageRaster",new Class[]{Image.class,int.class},new Object[]{null,0});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and showErrorDialog is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnShowErrorDialog() throws Throwable {
		new MockUp<AndroidSystemDialogDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void showErrorDialog(String a){}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		Class<?> system = androidLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"showErrorDialog",new Class[]{String.class},new Object[]{null});
	}
	
	private void invokeStatic(
			Class<?> system, 
			String methodName ) throws Throwable {
		invokeStatic(system, methodName, new Class[0], new Object[0]);
	}
	private void invokeStatic(
			Class<?> system, 
			String methodName, 
			Class<?>[] paramClasses, 
			Object[] params) throws Throwable {
		system.getMethod(methodName, paramClasses).invoke(null, params);
	}
	
	
	/**
	 * Checks if the Desktop delegate is implicitly loaded if it is the only
	 * available delegate and getPlatform is called.
	 * 
	 * Note: we assume/hope that if this works for getPlatform it also works for the other methods
	 * that were tested for the android system.
	 * This saves a lot of test cases.
	 */
	@Test
	public void testImplictlyLoadDesktopOnGetPlatform() throws Throwable {
		new MockUp<DesktopSystemStateDelegate>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			Platform getPlatform(){return null;}
		};
		Thread.currentThread().setContextClassLoader(desktopLoader);
		Class<?> system = desktopLoader.customLoadClass(JmeSystem.class);
		invokeStatic(system,"getPlatform");
	}
	
	
	private void uninitializeSystem() {
		JmeSystem.setDelegate((ISystemDialogDelegate) null);
		JmeSystem.setDelegate((ISystemFactoryDelegate) null);
		JmeSystem.setDelegate((ISystemIODelegate) null);
		JmeSystem.setDelegate((ISystemStateDelegate) null);
	}
	
	/**
	 * Tests if the initialize method tries to extract native libraries
	 * with the correct AppSettings.
	 */
	@Test
	public void testDesktopInitialize() {
		uninitializeSystem();
		final AppSettings emptySettings = new AppSettings(false);
		new MockUp<Natives>(){
			@SuppressWarnings("unused")
			@Mock(invocations = 1)
			void extractNativeLibs(Platform p, AppSettings s){
				Assert.assertEquals("extracting native lib with different settings than those given to initialize",
						emptySettings, s);
			};
		};
		Thread.currentThread().setContextClassLoader(desktopLoader);
		JmeSystem.setLowPermissions(false);
		JmeSystem.initialize(emptySettings);
	}
	
	/**
	 * Tests if the initialize method continues without interruption,
	 * even if an IOException is thrown by {@link Natives#extractNativeLibs(Platform, AppSettings)}.
	 */
	@Test
	public void testDesktopInitializeWithIOException() {
		uninitializeSystem();
		new MockUp<Natives>(){
			@SuppressWarnings("unused")
			@Mock(invocations = 1)
			void extractNativeLibs(Platform p, AppSettings s) throws IOException {
				throw new IOException("this is not allowed");
			};
		};
		Thread.currentThread().setContextClassLoader(desktopLoader);
		JmeSystem.setLowPermissions(false);
		JmeSystem.initialize(new AppSettings(false));
	}
	
	/**
	 * Tests if the initialize method skips loading of native libraries
	 * if low permissions is set.
	 */
	@Test
	public void testInitializeWithLowPermissions() {
		uninitializeSystem();
		new MockUp<Natives>(){
			@SuppressWarnings("unused")
			@Mock
			void extractNativeLibs(Platform p, AppSettings s){
				Assert.fail("With low permissions you should not be able to extract native libs");
			};
		};
		Thread.currentThread().setContextClassLoader(desktopLoader);
		JmeSystem.setLowPermissions(true);
		JmeSystem.initialize(new AppSettings(false));
	}

	/**
	 * Tests if the initialize method skips loading of native libraries
	 * if it has already been called before.
	 */
	@Test
	public void testDesktopInitializeTwice() {
		uninitializeSystem();
		new NoNativeExtraction();
		Thread.currentThread().setContextClassLoader(desktopLoader);
		JmeSystem.setLowPermissions(false);
		JmeSystem.initialize(new AppSettings(false));
		JmeSystem.initialize(new AppSettings(false));
	}
	
	/**
	 * Tests initializing a JmeAndroidSystem for the first time
	 */
	@Test
	public void initializeAndroidWithZeroHandlersTest() {
		uninitializeSystem();
		new NonStrictExpectations() {
			Logger mock;
			Logger mock2;
			Handler h1;
			Handler h2;
			{
				Logger.getLogger(anyString);result=mock;
				mock.getParent();result=mock2;times=1;
				mock.getHandlers();result=new Handler[0];
				mock2.getHandlers();result=new Handler[]{h1,h2};
				h1.setFormatter((JmeFormatter) any); times=1;
				h1.setLevel(Level.ALL);times=1;
				h2.setFormatter((JmeFormatter) any); times=1;
				h2.setLevel(Level.ALL);times=1;
			}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		JmeSystem.initialize(new AppSettings(false));
	}
	

	/**
	 * Tests initializing a JmeAndroidSystem if it's already been initialized
	 */
	@Test
	public void initializeTwiceTest() {
		uninitializeSystem();
		new NonStrictExpectations() {
			Logger mock;
			Logger mock2;
			Handler h1;
			Handler h2;
			{
				Logger.getLogger(anyString);result=mock;
				mock.getParent();result=mock2;times=1;
				mock.getHandlers();result=new Handler[0];
				mock2.getHandlers();result=new Handler[]{h1,h2};
				h1.setFormatter((JmeFormatter) any); times=1;
				h1.setLevel(Level.ALL);times=1;
				h2.setFormatter((JmeFormatter) any); times=1;
				h2.setLevel(Level.ALL);times=1;
			}
		};
		Thread.currentThread().setContextClassLoader(androidLoader);
		JmeSystem.initialize(new AppSettings(false));
		JmeSystem.initialize(new AppSettings(false));
	}
	
}


