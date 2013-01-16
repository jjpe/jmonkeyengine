package com.jme3.system;

/**
 * The {@link ISystemStateDelegate} is an interface to some general state of the system.
 * 
 * @author Volker Lanting
 *
 */
public interface ISystemStateDelegate {
	/**
	 * Returns only false at the moment.
	 * @return false.
	 */
	boolean trackDirectMemory();
	/**
	 * Retrieves the Platform of the current system.
	 * @return the {@link Platform} jme is running on.
	 */
	Platform getPlatform();
	/**
	 * Retrieves the full name of this version of the jme sytem.
	 * @return the full name.
	 */
	String getFullName();
}
