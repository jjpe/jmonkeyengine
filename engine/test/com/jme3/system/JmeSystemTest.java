package com.jme3.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Test;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.SoftTextDialogInput;
import com.jme3.system.android.JmeAndroidSystem;
import com.jme3.texture.Image;
import com.jme3.texture.image.ImageRaster;

public class JmeSystemTest {

	private static final String[] DELEGATES = new String[]{
		JmeDesktopSystem.class.getName(),
		JmeAndroidSystem.class.getName()
	};

	
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
	 * Checks if a JmeSystem with no delegate fails on its static, parameterless methods.
	 */
	@Test
	public void testNoDelegateFailures() throws Throwable {
		new MockUp<Logger>() {
			@SuppressWarnings("unused")
			@Mock
			public void log(Level level, String message) {
				Assert.assertEquals("The jmeSystem should have logged the absense of a delegate", 
						"Failed to find a JmeSystem delegate!\n"
                                + "Ensure either desktop or android jME3 jar is in the classpath.",
                        message
						);
				Assert.assertEquals("The JmeSystem should have logged the absense of a delegate with SEVERE level",
						Level.SEVERE, level);
				
			}
		};
		Class<?> system = new MyLoader(DELEGATES).getJmeSystem();
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
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			Platform getPlatform(){return null;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"getPlatform");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getStorageFolder is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetStorageFolder() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			File getStorageFolder(){return null;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"getStorageFolder");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getFullName is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetFullName() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			String getFullName(){return null;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"getFullName");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getResourceAsStream is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetResourceAsStream() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			InputStream getResourceAsStream(String a){return null;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"getResourceAsStream", new Class<?>[]{String.class}, new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and getResource is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetResource() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			URL getResource(String a){return null;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"getResource", new Class<?>[]{String.class}, new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and trackDirectMemory is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnTrackDirectmemory() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			boolean trackDirectMemory(){return true;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"trackDirectMemory");
	}

	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate and setLowPermissions is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnSetLowPermissions() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void setLowPermissions(boolean a){}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"setLowPermissions",new Class<?>[]{boolean.class},new Object[]{true});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and isLowPermissions is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnIsLowPermissions() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			boolean isLowPermissions(){return true;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"isLowPermissions");
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and {@link JmeAndroidSystem#setSoftTextDialogInput(SoftTextDialogInput)} is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnSetSoftTextDialogInput() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			void setSoftTextDialogInput(SoftTextDialogInput a){}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"setSoftTextDialogInput",new Class<?>[]{SoftTextDialogInput.class},new Object[]{null});
	}
	
	/**
	 * Checks if the Android delegate is implicitly loaded if it is the only
	 * available delegate 
	 * and getSoftTextDialogInput is called.
	 */
	@Test
	public void testImplictlyLoadAndroidOnGetSoftTextDialogInput() throws Throwable {
		new MockUp<JmeAndroidSystem>(){
			@SuppressWarnings("unused")
			@Mock(invocations=1)
			SoftTextDialogInput getSoftTextDialogInput(){return null;}
		};
		Class<?> system = new MyLoader(allDelegatesExcept(JmeAndroidSystem.class)).getJmeSystem();
		invokeStatic(system,"getSoftTextDialogInput");
	}
	
	class MockAndroidSystem extends MockUp<JmeAndroidSystem> {
//		public void writeImageFile(OutputStream a,String b,ByteBuffer c,int d,int e){}
//		AssetManager newAssetManager(URL a){return null;}
//		AssetManager newAssetManager(){return null;}
//		boolean showSettingsDialog(AppSettings a, boolean b){return true;}
//		JmeContext newContext(AppSettings a, JmeContext.Type b){return null;}
//		AudioRenderer newAudioRenderer(AppSettings a){return null;}
//		void initialize(AppSettings a){}
//		ImageRaster createImageRaster(Image a, int b){return null;}
//		void showErrorDialog(String a){}
		
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
		try {
			Class<?> c = super.loadClass(JmeSystem.class.getName());
			URL classFile = c.getResource(JmeSystem.class.getSimpleName()+".class");
			FileInputStream fis = new FileInputStream(new File(classFile.toURI()));
			byte[] classContents = new byte[fis.available()];
			fis.read(classContents);
			fis.close();
			return defineClass(JmeSystem.class.getName(), classContents, 0, classContents.length);
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
