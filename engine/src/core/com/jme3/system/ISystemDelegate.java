package com.jme3.system;

/**
 * SystemDelegate is the root interface for a subsystem that allows 
 * {@link JmeSystem} to serve as a facade by delegating its 
 * implementation to various non-abstract {@link ISystemDelegate} 
 * implementations.
 * 
 * @author Joey Ezechiels
 */
public interface ISystemDelegate {
	 public void initialize(AppSettings settings);
}
