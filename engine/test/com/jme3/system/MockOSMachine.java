package com.jme3.system;

import mockit.Mock;
import mockit.MockUp;

/**
 * Enum to mock (operating) system setups.
 * Some of these probably never occur but it's just for testing.
 * 
 * @author Volker Lanting
 *
 */
public enum MockOSMachine {
	WINDOWS_X86("windows", "x86"),
	WINDOWS_X86_64("windows", "x86_64"),
	WINDOWS_POWERPC("windows", "PowerPC"),
	LINUX_I386("linux", "i386"),
	FREEBSD_AMD64("freebsd", "amd64"),
	SUNOS_I686("sunos", "i686"),
	MACOSX_PPC("mac os x", "ppc"),
	MACOSX_PPC64("darwin", "ppc64"),
	MACOSX_UNIVERSAL("mac os x", "universal"),
	MACOSX_AMD64("darwin", "amd64"),
	
	NON_EXISTENT_OS("JMonkeyOS", "amd64"),
	NON_EXISTENT_ARCHITECTURE("windows", "jmonkey processor 13.4")
	;
	
	private final String name;
	private final String arch;
	private MockOSMachine(String name, String architecture) {
		this.name = name;
		this.arch = architecture;
	}
	public MockUp<System> mock() {
		return new MockUp<System>() {
			@SuppressWarnings("unused")
			@Mock
			String getProperty(String prop) {
				if ("os.name".equals(prop)) {
					return name;
				} else if ("os.arch".equals(prop)) {
					return arch;
				}
				return System.getProperty(prop, null);
			}
		};
	}
}
