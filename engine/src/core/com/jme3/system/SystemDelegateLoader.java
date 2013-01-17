package com.jme3.system;



/**
 * Loads {@link ISystemDelegate} instances from the file system.
 * 
 * @author Joey Ezechiels
 *
 * @param <T> The type of the delegate
 */
public final class SystemDelegateLoader<T extends ISystemDelegate> {
    @SuppressWarnings("unchecked")
	public T tryToLoad(String className) 
			throws InstantiationException, IllegalAccessException {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
