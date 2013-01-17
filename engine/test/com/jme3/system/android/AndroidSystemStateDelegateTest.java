package com.jme3.system.android;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jme3.system.Platform;

public class AndroidSystemStateDelegateTest {
	private AndroidSystemStateDelegate delegate;
	
	@Before
	public void setup(){
		delegate = new AndroidSystemStateDelegate();
	}
	
	/**
	 * Tests retrieving the {@link Platform#Android_ARM5} enum 
	 * value based on the {@link System} "os.arch" property.
	 */
	@Test
	public void getPlatformArmV5Test() {
		new MockArchitecture("armv5");
		Assert.assertEquals("wrong platform v5", Platform.Android_ARM5, delegate.getPlatform());
	}
	
	/**
	 * Tests retrieving the {@link Platform#Android_ARM6} enum 
	 * value based on the {@link System} "os.arch" property.
	 */
	@Test
	public void getPlatformArmV6Test() {
		new MockArchitecture("armv6");
		Assert.assertEquals("wrong platform v6", Platform.Android_ARM6, delegate.getPlatform());
	}

	/**
	 * Tests retrieving the {@link Platform#Android_ARM7} enum 
	 * value based on the {@link System} "os.arch" property.
	 */
	@Test
	public void getPlatformArmV7Test() {
		new MockArchitecture("armv7");
		Assert.assertEquals("wrong platform v7", Platform.Android_ARM7, delegate.getPlatform());
	}
	
	/**
	 * Tests retrieving the {@link Platform} enum value based on the 
	 * {@link System} "os.arch" property when the arch is of unknown ARM type.
	 */
	@Test
	public void getPlatformUnknownArmTest() {
		new MockArchitecture("arm derpyderp");
		Assert.assertEquals("wrong platform unknown should resolve to v5", Platform.Android_ARM5, delegate.getPlatform());
	}
	
	/**
	 * Tests retrieving the {@link Platform#Android_ARM7} enum value 
	 * if the arch is not recgnized as being of the ARM variety	
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void getPlatformUnsupportedAndroidPlatformTest() {
		new MockArchitecture("derpyderp");
		delegate.getPlatform();
	}
	
	private class MockArchitecture extends MockUp<System> {
    	private final String arch;
    	public MockArchitecture(String arch) {
    		this.arch = arch;
    	}
    	@SuppressWarnings("unused")
		@Mock
    	public String getProperty(String name) {
    		if ("os.arch".equals(name)) {
    			return arch; 
    		} else {
    			return System.getProperty(name, null);
    		}
    	}
    }
}
