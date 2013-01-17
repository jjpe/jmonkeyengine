package com.jme3.system;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class DesktopSystemStateDelegateTest {
	private DesktopSystemStateDelegate delegate;
	
	@Before
	public void setup(){
		delegate = new DesktopSystemStateDelegate();
	}
	
	/**
	 * Tests the getPlatform method for several (fictive) machine setups 
	 */
	@Test
	public void testGetPlatform() {
		MockOSMachine.LINUX_I386.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Linux i386",
				Platform.Linux32, delegate.getPlatform());
		MockOSMachine.FREEBSD_AMD64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: FreeBsd amd64",
				Platform.Linux64, delegate.getPlatform());
		MockOSMachine.SUNOS_I686.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: SunOs i686",
				Platform.Linux32, delegate.getPlatform());
		
		MockOSMachine.MACOSX_UNIVERSAL.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac universal",
				Platform.MacOSX32, delegate.getPlatform());
		MockOSMachine.MACOSX_AMD64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac amd64",
				Platform.MacOSX64, delegate.getPlatform());
		MockOSMachine.MACOSX_PPC.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac ppc",
				Platform.MacOSX_PPC32, delegate.getPlatform());
		MockOSMachine.MACOSX_PPC64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Mac ppc64",
				Platform.MacOSX_PPC64, delegate.getPlatform());
		
		MockOSMachine.WINDOWS_X86.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Windows x86",
				Platform.Windows32, delegate.getPlatform());
		MockOSMachine.WINDOWS_X86_64.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Windows x86_64",
				Platform.Windows64, delegate.getPlatform());
	}
	
	/**
	 * Tests a case which should probably work, 
	 * but fails due to an equals check with uppercase letters
	 * on a string which is always lowercased.
	 * See the private method JmeSystemDelegate.is64Bit at "PowerPC",
	 * which is called on System.getProperty("os.arch").toLowerCase
	 * in {@link JmeSystemDelegate#getPlatform()}. 
	 */
	@Test
	public void behaviourTestGetPlatform() {
		MockOSMachine.WINDOWS_POWERPC.mock();
		Assert.assertEquals("did not get the proper platform for machine setup: Windows PowerPC",
				Platform.Windows32, delegate.getPlatform());
	}
	
	/**
	 * Tests getPlatform with a non supported (or existent) operating system.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testGetPlatformWithUnsupportedOS() {
		MockOSMachine.NON_EXISTENT_OS.mock();
		delegate.getPlatform();
	}
	
	/**
	 * Tests getPlatform with a non supported (or existent) architecture.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testGetPlatformWithUnsupportedArchitecture() {
		MockOSMachine.NON_EXISTENT_ARCHITECTURE.mock();
		delegate.getPlatform();
	}
}
