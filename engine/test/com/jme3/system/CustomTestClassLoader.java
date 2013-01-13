package com.jme3.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;

/**
 * A custom class loader, 
 * which will not load the classes specified by {@link MyLoader#setNotAllowed(String...)}.
 * 
 * It can be used to test the behavior of JmeSystem by mocking the absence of some JmeDelegates
 * on the classpath.
 * 
 * @author Volker Lanting
 *
 */
public class CustomTestClassLoader extends ClassLoader {
		private String[] notAllowed = new String[0];
		public void setNotAllowed(String... nonAllowedClassNames) {
			notAllowed = nonAllowedClassNames;
		}
		public String[] getNotAllowed() {
			return notAllowed;
		}
		
		public CustomTestClassLoader(String... notAllowed) {setNotAllowed(notAllowed);}
		
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
		public Class<?> customLoadClass(Class<?> clazz) {
			try {
				URL classFile = clazz.getResource(clazz.getSimpleName()+".class");
				FileInputStream fis = new FileInputStream(new File(classFile.toURI()));
				byte[] classContents = new byte[fis.available()];
				fis.read(classContents);
				fis.close();
				return defineClass(clazz.getName(), classContents, 0, classContents.length);
			} catch (FileNotFoundException e) {
				Assert.fail("Couldn't locate the classfile: "+e.getMessage());
			} catch (URISyntaxException e) {
				Assert.fail("URL was not a proper URI: "+e.getMessage());
			} catch (IOException e) {
				Assert.fail("Some IO error while reading the class file: "+e.getMessage());
			}
			
			return null;
		}
}
