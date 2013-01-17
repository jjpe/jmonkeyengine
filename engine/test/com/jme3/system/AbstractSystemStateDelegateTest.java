package com.jme3.system;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class AbstractSystemStateDelegateTest {
	MyTestSystemStateDelegate delegate;
	
	@Before
	public void setup(){
		delegate = new MyTestSystemStateDelegate();
	}
	
	/**
	 * Tests if getFullName returns {@link JmeVersion#FULL_NAME}.
	 */
	@Test
	public void testGetFullName() {
		Assert.assertEquals("getFullName no longer refers to JmeVersion.FULL_NAME", 
				JmeVersion.FULL_NAME, delegate.getFullName());
	}
	
	/**
	 * Tests if trackDirectMemory returns false.
	 */
	@Test
	public void testTrackDirectMemory() {
		Assert.assertFalse("trackDirectMemory no longer defaults to false", 
				delegate.trackDirectMemory());
	}

	private class MyTestSystemStateDelegate extends AbstractSystemStateDelegate {
		@Override
		public Platform getPlatform() {return null;}
	}
}
