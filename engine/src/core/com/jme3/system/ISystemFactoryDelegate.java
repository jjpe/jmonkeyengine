package com.jme3.system;

import java.net.URL;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.system.JmeContext.Type;

/**
 * {@link ISystemFactoryDelegate} is an interface to some factory methods
 * for objects whose creation involves system specific configuration.
 *  
 * @author Volker Lanting
 *
 */
public interface ISystemFactoryDelegate {
	/**
	 * Creates a new {@link JmeContext} based on the Renderer in the settings
	 * and the given context Type. 
	 * @param settings the settings with an appropriate Renderer.
	 * @param type the {@link Type} of context to retrieve.
	 * @return the {@link JmeContext}. 
	 */
	JmeContext newContext(AppSettings settings, Type type);
	/**
	 * Creates a new {@link AssetManager} to manage the systems assets.
	 * @return the AssetManager.
	 */
	AssetManager newAssetManager();
	/**
	 * Creates a new {@link AssetManager} to manage the systems assets.
	 * The configuration for the manager is loaded from the given config file.
	 * @param configFile the configuration file to load from.
	 * @return the {@link AssetManager}.
	 */
	AssetManager newAssetManager(URL configFile);
	/**
	 * Creates a new {@link AudioRenderer} based on the settings' audio renderer.
	 * @param setttings the settings which specify what (kind of) audio renderer to use.
	 * @return the {@link AudioRenderer}.
	 */
	AudioRenderer newAudioRenderer(AppSettings setttings);
	/**
	 * Initializes this delegate.
	 * @param settings the settings required for initialization.
	 */
	void initialize(AppSettings settings);
}
