package com.jme3.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Test;

import com.jme3.system.android.JmeAndroidSystem;

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
