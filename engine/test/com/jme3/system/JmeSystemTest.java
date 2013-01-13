package com.jme3.system;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.jme3.input.SoftTextDialogInput;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.system.android.OGLESContext;
import com.jme3.texture.FrameBuffer;

public class JmeSystemTest {

	private static final String[] DELEGATES = new String[]{
		JmeDesktopSystem.class.getName(),
		JmeAndroidSystem.class.getName()
	};

	private static final String NON_EXISTENT_RESOURCE = "MyNonExistentResourceIndicator";
	
	/**
	 * Returns all delegates from {@link #DELEGATES},
	 * except the one that was given.
	 */
	private String[] allDelegatesExcept(Class<? extends JmeSystemDelegate> clazz) {
		ArrayList<String> delegates = new ArrayList<String>();
		for (String delegate : DELEGATES) {
			if (!clazz.getName().equals(delegate)) {
				delegates.add(delegate);
			}
		}
		return delegates.toArray(new String[delegates.size()]);
	}
	
	
	
	/**
	 * Checks if an explicitly defined android JmeSystem behaves the same as
	 * an implicit android JmeSystem, wrt the getPlatform method. 
	 */
	@Test
	public void testAndroidDelegatePlatform() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		JmeSystem.setSystemDelegate(new JmeAndroidSystem());
		testPlatform(JmeAndroidSystem.class);
	}
	
	/**
	 * Checks if an explicitly defined desktop JmeSystem behaves the same as
	 * an implicit desktop JmeSystem, wrt the getPlatform method. 
	 */
	@Test
	public void testDesktopDelegatePlatform() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		JmeSystem.setSystemDelegate(new JmeDesktopSystem());
		testPlatform(JmeDesktopSystem.class);
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegatePlatform() throws Throwable {
		testNoDelegateMethod("getPlatform");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateStorageFolder() throws Throwable {
		testNoDelegateMethod("getStorageFolder");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateFullName() throws Throwable {
		testNoDelegateMethod("getFullName");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateTrackMemory() throws Throwable {
		testNoDelegateMethod("trackDirectMemory");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegatePerms() throws Throwable {
		testNoDelegateMethod("isLowPermissions");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateDialogInput() throws Throwable {
		testNoDelegateMethod("getSoftTextDialogInput");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateAssetManager() throws Throwable {
		testNoDelegateMethod("newAssetManager");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateResourceStream() throws Throwable {
		testNoDelegateMethod("getResourceAsStream", new Param(NON_EXISTENT_RESOURCE, String.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateResource() throws Throwable {
		testNoDelegateMethod("getResource", new Param(NON_EXISTENT_RESOURCE, String.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateSetPerms() throws Throwable {
		testNoDelegateMethod("setLowPermissions", new Param(true, boolean.class));
	}
	
	@Test(expected = NullPointerException.class)
	public void testNoDelegateSetDialogInput() throws Throwable {
		testNoDelegateMethod("setSoftTextDialogInput", new Param(new OGLESContext(), SoftTextDialogInput.class));
	}
	
	// FIXME use mocking instead of null for the Buffer and Stream
	@Test(expected = NullPointerException.class)
	public void testNoDelegateWriteImage() throws Throwable {
		testNoDelegateMethod(
				"writeImageFile", 
				new Param(null, OutputStream.class),
				new Param("jpg", String.class),
				new Param(null, ByteBuffer.class),
				new Param(10, int.class),
				new Param(25, int.class)
				);
	}
	
//	@Test(expected = NullPointerException.class)
//	public void testNoDelegateAssetManagerURL() throws Throwable {
//		testNoDelegateMethod("newAssetManager");
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testNoDelegateSettings() throws Throwable {
//		testNoDelegateMethod("showSettingsDialog");
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testNoDelegateContext() throws Throwable {
//		testNoDelegateMethod("newContext");
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testNoDelegateAudioRenderer() throws Throwable {
//		testNoDelegateMethod("newAudioRenderer");
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testNoDelegateImageRaster() throws Throwable {
//		testNoDelegateMethod("createImageRaster");
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testNoDelegateErrorDialog() throws Throwable {
//		testNoDelegateMethod("showErrorDialog");
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testNoDelegateInit() throws Throwable {
//		testNoDelegateMethod("initialize");
//	}
	
	public void testNoDelegateMethod(String methodName, Param...params) throws Throwable {
		Class<?> system = new MyLoader(DELEGATES).getJmeSystem();
		Class<?>[] paramClasses = new Class[params.length];
		Object[] realParams = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			paramClasses[i] = params[i].requiredClass;
			realParams[i] = params[i].paramObj;
		}
		try {
			system.getMethod(methodName, paramClasses).invoke(null, realParams);
		} catch (InvocationTargetException e) {
			 throw e.getCause();
		}
	}
	
	
	
	
	
	
	
	
	
	
	private void testPlatform(Class<? extends JmeSystemDelegate> clazz)	throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> mySystem = new MyLoader(allDelegatesExcept(clazz)).getJmeSystem();
		Platform p1 = null;
		try {
			p1 = JmeSystem.getPlatform(); 
		} catch (Exception e) {
			try {
				mySystem.getMethod("getPlatform").invoke(null);
			} catch (InvocationTargetException e2) {
				Assert.assertEquals(
						"The exception thrown by the explicit system was not matched by the implicit system.",
						e.getClass(), e2.getCause().getClass());
				return;
			}
			Assert.fail("JmeSystem.getPlatform was unsupported, the testversion didn't do the same.");
		}
		Assert.assertEquals(p1, mySystem.getMethod("getPlatform").invoke(null));
	}
}

/**
 * A custom class loader, 
 * which will not load the classes specified by {@link MyLoader#setNotAllowed(String...)}.
 * 
 * It can be used to test the behavior of JmeSystem by mocking the absense of some JmeDelegates
 * on the classpath.
 * 
 * @author Volker Lanting
 *
 */
class MyLoader extends ClassLoader {
	private String[] notAllowed = new String[0];
	public void setNotAllowed(String... nonAllowedClassNames) {
		notAllowed = nonAllowedClassNames;
	}
	public String[] getNotAllowed() {
		return notAllowed;
	}
	
	public MyLoader(){}
	public MyLoader(String... notAllowed) {setNotAllowed(notAllowed);}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		for (String s : notAllowed) {
			if (name.equals(s)) {
				throw new ClassNotFoundException("Loading this class is not allowed for testing purposes.");
			}
		}
		return super.loadClass(name);
	}
	
	/**
	 * Returns a new Class definition of JmeSystem.
	 * Note that it is no ordinary JmeSystem, so you can not cast to one.
	 * @return a new JmeSystem, with this ClassLoader as ClassLoader.
	 */
	public Class<?> getJmeSystem() {
		String name = JmeSystem.class.getName();
		String simpleName = JmeSystem.class.getSimpleName();
		try {
			Class<?> c = super.loadClass(name);
			URL classFile = c.getResource(simpleName+".class");
			FileInputStream fis = new FileInputStream(new File(classFile.toURI()));
			byte[] classContents = new byte[fis.available()];
			fis.read(classContents);
			fis.close();
			return defineClass(name, classContents, 0, classContents.length);
		} catch (FileNotFoundException e) {
			Assert.fail("Couldn't locate the classfile for JmeSystem: "+e.getMessage());
		} catch (URISyntaxException e) {
			Assert.fail("URL was not a proper URI: "+e.getMessage());
		} catch (ClassNotFoundException e) {
			Assert.fail("The JmeSystem class could not be found: "+e.getMessage());
		} catch (IOException e) {
			Assert.fail("Some IO error while reading the class file: "+e.getMessage());
		}
		
		return null;
	}
}

/**
 * A Param is just a way to represent a parameter of a method.
 *
 */
class Param {
	public final Object paramObj;
	public final Class<?> requiredClass;
	public Param(Object param, Class<?> clazz) {
		paramObj = param;
		requiredClass = clazz;
	}
}